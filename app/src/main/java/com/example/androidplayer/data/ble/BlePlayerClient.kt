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
import android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE
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
import android.util.Base64
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
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.json.JSONObject
import androidx.core.content.ContextCompat
import timber.log.Timber
import java.util.UUID
import java.util.ArrayDeque
import com.example.androidplayer.ui.i18n.tr

/**
 * BLE Player клієнт для управління ESP32 плеєром по Bluetooth LE
 */
class BlePlayerClient(context: Context) {
    private val context = context
    private val preferences = context.applicationContext.getSharedPreferences("ble_player", Context.MODE_PRIVATE)
    private val reconnectHandler = Handler(Looper.getMainLooper())
    private val reconnectWatchdog = object : Runnable {
        override fun run() {
            if (!explicitlyDisconnected && !manualReconnectInProgress &&
                !_isConnected.value && hasRememberedDevice()
            ) {
                Timber.d("BLE watchdog: reconnecting...")
                connectRememberedDevice()
            }
            reconnectHandler.postDelayed(this, 4000)
        }
    }
    init {
        reconnectHandler.postDelayed(reconnectWatchdog, 4000)
    }
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
    private val pendingOtaWrites = ArrayDeque<ByteArray>()
    private val otaWriteLock = Any()
    /** Guards the single GATT characteristic: OTA frames and commands must not interleave. */
    private val writeArbiter = Any()
    private var otaWriteInProgress = false
    @Volatile private var negotiatedMtu = 23
    private val mainHandler = android.os.Handler(android.os.Looper.getMainLooper())
    private var commandWriteInProgress = false
    private val rpcMutex = Mutex()
    @Volatile private var pendingRpcResponse: CompletableDeferred<String>? = null
    /** Last rejection reason reported by the device for `file.data`. */
    @Volatile var lastFileOtaError: String = ""

    /** True when the device received fewer bytes than were sent (ATT MTU mismatch). */
    @Volatile var lastFileOtaTruncated: Boolean = false

    /**
     * Largest command the device actually received. Learned from rejections: if a
     * 361-byte request reaches the device as 244 bytes, only 244 can be used.
     */
    @Volatile private var observedCommandBytes: Int = 0

    @Volatile private var servicesDiscovered = false
    @Volatile private var manualReconnectInProgress = false

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
                    servicesDiscovered = false
                    negotiatedMtu = 23
                    observedCommandBytes = 0
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
                    servicesDiscovered = false
                    negotiatedMtu = 23
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
            negotiatedMtu = mtu
            Timber.d("BLE MTU negotiated: mtu=$mtu status=$status")
            // Re-discovering services tears down CCCD subscriptions, which would
            // break an update in progress. Only discover once per connection.
            if (!servicesDiscovered) {
                gatt.discoverServices()
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            super.onServicesDiscovered(gatt, status)
            
            if (status == BluetoothGatt.GATT_SUCCESS) {
                servicesDiscovered = true
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
            if (otaWriteInProgress) {
                synchronized(otaWriteLock) { otaWriteInProgress = false }
                startNextOtaWrite()
                return
            }
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

        val text = try {
            withTimeout(5000) { response.await() }
        } catch (e: TimeoutCancellationException) {
            Timber.w("BLE RPC timeout: $operation")
            throw IllegalStateException(
                "BLE response timeout for '$operation' (device did not answer in 5s)"
            )
        } finally {
            if (pendingRpcResponse === response) {
                pendingRpcResponse = null
            }
        }
        text
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
            elm327 = json.optInt("elm327", 1),
            pipeline = json.optInt("pipeline", 1),
            can = json.optInt("can", 1),
            usb = json.optInt("usb", 1),
            ble = json.optInt("ble", 1),
            wifi = json.optInt("wifi", 1),
            sleep = json.optInt("sleep", 1),
            playlist = json.optInt("playlist", 1),
            console = json.optInt("console", 1),
            link = json.optInt("link", 1),
            nvs = json.optInt("nvs", 1),
            group_player = json.optInt("group_player", 1),
            group_connect = json.optInt("group_connect", 1),
            group_ota = json.optInt("group_ota", 1),
            group_car = json.optInt("group_car", 1),
            group_system = json.optInt("group_system", 1)
        )
    }

    suspend fun setComponentLogs(settings: ComponentLogSettings): String {
        val payload = JSONObject()
            .put("player", settings.player)
            .put("wifi_can_sniffer", settings.wifi_can_sniffer)
            .put("web_ota", settings.web_ota)
            .put("uart_ota", settings.uart_ota)
            .put("elm327", settings.elm327)
            .put("pipeline", settings.pipeline)
            .put("can", settings.can)
            .put("usb", settings.usb)
            .put("ble", settings.ble)
            .put("wifi", settings.wifi)
            .put("sleep", settings.sleep)
            .put("playlist", settings.playlist)
            .put("console", settings.console)
            .put("link", settings.link)
            .put("nvs", settings.nvs)
            .put("group_player", settings.group_player)
            .put("group_connect", settings.group_connect)
            .put("group_ota", settings.group_ota)
            .put("group_car", settings.group_car)
            .put("group_system", settings.group_system)
            .toString()
        val result = JSONObject(rpc("component_logs.set", payload))
        if (!result.optBoolean("success")) {
            throw IllegalStateException(result.optString("error", "Log settings were rejected"))
        }
        return "OK"
    }


    suspend fun otaBegin(totalSize: Int): Boolean {
        val payload = JSONObject().put("size", totalSize).toString()
        val result = JSONObject(rpc("ota.begin", payload))
        if (!result.optBoolean("success")) {
            throw IllegalStateException(result.optString("error", "OTA begin rejected"))
        }
        return true
    }

    suspend fun otaChunk(chunk: ByteArray, offset: Int): Int {
        val encoded = Base64.encodeToString(chunk, Base64.NO_WRAP)
        val payload = JSONObject().put("offset", offset).put("data", encoded).toString()
        val fullLen = 4 + "ota.data:".length + payload.toByteArray(Charsets.UTF_8).size
        if (fullLen > negotiatedMtu - 3) {
            throw IllegalStateException(
                "BLE MTU too small (mtu=$negotiatedMtu, need ${fullLen + 3}). Reconnect BLE.")
        }
        val result = JSONObject(rpc("ota.data", payload))
        if (!result.optBoolean("success")) {
            return result.optInt("written", -1)
        }
        return result.optInt("written", 0)
    }

    fun otaChunkNoWait(chunk: ByteArray, offset: Int): Boolean {
        val encoded = Base64.encodeToString(chunk, Base64.NO_WRAP)
        val payload = JSONObject().put("offset", offset).put("data", encoded).toString()
        return sendOtaDataNoResponse("rpc:ota.data:$payload")
    }

    suspend fun otaEnd(): Boolean {
        val result = JSONObject(rpc("ota.end"))
        if (!result.optBoolean("success")) {
            throw IllegalStateException(result.optString("error", "OTA end rejected"))
        }
        return true
    }

    suspend fun otaAbort() {
        runCatching { rpc("ota.abort") }
    }

    suspend fun otaStatus(): Triple<Boolean, Int, Int> {
        val json = JSONObject(rpc("ota.status"))
        return Triple(json.optBoolean("active"), json.optInt("written"), json.optInt("total"))
    }


    suspend fun slaveOtaBegin(totalSize: Int): Boolean {
        val payload = JSONObject().put("size", totalSize).toString()
        val result = JSONObject(rpc("slave_ota.begin", payload))
        if (!result.optBoolean("success")) {
            throw IllegalStateException(result.optString("error", "Slave OTA begin rejected"))
        }
        return true
    }

    suspend fun slaveOtaChunk(chunk: ByteArray, offset: Int): Int {
        val encoded = Base64.encodeToString(chunk, Base64.NO_WRAP)
        val payload = JSONObject().put("offset", offset).put("data", encoded).toString()
        val fullLen = 4 + "slave_ota.data:".length + payload.toByteArray(Charsets.UTF_8).size
        if (fullLen > negotiatedMtu - 3) {
            throw IllegalStateException("BLE MTU too small for slave OTA chunk")
        }
        val result = JSONObject(rpc("slave_ota.data", payload))
        return if (result.optBoolean("success")) result.optInt("written", -1) else -1
    }

    suspend fun slaveOtaStart(): Boolean {
        val result = JSONObject(rpc("slave_ota.start"))
        if (!result.optBoolean("success")) {
            throw IllegalStateException(result.optString("error", "Slave OTA start rejected"))
        }
        return true
    }

    suspend fun slaveOtaAbort() {
        runCatching { rpc("slave_ota.abort") }
    }

    suspend fun slaveOtaStatus(): Triple<Int, Int, Int> {
        val json = JSONObject(rpc("slave_ota.status"))
        return Triple(json.optInt("state", 0), json.optInt("written", 0), json.optInt("sent", 0))
    }


    data class FileOtaStatus(
        val state: Int,
        val target: Int,
        val written: Int,
        val total: Int,
        val applied: Int,
        val error: String
    )

    suspend fun fileOtaBegin(totalSize: Int, target: String): Boolean {
        val payload = JSONObject().put("size", totalSize).put("target", target).toString()
        val result = JSONObject(rpc("file.begin", payload))
        if (!result.optBoolean("success")) {
            throw IllegalStateException(result.optString("error", "File OTA begin rejected"))
        }
        return true
    }

    suspend fun fileOtaChunk(chunk: ByteArray, offset: Int): Int {
        val encoded = Base64.encodeToString(chunk, Base64.NO_WRAP)
        val payload = JSONObject().put("offset", offset).put("data", encoded).toString()
        val fullLen = 4 + "file.data:".length + payload.toByteArray(Charsets.UTF_8).size
        lastChunkCommandBytes = fullLen
        val limit = maxCommandBytes()
        if (fullLen > limit) {
            throw IllegalStateException(
                "Chunk command of $fullLen B does not fit into $limit B (mtu=$negotiatedMtu)"
            )
        }
        val result = JSONObject(rpc("file.data", payload))
        if (result.optBoolean("success")) {
            lastFileOtaError = ""
            lastFileOtaTruncated = false
            return result.optInt("written", -1)
        }
        lastFileOtaError = result.optString("error", "")
        val received = result.optInt("payload_len", -1)
        lastFileOtaTruncated = received in 1 until payload.length
        if (lastFileOtaTruncated) {
            // The device received only `received + "file.data:".length + "rpc:".length`
            // bytes of our command: never send more than that again.
            observedCommandBytes = received + "file.data:".length + 4
            if (received <= 30) {
                // Writes are being cut down to the ATT minimum: our cached MTU is wrong.
                negotiatedMtu = 23
            }
        }
        Timber.w(
            "file.data rejected at $offset: '$lastFileOtaError' " +
                "(device got $received of ${payload.length} bytes)"
        )
        return -1
    }

    suspend fun fileOtaFinish(): Boolean {
        val result = JSONObject(rpc("file.finish"))
        if (!result.optBoolean("success")) {
            throw IllegalStateException(result.optString("error", "File OTA finish rejected"))
        }
        return true
    }

    suspend fun fileOtaAbort() {
        runCatching { rpc("file.abort") }
    }

    suspend fun fileOtaStatus(): FileOtaStatus {
        val json = JSONObject(rpc("file.status"))
        return FileOtaStatus(
            state = json.optInt("state", 0),
            target = json.optInt("target", 0),
            written = json.optInt("written", 0),
            total = json.optInt("total", 0),
            applied = json.optInt("applied", 0),
            error = json.optString("error", "")
        )
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
        reconnectHandler.removeCallbacks(reconnectWatchdog)
        reconnectHandler.postDelayed(reconnectWatchdog, 4000)
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
        if (bluetoothGatt != null && _isConnected.value) {
            return true
        }
        if (bluetoothGatt != null) {
            Timber.d("BLE: closing stale GATT before reconnect")
            bluetoothGatt?.close()
            bluetoothGatt = null
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

    fun currentDeviceLabel(): String {
        val name = connectedDevice?.name
        if (!name.isNullOrBlank()) return name
        return preferences.getString("device_address", null) ?: tr("Не вибрано")
    }

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
    fun maxOtaChunkSize(): Int {
        var attPayload = (negotiatedMtu - 3).coerceAtLeast(20)
        val observed = observedCommandBytes
        if (observed in 1 until attPayload) {
            attPayload = observed
        }
        // "rpc:" + "file.data:" + {"offset":NNNNNNN,"data":"BASE64"}
        val overhead = 4 + 9 + 10 + 7 + 9 + 2
        val b64Budget = attPayload - overhead
        if (b64Budget < 24) return 16
        return ((b64Budget / 4) * 3).coerceAtLeast(16).coerceAtMost(340)
    }

    /** How many bytes of a command the device can really receive (0 = unknown). */
    fun commandCapacity(): Int = observedCommandBytes

    fun resetCommandCapacity() {
        observedCommandBytes = 0
    }

    /**
     * Re-runs the ATT MTU exchange. Used when the device reports that it got a
     * truncated command, which means our cached MTU is stale (after a silent
     * reconnection) and the stack is cutting every write down to few bytes.
     */
    @SuppressLint("MissingPermission")
    suspend fun renegotiateMtu(timeoutMs: Long = 3000): Int {
        val gatt = bluetoothGatt ?: return negotiatedMtu
        negotiatedMtu = 23
        observedCommandBytes = 0
        if (!gatt.requestMtu(512)) {
            Timber.w("BLE MTU renegotiation could not be started")
            return negotiatedMtu
        }
        val start = android.os.SystemClock.elapsedRealtime()
        while (negotiatedMtu < 100 &&
            android.os.SystemClock.elapsedRealtime() - start < timeoutMs
        ) {
            delay(50)
        }
        Timber.d("BLE MTU after renegotiation: $negotiatedMtu")
        return negotiatedMtu
    }

    /** Drops the link and reconnects (used when the MTU cannot be restored). */
    @SuppressLint("MissingPermission")
    suspend fun forceReconnect(timeoutMs: Long = 12000): Boolean {
        val address = preferences.getString("device_address", null) ?: return false
        val device = try {
            bluetoothAdapter?.getRemoteDevice(address)
        } catch (e: IllegalArgumentException) {
            null
        } ?: return false

        Timber.w("BLE: forcing reconnect to $address")
        runCatching { bluetoothGatt?.disconnect() }
        runCatching { bluetoothGatt?.close() }
        bluetoothGatt = null
        _isConnected.value = false
        _serviceReady.value = false
        _notificationsReady.value = false
        negotiatedMtu = 23
        servicesDiscovered = false

        manualReconnectInProgress = true
        try {
            if (!connect(device)) return false
            val start = android.os.SystemClock.elapsedRealtime()
            while (android.os.SystemClock.elapsedRealtime() - start < timeoutMs) {
                if (_notificationsReady.value) return true
                delay(150)
            }
            return _serviceReady.value
        } finally {
            manualReconnectInProgress = false
        }
    }

    /** Hard cap for a single command: ATT limit, but never more than the device accepts. */
    fun maxCommandBytes(): Int {
        val att = (negotiatedMtu - 3).coerceAtLeast(20)
        val observed = observedCommandBytes
        return if (observed in 1 until att) observed else att
    }

    /** Size of the last `file.data` command that was built (for error messages). */
    @Volatile var lastChunkCommandBytes: Int = 0
        private set

    fun currentMtu(): Int = negotiatedMtu

    suspend fun ensureMtu(timeoutMs: Long = 2500): Int {
        val gatt = bluetoothGatt ?: return negotiatedMtu
        if (negotiatedMtu >= 100) return negotiatedMtu
        gatt.requestMtu(512)
        val start = System.currentTimeMillis()
        while (negotiatedMtu < 100 && System.currentTimeMillis() - start < timeoutMs) {
            delay(50)
        }
        return negotiatedMtu
    }

    fun sendOtaDataNoResponse(fullCommand: String): Boolean {
        if (!_isConnected.value || commandCharacteristic == null) return false
        val bytes = fullCommand.toByteArray(Charsets.UTF_8)
        if (bytes.size > negotiatedMtu - 3) {
            Timber.e("BLE OTA chunk too large: ${bytes.size} > ${negotiatedMtu - 3} (mtu=$negotiatedMtu)")
            return false
        }
        synchronized(otaWriteLock) {
            pendingOtaWrites.addLast(bytes)
        }
        startNextOtaWrite()
        return true
    }

    fun isOtaWriteIdle(): Boolean = synchronized(otaWriteLock) {
        !otaWriteInProgress && pendingOtaWrites.isEmpty()
    }

    @SuppressLint("MissingPermission")
    private fun startNextOtaWrite() {
        val gatt = bluetoothGatt
        val characteristic = commandCharacteristic
        if (gatt == null || characteristic == null) {
            synchronized(otaWriteLock) { otaWriteInProgress = false }
            return
        }
        synchronized(writeArbiter) {
            if (commandWriteInProgress) {
                mainHandler.postDelayed({ startNextOtaWrite() }, 3)
                return
            }
            val bytes = synchronized(otaWriteLock) {
                if (otaWriteInProgress) return
                val next = pendingOtaWrites.pollFirst() ?: return
                otaWriteInProgress = true
                next
            }
            writeFrame(gatt, characteristic, bytes)
        }
    }

    private fun writeFrame(
        gatt: android.bluetooth.BluetoothGatt,
        characteristic: android.bluetooth.BluetoothGattCharacteristic,
        bytes: ByteArray
    ) {
        characteristic.value = bytes
        characteristic.writeType = WRITE_TYPE_NO_RESPONSE
        if (!gatt.writeCharacteristic(characteristic)) {
            // Controller is busy: retry the same frame shortly, keep it unacknowledged.
            mainHandler.postDelayed({
                synchronized(otaWriteLock) {
                    otaWriteInProgress = false
                    pendingOtaWrites.addFirst(bytes)
                }
                startNextOtaWrite()
            }, 5)
            return
        }
        // Watchdog: some stacks never call onCharacteristicWrite for WRITE_NR
        mainHandler.postDelayed({
            if (otaWriteInProgress) {
                synchronized(otaWriteLock) { otaWriteInProgress = false }
                startNextOtaWrite()
            }
        }, 15)
    }

    /**
     * Sends one binary OTA frame: [0xA5][0x01][offset:4 LE][len:2 LE][payload].
     * Uses Write Without Response, so there is no RPC round trip per chunk.
     */
    fun otaSendFrame(offset: Int, data: ByteArray): Boolean {
        if (!_isConnected.value || commandCharacteristic == null) {
            throw IllegalStateException("BLE is not connected")
        }
        val frame = ByteArray(data.size + 8)
        frame[0] = 0xA5.toByte()
        frame[1] = 0x01
        frame[2] = (offset and 0xFF).toByte()
        frame[3] = ((offset ushr 8) and 0xFF).toByte()
        frame[4] = ((offset ushr 16) and 0xFF).toByte()
        frame[5] = ((offset ushr 24) and 0xFF).toByte()
        frame[6] = (data.size and 0xFF).toByte()
        frame[7] = ((data.size ushr 8) and 0xFF).toByte()
        System.arraycopy(data, 0, frame, 8, data.size)

        val limit = maxCommandBytes()
        if (frame.size > limit) {
            throw IllegalStateException(
                "OTA frame of ${frame.size} B does not fit into $limit B (mtu=$negotiatedMtu)"
            )
        }
        synchronized(otaWriteLock) { pendingOtaWrites.addLast(frame) }
        startNextOtaWrite()
        return true
    }

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
        val gatt = bluetoothGatt
        val characteristic = commandCharacteristic
        if (gatt == null || characteristic == null) {
            synchronized(commandWriteLock) { commandWriteInProgress = false }
            return
        }
        synchronized(writeArbiter) {
        if (otaWriteInProgress || synchronized(otaWriteLock) { pendingOtaWrites.isNotEmpty() }) {
            mainHandler.postDelayed({ startNextCommandWrite() }, 3)
            return
        }
        val commandBytes = synchronized(commandWriteLock) {
            if (commandWriteInProgress) return
            val next = pendingCommandWrites.pollFirst() ?: return
            commandWriteInProgress = true
            next
        }
        val limit = maxCommandBytes()
        if (commandBytes.size > limit) {
            Timber.e("BLE command too large: ${commandBytes.size} B (limit $limit B, mtu=$negotiatedMtu)")
            pendingRpcResponse?.completeExceptionally(
                IllegalStateException(
                    "BLE command of ${commandBytes.size} B exceeds the limit of $limit B " +
                        "(mtu=$negotiatedMtu); chunk size must be reduced"
                )
            )
            synchronized(commandWriteLock) { commandWriteInProgress = false }
            startNextCommandWrite()
            return
        }
        characteristic.value = commandBytes
        characteristic.writeType = WRITE_TYPE_DEFAULT
        if (!gatt.writeCharacteristic(characteristic)) {
            Timber.e("❌ Could not start BLE command write")
            synchronized(commandWriteLock) { commandWriteInProgress = false }
            mainHandler.postDelayed({ startNextCommandWrite() }, 5)
        }
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
        val result = mutableListOf<String>()
        var offset = 0
        var guard = 0
        while (guard++ < 300) {
            val payload = JSONObject().put("offset", offset).put("count", 3).toString()
            val json = JSONObject(rpc("playlist.get", payload))
            val total = json.optInt("total", 0)
            val items = json.optJSONArray("items")
            if (items != null) {
                for (i in 0 until items.length()) {
                    result.add(items.optString(i))
                }
            }
            val next = json.optInt("next", offset)
            if (next <= offset || next >= total) break
            offset = next
        }
        _playlistData.value = result
        return result
    }
    
}
