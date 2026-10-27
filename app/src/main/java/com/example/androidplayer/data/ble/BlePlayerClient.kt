package com.example.androidplayer.data.ble

import android.annotation.SuppressLint
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import com.example.androidplayer.data.model.CanStatistics
import com.example.androidplayer.data.model.ComponentLogSettings
import com.example.androidplayer.data.model.SleepSettings
import com.example.androidplayer.data.model.SystemInfo
import com.example.androidplayer.data.model.WifiSettings
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import androidx.core.content.ContextCompat
import timber.log.Timber
import java.util.UUID
import java.util.ArrayDeque

/**
 * BLE Player клієнт для управління ESP32 плеєром по Bluetooth LE
 */
class BlePlayerClient(context: Context) {
    private val context = context
    private val preferences = context.applicationContext.getSharedPreferences("ble_player", Context.MODE_PRIVATE)
    private val reconnectHandler = Handler(Looper.getMainLooper())
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private val scanner: BluetoothLeScanner? = bluetoothAdapter?.bluetoothLeScanner
    private var bluetoothGatt: BluetoothGatt? = null
    private var connectedDevice: BluetoothDevice? = null

    // UUIDs для сервісу
    private val PLAYER_SERVICE_UUID = UUID.fromString("11000000-0000-1000-8000-00805f9b34fb")
    private val COMMAND_CHAR_UUID = UUID.fromString("11010100-0000-1000-8000-00805f9b34fb")
    private val STATUS_CHAR_UUID = UUID.fromString("11010200-0000-1000-8000-00805f9b34fb")
    private val PLAYLIST_CHAR_UUID = UUID.fromString("11010300-0000-1000-8000-00805f9b34fb")
    private val LOGS_CHAR_UUID = UUID.fromString("11010400-0000-1000-8000-00805f9b34fb")
    private val CCCD_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices

    private var commandCharacteristic: BluetoothGattCharacteristic? = null
    private var statusCharacteristic: BluetoothGattCharacteristic? = null
    private var playlistCharacteristic: BluetoothGattCharacteristic? = null
    private var logsCharacteristic: BluetoothGattCharacteristic? = null
    private val pendingNotificationDescriptors = ArrayDeque<BluetoothGattDescriptor>()
    private val commandWriteLock = Any()
    private val pendingCommandWrites = ArrayDeque<ByteArray>()
    private var commandWriteInProgress = false
    private val rpcMutex = Mutex()
    @Volatile private var pendingRpcResponse: CompletableDeferred<String>? = null

    private val _statusData = MutableStateFlow<com.example.androidplayer.data.model.PlayerStatus?>(null)
    val statusData: StateFlow<com.example.androidplayer.data.model.PlayerStatus?> = _statusData

    private val _playlistData = MutableStateFlow<List<String>>(emptyList())
    val playlistData: StateFlow<List<String>> = _playlistData

    private val _notificationsReady = MutableStateFlow(false)
    val notificationsReady: StateFlow<Boolean> = _notificationsReady

    private val _serviceReady = MutableStateFlow(false)
    val serviceReady: StateFlow<Boolean> = _serviceReady
    private val _serviceError = MutableStateFlow<String?>(null)
    val serviceError: StateFlow<String?> = _serviceError
    @Volatile private var explicitlyDisconnected = false

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device ?: return
            val list = _discoveredDevices.value
            if (list.none { it.address == device.address }) {
                _discoveredDevices.value = list + device
                Timber.d("🔍 Found ${device.name ?: "(no name)"} ${device.address}")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Timber.e("❌ BLE scan failed: $errorCode")
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    Timber.d("✅ BLE Connected")
                    explicitlyDisconnected = false
                    _isConnected.value = true
                    _serviceReady.value = false
                    _serviceError.value = null
                    _notificationsReady.value = false
                    if (!gatt.requestMtu(512)) {
                        gatt.discoverServices()
                    }
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    Timber.d("🔌 BLE Disconnected")
                    _isConnected.value = false
                    _serviceReady.value = false
                    _serviceError.value = null
                    _notificationsReady.value = false
                    pendingNotificationDescriptors.clear()
                    synchronized(commandWriteLock) {
                        pendingCommandWrites.clear()
                        commandWriteInProgress = false
                    }
                    pendingRpcResponse?.completeExceptionally(IllegalStateException("BLE disconnected"))
                    pendingRpcResponse = null
                    gatt.close()
                    if (bluetoothGatt === gatt) bluetoothGatt = null
                    if (!explicitlyDisconnected && preferences.getString("device_address", null) != null) {
                        reconnectHandler.postDelayed({ connectRememberedDevice() }, 1200)
                    }
                }
            }
        }

        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            Timber.d("BLE MTU negotiated: mtu=$mtu status=$status")
            gatt.discoverServices()
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val service = gatt.getService(PLAYER_SERVICE_UUID)
                if (service != null) {
                    commandCharacteristic = service.getCharacteristic(COMMAND_CHAR_UUID)
                    statusCharacteristic = service.getCharacteristic(STATUS_CHAR_UUID)
                    playlistCharacteristic = service.getCharacteristic(PLAYLIST_CHAR_UUID)
                    logsCharacteristic = service.getCharacteristic(LOGS_CHAR_UUID)
                    _notificationsReady.value = false
                    
                    Timber.d("✅ Player Service discovered")
                    Timber.d("📋 Characteristics found:")
                    Timber.d("  - Command: ${commandCharacteristic != null}")
                    Timber.d("  - Status: ${statusCharacteristic != null}")
                    Timber.d("  - Playlist: ${playlistCharacteristic != null}")
                    if (commandCharacteristic == null || statusCharacteristic == null ||
                        playlistCharacteristic == null || logsCharacteristic == null) {
                        Timber.e("❌ Required player characteristics are missing")
                        _serviceError.value = "Player GATT characteristics not found; update ESP32 firmware"
                        _serviceReady.value = false
                        return
                    }

                    _serviceError.value = null
                    _serviceReady.value = true

                    pendingNotificationDescriptors.clear()
                    listOf(statusCharacteristic!!, playlistCharacteristic!!, logsCharacteristic!!).forEach { characteristic ->
                        if (!gatt.setCharacteristicNotification(characteristic, true)) {
                            Timber.e("❌ Could not enable notification for ${characteristic.uuid}")
                            return
                        }
                        val descriptor = characteristic.getDescriptor(CCCD_UUID)
                        if (descriptor == null) {
                            Timber.e("❌ CCCD missing for ${characteristic.uuid}")
                            return
                        }
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        pendingNotificationDescriptors.addLast(descriptor)
                    }
                    writeNextNotificationDescriptor(gatt)
                } else {
                    _serviceError.value = "Player GATT service not found; update ESP32 firmware"
                    Timber.w("⚠️ Player Service not found")
                }
            } else {
                _serviceError.value = "GATT service discovery failed: $status"
            }
        }

        override fun onDescriptorWrite(gatt: BluetoothGatt, descriptor: BluetoothGattDescriptor, status: Int) {
            super.onDescriptorWrite(gatt, descriptor, status)
            if (status != BluetoothGatt.GATT_SUCCESS) {
                Timber.e("❌ CCCD write failed for ${descriptor.characteristic.uuid}: $status")
                pendingNotificationDescriptors.clear()
                _notificationsReady.value = false
                _serviceError.value = "BLE notifications failed: $status"
                return
            }
            writeNextNotificationDescriptor(gatt)
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            super.onCharacteristicWrite(gatt, characteristic, status)
            
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Timber.d("✅ Command sent successfully")
            } else {
                Timber.e("❌ Write characteristic failed: $status")
            }
            synchronized(commandWriteLock) {
                commandWriteInProgress = false
            }
            startNextCommandWrite()
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            super.onCharacteristicChanged(gatt, characteristic)
            
            val data = characteristic.value?.let { String(it) } ?: ""
            Timber.d("📡 Characteristic changed: UUID=${characteristic.uuid}, data='$data'")
            
            when (characteristic.uuid) {
                STATUS_CHAR_UUID -> {
                    Timber.d("📊 Status received: $data")
                    // Parse status data format: "track,pos,duration,paused,playlistSize"
                    try {
                        val parts = data.split(",")
                        if (parts.size >= 5) {
                            _statusData.value = com.example.androidplayer.data.model.PlayerStatus(
                                currentTrack = parts[0].toIntOrNull() ?: 0,
                                positionMs = parts[1].toIntOrNull() ?: 0,
                                durationMs = parts[2].toIntOrNull() ?: 0,
                                isPaused = parts[3].toBoolean(),
                                playlistSize = parts[4].toIntOrNull() ?: 0,
                                mode = parts.getOrNull(5) ?: "USB",
                                snifferEnabled = parts.getOrNull(6)?.toIntOrNull() == 1
                            )
                            Timber.d("✅ Status parsed: track=${parts[0]}, pos=${parts[1]}ms, duration=${parts[2]}ms")
                        } else {
                            Timber.w("⚠️ Status data incomplete: ${parts.size} parts")
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to parse status data")
                    }
                }
                PLAYLIST_CHAR_UUID -> {
                    Timber.d("📝 Playlist received: $data")
                    try {
                        _playlistData.value = data.split("|").filter { it.isNotEmpty() }
                        Timber.d("✅ Playlist parsed: ${_playlistData.value.size} tracks")
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to parse playlist data")
                    }
                }
                LOGS_CHAR_UUID -> pendingRpcResponse?.complete(data)
                else -> {
                    Timber.d("ℹ️ Received data from unknown characteristic: ${characteristic.uuid}")
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun writeNextNotificationDescriptor(gatt: BluetoothGatt) {
        val descriptor = pendingNotificationDescriptors.pollFirst()
        if (descriptor == null) {
            _notificationsReady.value = true
            Timber.d("✅ BLE notifications enabled")
            return
        }
        if (!gatt.writeDescriptor(descriptor)) {
            pendingNotificationDescriptors.clear()
            _notificationsReady.value = false
            _serviceError.value = "Could not enable BLE notifications"
            Timber.e("❌ Could not write CCCD for ${descriptor.characteristic.uuid}")
        }
    }

    private suspend fun rpc(operation: String, payload: String? = null): String = rpcMutex.withLock {
        val response = CompletableDeferred<String>()
        pendingRpcResponse = response
        val request = if (payload == null) operation else "$operation:$payload"
        if (!sendCommand("rpc", request)) {
            pendingRpcResponse = null
            throw IllegalStateException("BLE request could not be sent: $operation")
        }

        try {
            withTimeout(5000) { response.await() }
        } finally {
            if (pendingRpcResponse === response) {
                pendingRpcResponse = null
            }
        }
    }

    suspend fun getWifiSettings(): WifiSettings {
        val json = JSONObject(rpc("wifi.get"))
        return WifiSettings(
            staSsid = json.optString("sta_ssid"),
            staPassword = "",
            apSsid = json.optString("ap_ssid"),
            apPassword = "",
            mode = json.optInt("mode")
        )
    }

    suspend fun setWifiSettings(settings: WifiSettings): String {
        val json = JSONObject()
            .put("sta_ssid", settings.staSsid)
            .put("sta_password", settings.staPassword)
            .put("ap_ssid", settings.apSsid)
            .put("ap_password", settings.apPassword)
            .put("mode", settings.mode)
        val result = JSONObject(rpc("wifi.set", json.toString()))
        if (!result.optBoolean("success")) {
            throw IllegalStateException(result.optString("error", "Wi-Fi settings were rejected"))
        }
        return "OK"
    }

    suspend fun getSleepSettings(): SleepSettings {
        val json = JSONObject(rpc("sleep.get"))
        return SleepSettings(json.optInt("sleep_mode"), json.optInt("idle_timeout_sec", 30))
    }

    suspend fun setSleepSettings(settings: SleepSettings): String {
        val payload = JSONObject()
            .put("sleep_mode", settings.sleepMode)
            .put("idle_timeout_sec", settings.idleTimeoutSec)
            .toString()
        val result = JSONObject(rpc("sleep.set", payload))
        if (!result.optBoolean("success")) {
            throw IllegalStateException(result.optString("error", "Sleep settings were rejected"))
        }
        return "OK"
    }

    suspend fun getLogLevel(): String = rpc("log_level.get")

    suspend fun setLogLevel(level: String): String = rpc("log_level.set", level)

    suspend fun getVerboseLogs(): Boolean = rpc("verbose.get") == "1"

    suspend fun setVerboseLogs(enabled: Boolean): String = rpc("verbose.set", if (enabled) "1" else "0")

    suspend fun getComponentLogs(): ComponentLogSettings {
        val json = JSONObject(rpc("component_logs.get"))
        return ComponentLogSettings(
            player = json.optInt("player", 1),
            wifi_can_sniffer = json.optInt("wifi_can_sniffer", 1),
            web_ota = json.optInt("web_ota", 1),
            uart_ota = json.optInt("uart_ota", 1),
            elm327 = json.optInt("elm327", 1)
        )
    }

    suspend fun setComponentLogs(settings: ComponentLogSettings): String {
        val payload = JSONObject()
            .put("player", settings.player)
            .put("wifi_can_sniffer", settings.wifi_can_sniffer)
            .put("web_ota", settings.web_ota)
            .put("uart_ota", settings.uart_ota)
            .put("elm327", settings.elm327)
            .toString()
        val result = JSONObject(rpc("component_logs.set", payload))
        if (!result.optBoolean("success")) {
            throw IllegalStateException(result.optString("error", "Log settings were rejected"))
        }
        return "OK"
    }

    suspend fun getSystemLogs(): String = rpc("logs.get")

    suspend fun getSystemInfo(): SystemInfo {
        val json = JSONObject(rpc("system_info.get"))
        return SystemInfo(
            freeHeap = json.optInt("free_heap"),
            totalHeap = json.optInt("total_heap"),
            freePsram = json.optInt("free_psram"),
            totalPsram = json.optInt("total_psram"),
            uptimeMs = json.optLong("uptime_ms"),
            firmwareVersion = json.optString("firmware_version"),
            buildDate = json.optString("build_date"),
            buildTime = json.optString("build_time")
        )
    }

    suspend fun getCanStatistics(): CanStatistics {
        val json = JSONObject(rpc("can_statistics.get"))
        return CanStatistics(
            framesPerSecond = json.optInt("frames_per_second"),
            bytesPerSecond = json.optInt("bytes_per_second"),
            totalFrames = json.optLong("total_frames"),
            totalBytes = json.optLong("total_bytes"),
            bitrate = json.optInt("bitrate")
        )
    }

    suspend fun getVersionInfo(): com.example.androidplayer.data.model.VersionInfo {
        val json = JSONObject(rpc("version.get"))
        return com.example.androidplayer.data.model.VersionInfo(
            player_build_date = json.optString("player_build_date"),
            player_build_time = json.optString("player_build_time"),
            web_build_date = json.optString("web_build_date"),
            web_build_time = json.optString("web_build_time"),
            uart_build_date = json.optString("uart_build_date"),
            uart_build_time = json.optString("uart_build_time"),
            slave_main_build_date = json.optString("slave_main_build_date"),
            slave_main_build_time = json.optString("slave_main_build_time"),
            slave_crc_build_date = json.optString("slave_crc_build_date"),
            slave_crc_build_time = json.optString("slave_crc_build_time"),
            slave_uart_build_date = json.optString("slave_uart_build_date"),
            slave_uart_build_time = json.optString("slave_uart_build_time")
        )
    }

    suspend fun restartDevice(): String = rpc("device.restart")

    suspend fun toggleSniffer(enabled: Boolean): String {
        val result = JSONObject(rpc("sniffer.set", if (enabled) "1" else "0"))
        if (result.optBoolean("enabled") != enabled) {
            throw IllegalStateException("Could not change CAN sniffer state")
        }
        return "OK"
    }

    /**
     * Сканування BLE пристроїв
     */
    @SuppressLint("MissingPermission")
    fun startScan() {
        Timber.d("🔍 Starting BLE scan (no filter)")
        _discoveredDevices.value = emptyList()

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        scanner?.startScan(null, settings, scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        scanner?.stopScan(scanCallback)
    }

    /**
     * Підключення до пристрою
     */
    @SuppressLint("MissingPermission")
    fun connect(device: BluetoothDevice): Boolean {
        Timber.d("🔗 Connecting to ${device.name} (${device.address})")
        stopScan()
        explicitlyDisconnected = false
        preferences.edit().putString("device_address", device.address).apply()
        _serviceReady.value = false
        _notificationsReady.value = false
        
        bluetoothGatt?.close()
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
        connectedDevice = device
        
        return bluetoothGatt != null
    }

    @SuppressLint("MissingPermission")
    fun connectRememberedDevice(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Timber.w("BLE reconnect skipped: BLUETOOTH_CONNECT permission is missing")
            return false
        }
        val address = preferences.getString("device_address", null) ?: return false
        if (bluetoothGatt != null && (_isConnected.value || bluetoothGatt?.device?.address == address)) {
            return true
        }
        val device = try {
            bluetoothAdapter?.getRemoteDevice(address)
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Saved BLE address is invalid")
            null
        } ?: return false
        return connect(device)
    }

    fun hasRememberedDevice(): Boolean = preferences.getString("device_address", null) != null

    /**
     * Від'єднання
     */
    @SuppressLint("MissingPermission")
    fun disconnect() {
        explicitlyDisconnected = true
        reconnectHandler.removeCallbacksAndMessages(null)
        preferences.edit().remove("device_address").apply()
        Timber.d("🔌 Disconnecting")
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        _isConnected.value = false
        _serviceReady.value = false
        _notificationsReady.value = false
        pendingRpcResponse?.completeExceptionally(IllegalStateException("BLE disconnected"))
        pendingRpcResponse = null
    }

    /**
     * Відправка команди
     */
    @SuppressLint("MissingPermission")
    fun sendCommand(command: String, params: String = ""): Boolean {
        if (!_isConnected.value || commandCharacteristic == null) {
            Timber.w("❌ BLE not connected or characteristic not found")
            Timber.w("   isConnected=${_isConnected.value}, commandCharacteristic=${commandCharacteristic != null}")
            return false
        }
        Timber.d("📤 Sending BLE command: $command${if (params.isEmpty()) "" else ":$params"}")

        val fullCommand = if (params.isEmpty()) command else "$command:$params"
        synchronized(commandWriteLock) {
            pendingCommandWrites.addLast(fullCommand.toByteArray(Charsets.UTF_8))
        }
        startNextCommandWrite()
        return true
    }

    @SuppressLint("MissingPermission")
    private fun startNextCommandWrite() {
        val commandBytes = synchronized(commandWriteLock) {
            if (commandWriteInProgress) return
            val next = pendingCommandWrites.pollFirst() ?: return
            commandWriteInProgress = true
            next
        }

        val gatt = bluetoothGatt
        val characteristic = commandCharacteristic
        if (gatt == null || characteristic == null) {
            synchronized(commandWriteLock) { commandWriteInProgress = false }
            return
        }
        characteristic.value = commandBytes
        characteristic.writeType = WRITE_TYPE_DEFAULT
        if (!gatt.writeCharacteristic(characteristic)) {
            Timber.e("❌ Could not start BLE command write")
            synchronized(commandWriteLock) { commandWriteInProgress = false }
            startNextCommandWrite()
        }
    }

    // Команди плеєра
    fun play(trackIndex: Int): String {
        Timber.d("▶️ BLE: Play track $trackIndex")
        return if (sendCommand("play", trackIndex.toString())) "OK" else "FAILED"
    }
    
    fun pause(): String {
        Timber.d("⏸️ BLE: Pause")
        return if (sendCommand("pause")) "OK" else "FAILED"
    }
    
    fun resume(): String {
        Timber.d("▶️ BLE: Resume")
        return if (sendCommand("play")) "OK" else "FAILED"
    }
    
    fun next(): String {
        Timber.d("⏭️ BLE: Next track")
        return if (sendCommand("next")) "OK" else "FAILED"
    }
    
    fun previous(): String {
        Timber.d("⏮️ BLE: Previous track")
        return if (sendCommand("prev")) "OK" else "FAILED"
    }
    
    fun seek(positionMs: Int): String {
        Timber.d("⏱️ BLE: Seek to $positionMs ms")
        return if (sendCommand("seek", positionMs.toString())) "OK" else "FAILED"
    }
    
    suspend fun getStatus(): com.example.androidplayer.data.model.PlayerStatus {
        _statusData.value = null  // очистити старі дані
        sendCommand("status")
        
        // Чекати до 2 секунд на відповідь від ESP32
        repeat(20) {
            if (_statusData.value != null) {
                return _statusData.value!!
            }
            delay(100)
        }
        
        Timber.w("⚠️ Status timeout, returning default")
        return com.example.androidplayer.data.model.PlayerStatus(
            currentTrack = 0,
            positionMs = 0,
            durationMs = 0,
            isPaused = false,
            playlistSize = 0,
            mode = "BLE",
            snifferEnabled = false
        )
    }
    
    suspend fun getPlaylist(): List<String> {
        _playlistData.value = emptyList()  // очистити старі дані
        sendCommand("playlist")
        
        // Чекати до 2 секунд на відповідь від ESP32
        repeat(20) {
            if (_playlistData.value.isNotEmpty()) {
                return _playlistData.value
            }
            delay(100)
        }
        
        Timber.w("⚠️ Playlist timeout, returning empty")
        return emptyList()
    }
    
}
