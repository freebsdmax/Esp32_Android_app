package com.example.androidplayer.data.repository

import com.example.androidplayer.data.model.*
import com.example.androidplayer.data.network.Esp32PlayerApi
import com.example.androidplayer.data.ble.BlePlayerClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с ESP32 плеером
 * Абстрагирует источники данных (HTTP/BLE) и предоставляет единый интерфейс
 */
@Singleton
class Esp32PlayerRepository @Inject constructor(
    private val api: Esp32PlayerApi,
    private val bleClient: BlePlayerClient
) {
    
    /**
     * Текущий источник подключения (true=BLE, false=HTTP)
     */
    private val otaMutex = Mutex()

    var useBleTransport: Boolean = true
        private set
    
    /**
     * BLE клієнт (опціональний, встановлюється пізніше)
     */
    // BLE client is injected; transport is BLE-only
    
    /**
     * Встановити BLE клієнт та переключити в BLE режим
     */
    fun setBleClient(client: BlePlayerClient) { useBleTransport = true }




    
    /**
     * Відключити BLE та переключити в HTTP режим
     */
    fun disconnectBle() {
        bleClient.disconnect()
        useBleTransport = true
    }

    /**
     * Повертає, чи активний BLE транспорт (true) або HTTP (false)
     */
    fun isBleTransport(): Boolean = useBleTransport
    
    // ========================================================================
    // ПЛЕЕР УПРАВЛЕНИЕ
    // ========================================================================
    
    suspend fun getPlayerStatus(): Result<PlayerStatus> = safeApiCall {
        if (useBleTransport) {
            bleClient.getStatus()
        } else {
            api.getPlayerStatus()
        }
    }
    
    suspend fun getPlaylist(): Result<List<String>> = safeApiCall {
        if (useBleTransport) {
            bleClient.getPlaylist()
        } else {
            api.getPlaylist()
        }
    }
    
    suspend fun sendPlayerCommand(command: String, track: Int? = null, position: Int? = null): Result<String> = safeApiCall {
        if (useBleTransport) {
            when (command) {
                "play" -> if (track != null) bleClient.play(track) else bleClient.play(0)
                "pause" -> bleClient.pause()
                "next" -> bleClient.next()
                "prev" -> bleClient.previous()
                "set_pos" -> if (position != null) bleClient.seek(position) else ""
                "switch_mode" -> if (bleClient.sendCommand("switch_mode")) "OK" else "FAILED"
                else -> throw IllegalArgumentException("Unknown command: $command")
            }
        } else {
            api.sendPlayerCommand(command, track, position)
        }
    }
    
    suspend fun playTrack(trackIndex: Int): Result<String> = safeApiCall {
        if (useBleTransport) {
            bleClient.play(trackIndex)
        } else {
            api.sendPlayerCommand("play", trackIndex)
        }
    }
    
    suspend fun pausePlayback(): Result<String> = safeApiCall {
        if (useBleTransport) {
            bleClient.pause()
        } else {
            api.sendPlayerCommand("pause")
        }
    }
    
    suspend fun resumePlayback(): Result<String> = safeApiCall {
        if (useBleTransport) {
            bleClient.play(0)
        } else {
            api.sendPlayerCommand("play")
        }
    }
    
    suspend fun nextTrack(): Result<String> = safeApiCall {
        if (useBleTransport) {
            bleClient.next()
        } else {
            api.sendPlayerCommand("next")
        }
    }
    
    suspend fun previousTrack(): Result<String> = safeApiCall {
        if (useBleTransport) {
            bleClient.previous()
        } else {
            api.sendPlayerCommand("prev")
        }
    }
    
    suspend fun switchMode(): Result<String> = safeApiCall {
        if (useBleTransport) {
            if (bleClient.sendCommand("switch_mode")) "OK" else throw IllegalStateException("BLE command could not be sent")
        } else {
            api.sendPlayerCommand("switch_mode")
        }
    }
    
    suspend fun setPosition(positionMs: Int): Result<String> = safeApiCall {
        if (useBleTransport) {
            bleClient.seek(positionMs)
        } else {
            api.sendPlayerCommand("set_pos", position = positionMs)
        }
    }
    
    // ========================================================================
    // WI-FI УПРАВЛЕНИЕ
    // ========================================================================
    
    suspend fun getWifiSettings(): Result<WifiSettings> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.getWifiSettings() else api.getWifiSettings()
    }
    
    suspend fun setWifiSettings(settings: WifiSettings): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.setWifiSettings(settings) else api.setWifiSettings(settings)
    }
    
    suspend fun toggleSniffer(enabled: Boolean): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.toggleSniffer(enabled)
        else api.toggleSniffer("set_sniffer", if (enabled) 1 else 0)
    }
    
    // ========================================================================
    // СИСТЕМНЫЕ НАСТРОЙКИ
    // ========================================================================
    
    suspend fun getSleepSettings(): Result<SleepSettings> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.getSleepSettings() else api.getSleepSettings()
    }
    
    suspend fun setSleepSettings(settings: SleepSettings): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.setSleepSettings(settings) else api.setSleepSettings(settings)
    }
    
    suspend fun getLogLevel(): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.getLogLevel() else api.getLogLevel()
    }
    
    suspend fun setLogLevel(level: String): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.setLogLevel(level) else api.setLogLevel(level)
    }
    
    suspend fun getVerboseLogs(): Result<Boolean> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.getVerboseLogs() else api.getVerboseLogs() == "1"
    }
    
    suspend fun setVerboseLogs(enabled: Boolean): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.setVerboseLogs(enabled)
        else api.setVerboseLogs(if (enabled) "1" else "0")
    }
    
    suspend fun getComponentLogs(): Result<ComponentLogSettings> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.getComponentLogs() else api.getComponentLogs()
    }
    
    suspend fun setComponentLogs(settings: ComponentLogSettings): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.setComponentLogs(settings)
        else api.setComponentLogs(settings)
    }
    
    // ========================================================================
    // OTA ОБНОВЛЕНИЯ
    // ========================================================================
    

    /**
     * Update firmware over BLE (chunked, base64 RPC).
     * Progress callback receives 0..100.
     */
    suspend fun bleOtaUpdate(firmware: ByteArray, onProgress: (Int) -> Unit): Result<Unit> = safeApiCall {
        val client = bleClient ?: throw IllegalStateException("BLE client not connected")
        if (!useBleTransport) throw IllegalStateException("Switch to BLE transport first")
        val mtu = client.ensureMtu()
        if (mtu < 100) throw IllegalStateException("BLE MTU too small ($mtu). Reconnect and retry.")
        val chunkSize = client.maxOtaChunkSize()
        client.otaBegin(firmware.size)
        var offset = 0
        var retries = 0
        try {
            while (offset < firmware.size) {
                val end = minOf(offset + chunkSize, firmware.size)
                val written = client.otaChunk(firmware.copyOfRange(offset, end), offset)
                if (written < 0) {
                    retries++
                    if (retries > 5) throw IllegalStateException("OTA chunk rejected")
                    delay(50)
                    continue
                }
                if (written > offset) {
                    offset = written
                    retries = 0
                } else {
                    retries++
                    if (retries > 5) throw IllegalStateException("OTA stuck at $offset")
                    delay(50)
                }
                onProgress((offset * 100L / firmware.size).toInt())
            }
            onProgress(100)
            client.otaEnd()
        } catch (e: Exception) {
            runCatching { client.otaAbort() }
            throw e
        }
    }


    suspend fun bleSlaveOtaUpdate(firmware: ByteArray, onProgress: (Int) -> Unit): Result<Unit> = safeApiCall {


        val mtu = bleClient.ensureMtu()
        if (mtu < 100) throw IllegalStateException("BLE MTU too small ($mtu). Reconnect and retry.")
        val chunkSize = minOf(240, (bleClient.maxOtaChunkSize() - 8).coerceAtLeast(16))
        bleClient.slaveOtaBegin(firmware.size)
        var offset = 0
        var retries = 0
        try {
            while (offset < firmware.size) {
                val end = minOf(offset + chunkSize, firmware.size)
                val written = bleClient.slaveOtaChunk(firmware.copyOfRange(offset, end), offset)
                if (written < 0) {
                    retries++
                    if (retries > 5) throw IllegalStateException("Slave OTA chunk rejected")
                    delay(50); continue
                }
                if (written > offset) { offset = written; retries = 0 }
                else {
                    retries++
                    if (retries > 5) throw IllegalStateException("Slave OTA stuck at $offset")
                    delay(50)
                }
                onProgress((offset * 90L / firmware.size).toInt())
                delay(3)
            }
            bleClient.slaveOtaStart()
            var guard = 0
            while (guard++ < 1200) {
                val (state, _, sent) = bleClient.slaveOtaStatus()
                if (state == 3) { onProgress(100); return@safeApiCall }
                if (state == 4) throw IllegalStateException("Slave OTA failed on device")
                onProgress(90 + (sent * 10L / firmware.size).toInt().coerceAtMost(9))
                delay(250)
            }
            throw IllegalStateException("Slave OTA timeout")
        } catch (e: Exception) {
            runCatching { bleClient.slaveOtaAbort() }
            throw e
        }
    }


    /**
     * Uploads a firmware image and flashes it.
     *
     * When [storeName] is set the image is first written to the device's
     * internal /storage/<storeName> and only then flashed; the file is deleted
     * by the device after a successful update. This keeps the download on the
     * board until the flash really happened.
     */
    suspend fun bleFileOtaUpdate(
        firmware: ByteArray,
        target: String,
        storeName: String? = null,
        onProgress: (OtaTransferProgress) -> Unit
    ): Result<Unit> = safeApiCall {
        if (!otaMutex.tryLock()) throw IllegalStateException("An update is already in progress")
        bleClient.setOtaSessionActive(true)
        try {
            val mtu = bleClient.ensureMtu()
            if (mtu < 100) throw IllegalStateException("BLE MTU too small ($mtu). Reconnect and retry.")
            bleClient.fileOtaBegin(firmware.size, target, storeName)
            val total = firmware.size.toLong()
            val uploadMeter = RateTracker()
            var offset = 0

            fun emit(phase: String, done: Long, totalForPhase: Long, percent: Int, meter: RateTracker) {
                val speed = meter.at(done)
                val remaining = (totalForPhase - done).coerceAtLeast(0)
                val eta = if (speed > 0) remaining / speed else -1L
                onProgress(
                    OtaTransferProgress(
                        percent = percent.coerceIn(0, 100),
                        bytesDone = done,
                        bytesTotal = totalForPhase,
                        bytesPerSecond = speed,
                        etaSeconds = eta,
                        phase = phase
                    )
                )
            }

            var mtuRecoveries = 0
            var attempts = 0

            suspend fun resyncFromDevice(fallback: Int): Int {
                val st = runCatching { bleClient.fileOtaStatus() }.getOrNull() ?: return fallback
                if (st.state == 4) {
                    throw IllegalStateException(
                        "Device failed: " + st.error.ifBlank { "unknown error" }
                    )
                }
                if (st.state == 0 && fallback > 0) {
                    throw IllegalStateException("Device lost the update session at $fallback. Try again.")
                }
                return st.written.coerceIn(0, firmware.size)
            }

            try {
                while (true) {
                    if (attempts++ > 4) {
                        throw IllegalStateException(
                            "Device keeps stopping at $offset/${firmware.size} bytes. Try again."
                        )
                    }
                    val before = offset
                    var aborted = false

                    // Binary frames, Write Without Response: no RPC round trip per chunk.
                    while (offset < firmware.size) {
                        val frameData = (bleClient.maxCommandBytes() - 8).coerceIn(16, 480)
                        val end = minOf(offset + frameData, firmware.size)
                        try {
                            bleClient.otaSendFrame(offset, firmware.copyOfRange(offset, end))
                        } catch (e: Exception) {
                            if (mtuRecoveries++ >= 3) throw e
                            Timber.w("OTA frame at $offset failed (${e.message}); fixing BLE link")
                            var mtu = bleClient.renegotiateMtu(3000)
                            if (mtu < 100) bleClient.forceReconnect()
                            aborted = true
                            break
                        }
                        offset = end
                        emit("upload", offset.toLong(), total, (offset * 90L / firmware.size).toInt(), uploadMeter)
                        while (!bleClient.isOtaWriteIdle()) delay(2)
                    }

                    if (aborted) {
                        if (bleClient.currentMtu() < 100) {
                            throw IllegalStateException(
                                "BLE MTU could not be restored after reconnecting. Try again."
                            )
                        }
                        offset = resyncFromDevice(offset)
                        continue
                    }

                    val written = resyncFromDevice(offset)
                    if (written >= firmware.size) break
                    if (written <= before) {
                        if (mtuRecoveries++ >= 3) {
                            throw IllegalStateException(
                                "Device received nothing at $offset (mtu=${bleClient.currentMtu()}). " +
                                    "Reconnect the device and retry."
                            )
                        }
                        Timber.w("OTA: device has $written B, nothing advanced; fixing BLE link")
                        var mtu = bleClient.renegotiateMtu(3000)
                        if (mtu < 100) bleClient.forceReconnect()
                    } else {
                        Timber.w("OTA: device has $written/${firmware.size} B, resending the tail")
                    }
                    offset = written
                    emit("upload", offset.toLong(), total, (offset * 90L / firmware.size).toInt(), uploadMeter)
                }

                emit("apply", total, total, 90, uploadMeter)
                bleClient.fileOtaFinish()

                if (storeName != null) {
                    // The image is on the device now: flash it from /storage and
                    // let the device delete the file after success.
                    emit("apply", 0, total, 91, uploadMeter)
                    bleClient.fileOtaFlash(storeName, target)
                    if (target == "master") {
                        emit("apply", total, total, 100, uploadMeter)
                        return@safeApiCall
                    }
                } else if (target == "master") {
                    emit("apply", total, total, 100, uploadMeter)
                    return@safeApiCall
                }

                val applyMeter = RateTracker()
                var guard = 0
                while (guard++ < 1200) {
                    val st = bleClient.fileOtaStatus()
                    if (st.state == 3) {
                        emit("apply", total, total, 100, applyMeter)
                        return@safeApiCall
                    }
                    if (st.state == 4) {
                        val detail = if (st.error.isNotBlank()) st.error else "unknown error"
                        throw IllegalStateException("Device failed: $detail")
                    }
                    val phaseTotal = if (st.total > 0) st.total.toLong() else total
                    val done = st.applied.toLong().coerceIn(0, phaseTotal)
                    val percent = if (phaseTotal > 0) 90 + (done * 9L / phaseTotal).toInt() else 90
                    emit("apply", done, phaseTotal, percent.coerceIn(90, 99), applyMeter)
                    delay(250)
                }
                throw IllegalStateException("File OTA timeout")
            } catch (e: Exception) {
                runCatching { bleClient.fileOtaAbort() }
                throw e
            }
        } finally {
            bleClient.setOtaSessionActive(false)
            otaMutex.unlock()
        }
    }

    suspend fun getOtaStatus(): Result<OtaStatus> = safeApiCall {
        
        api.getOtaStatus()
    }
    
    suspend fun uploadMasterFirmware(firmware: ByteArray): Result<String> = safeApiCall {
        bleOtaUpdate(firmware) { }.getOrThrow()
        "OK"
    }
    
    suspend fun uploadSlaveFirmware(firmware: ByteArray): Result<String> = safeApiCall {
        if (useBleTransport) throw UnsupportedOperationException("Firmware upload requires Wi-Fi")
        api.uploadSlaveFirmware(firmware)
    }
    
    // ========================================================================
    // МОНИТОРИНГ
    // ========================================================================
    
    suspend fun getMedia(): Result<MediaInfo> = safeApiCall {
        bleClient.getMedia()
    }

    suspend fun getSystemLogs(): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.getSystemLogs() else api.getSystemLogs()
    }
    
    suspend fun getVersionInfo(): Result<VersionInfo> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.getVersionInfo() else api.getVersionInfo()
    }
    
    suspend fun restartDevice(): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.restartDevice() else api.restartDevice()
    }
    
    suspend fun getCanStatistics(): Result<CanStatistics> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.getCanStatistics() else api.getCanStatistics()
    }
    
    suspend fun getSystemInfo(): Result<SystemInfo> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient.getSystemInfo() else api.getSystemInfo()
    }
    
    // ========================================================================
    // UTILITY FUNCTIONS
    // ========================================================================
    
    /**
     * Безопасный вызов API с обработкой ошибок
     */
    private suspend fun <T> safeApiCall(apiCall: suspend () -> T): Result<T> {
        return try {
            val result = apiCall()
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Проверка соединения с устройством
     */
    suspend fun testConnection(): Result<Boolean> {
        return try {
            if (useBleTransport) {
                bleClient.getStatus()
                Result.success(true)
            } else {
                api.getPlayerStatus()
                Result.success(true)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Переключение между BLE и HTTP транспортом
     */
    fun switchTransport(useBle: Boolean) {
        useBleTransport = useBle
    }
    
    /**
     * Получить текущий активный транспорт
     */
    fun getActiveTransport(): String {
        return if (useBleTransport) "BLE" else "HTTP"
    }
}

/**
 * Smoothed transfer rate meter. [at] is expected to be called on every progress
 * tick; it only recomputes a few times per second to keep the number stable.
 */
private class RateTracker {
    private var lastTime = android.os.SystemClock.elapsedRealtime()
    private var lastBytes = 0L
    private var speed = 0.0

    fun at(bytes: Long): Long {
        val now = android.os.SystemClock.elapsedRealtime()
        val dt = now - lastTime
        if (dt >= 400) {
            val instant = (bytes - lastBytes) * 1000.0 / dt
            speed = if (speed <= 0.0) instant else speed * 0.6 + instant * 0.4
            if (speed < 0.0) speed = 0.0
            lastTime = now
            lastBytes = bytes
        }
        return speed.toLong()
    }
}
