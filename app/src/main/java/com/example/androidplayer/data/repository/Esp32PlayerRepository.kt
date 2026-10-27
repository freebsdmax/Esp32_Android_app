package com.example.androidplayer.data.repository

import com.example.androidplayer.data.model.*
import com.example.androidplayer.data.network.Esp32PlayerApi
import com.example.androidplayer.data.ble.BlePlayerClient
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с ESP32 плеером
 * Абстрагирует источники данных (HTTP/BLE) и предоставляет единый интерфейс
 */
@Singleton
class Esp32PlayerRepository @Inject constructor(
    private val api: Esp32PlayerApi
) {
    
    /**
     * Текущий источник подключения (true=BLE, false=HTTP)
     */
    var useBleTransport: Boolean = false
        private set
    
    /**
     * BLE клієнт (опціональний, встановлюється пізніше)
     */
    private var bleClient: BlePlayerClient? = null
    
    /**
     * Встановити BLE клієнт та переключити в BLE режим
     */
    fun setBleClient(client: BlePlayerClient) {
        bleClient = client
        useBleTransport = true
        timber.log.Timber.d("📡 Repository: BLE transport enabled, useBleTransport=$useBleTransport")
    }
    
    /**
     * Відключити BLE та переключити в HTTP режим
     */
    fun disconnectBle() {
        bleClient?.disconnect()
        bleClient = null
        useBleTransport = false
    }

    /**
     * Повертає, чи активний BLE транспорт (true) або HTTP (false)
     */
    fun isBleTransport(): Boolean = useBleTransport
    
    // ========================================================================
    // ПЛЕЕР УПРАВЛЕНИЕ
    // ========================================================================
    
    suspend fun getPlayerStatus(): Result<PlayerStatus> = safeApiCall {
        if (useBleTransport && bleClient != null) {
            bleClient!!.getStatus()
        } else {
            api.getPlayerStatus()
        }
    }
    
    suspend fun getPlaylist(): Result<List<String>> = safeApiCall {
        if (useBleTransport && bleClient != null) {
            bleClient!!.getPlaylist()
        } else {
            api.getPlaylist()
        }
    }
    
    suspend fun sendPlayerCommand(command: String, track: Int? = null, position: Int? = null): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) {
            when (command) {
                "play" -> if (track != null) bleClient!!.play(track) else bleClient!!.play(0)
                "pause" -> bleClient!!.pause()
                "next" -> bleClient!!.next()
                "prev" -> bleClient!!.previous()
                "set_pos" -> if (position != null) bleClient!!.seek(position) else ""
                "switch_mode" -> if (bleClient!!.sendCommand("switch_mode")) "OK" else "FAILED"
                else -> throw IllegalArgumentException("Unknown command: $command")
            }
        } else {
            api.sendPlayerCommand(command, track, position)
        }
    }
    
    suspend fun playTrack(trackIndex: Int): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) {
            bleClient!!.play(trackIndex)
        } else {
            api.sendPlayerCommand("play", trackIndex)
        }
    }
    
    suspend fun pausePlayback(): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) {
            bleClient!!.pause()
        } else {
            api.sendPlayerCommand("pause")
        }
    }
    
    suspend fun resumePlayback(): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) {
            bleClient!!.play(0)
        } else {
            api.sendPlayerCommand("play")
        }
    }
    
    suspend fun nextTrack(): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) {
            bleClient!!.next()
        } else {
            api.sendPlayerCommand("next")
        }
    }
    
    suspend fun previousTrack(): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) {
            bleClient!!.previous()
        } else {
            api.sendPlayerCommand("prev")
        }
    }
    
    suspend fun switchMode(): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) {
            if (bleClient!!.sendCommand("switch_mode")) "OK" else throw IllegalStateException("BLE command could not be sent")
        } else {
            api.sendPlayerCommand("switch_mode")
        }
    }
    
    suspend fun setPosition(positionMs: Int): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) {
            bleClient!!.seek(positionMs)
        } else {
            api.sendPlayerCommand("set_pos", position = positionMs)
        }
    }
    
    // ========================================================================
    // WI-FI УПРАВЛЕНИЕ
    // ========================================================================
    
    suspend fun getWifiSettings(): Result<WifiSettings> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.getWifiSettings() else api.getWifiSettings()
    }
    
    suspend fun setWifiSettings(settings: WifiSettings): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.setWifiSettings(settings) else api.setWifiSettings(settings)
    }
    
    suspend fun toggleSniffer(enabled: Boolean): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.toggleSniffer(enabled)
        else api.toggleSniffer("set_sniffer", if (enabled) 1 else 0)
    }
    
    // ========================================================================
    // СИСТЕМНЫЕ НАСТРОЙКИ
    // ========================================================================
    
    suspend fun getSleepSettings(): Result<SleepSettings> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.getSleepSettings() else api.getSleepSettings()
    }
    
    suspend fun setSleepSettings(settings: SleepSettings): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.setSleepSettings(settings) else api.setSleepSettings(settings)
    }
    
    suspend fun getLogLevel(): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.getLogLevel() else api.getLogLevel()
    }
    
    suspend fun setLogLevel(level: String): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.setLogLevel(level) else api.setLogLevel(level)
    }
    
    suspend fun getVerboseLogs(): Result<Boolean> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.getVerboseLogs() else api.getVerboseLogs() == "1"
    }
    
    suspend fun setVerboseLogs(enabled: Boolean): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.setVerboseLogs(enabled)
        else api.setVerboseLogs(if (enabled) "1" else "0")
    }
    
    suspend fun getComponentLogs(): Result<ComponentLogSettings> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.getComponentLogs() else api.getComponentLogs()
    }
    
    suspend fun setComponentLogs(settings: ComponentLogSettings): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.setComponentLogs(settings)
        else api.setComponentLogs(settings)
    }
    
    // ========================================================================
    // OTA ОБНОВЛЕНИЯ
    // ========================================================================
    
    suspend fun getOtaStatus(): Result<OtaStatus> = safeApiCall {
        if (useBleTransport) throw UnsupportedOperationException("OTA status requires Wi-Fi")
        api.getOtaStatus()
    }
    
    suspend fun uploadMasterFirmware(firmware: ByteArray): Result<String> = safeApiCall {
        if (useBleTransport) throw UnsupportedOperationException("Firmware upload requires Wi-Fi")
        api.uploadMasterFirmware(firmware)
    }
    
    suspend fun uploadSlaveFirmware(firmware: ByteArray): Result<String> = safeApiCall {
        if (useBleTransport) throw UnsupportedOperationException("Firmware upload requires Wi-Fi")
        api.uploadSlaveFirmware(firmware)
    }
    
    // ========================================================================
    // МОНИТОРИНГ
    // ========================================================================
    
    suspend fun getSystemLogs(): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.getSystemLogs() else api.getSystemLogs()
    }
    
    suspend fun getVersionInfo(): Result<VersionInfo> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.getVersionInfo() else api.getVersionInfo()
    }
    
    suspend fun restartDevice(): Result<String> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.restartDevice() else api.restartDevice()
    }
    
    suspend fun getCanStatistics(): Result<CanStatistics> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.getCanStatistics() else api.getCanStatistics()
    }
    
    suspend fun getSystemInfo(): Result<SystemInfo> = safeApiCall {
        if (useBleTransport && bleClient != null) bleClient!!.getSystemInfo() else api.getSystemInfo()
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
            if (useBleTransport && bleClient != null) {
                bleClient!!.getStatus()
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