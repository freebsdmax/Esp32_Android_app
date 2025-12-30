package com.example.androidplayer.data.repository

import com.example.androidplayer.data.model.*
import com.example.androidplayer.data.network.Esp32PlayerApi
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Репозиторий для работы с ESP32 плеером
 * Абстрагирует источники данных и предоставляет единый интерфейс
 */
@Singleton
class Esp32PlayerRepository @Inject constructor(
    private val api: Esp32PlayerApi
) {
    
    // ========================================================================
    // ПЛЕЕР УПРАВЛЕНИЕ
    // ========================================================================
    
    suspend fun getPlayerStatus(): Result<PlayerStatus> = safeApiCall {
        api.getPlayerStatus()
    }
    
    suspend fun getPlaylist(): Result<List<String>> = safeApiCall {
        api.getPlaylist()
    }
    
    suspend fun sendPlayerCommand(command: String, track: Int? = null, position: Int? = null): Result<String> = safeApiCall {
        api.sendPlayerCommand(command, track, position)
    }
    
    suspend fun playTrack(trackIndex: Int): Result<String> = safeApiCall {
        api.sendPlayerCommand("play", trackIndex)
    }
    
    suspend fun pausePlayback(): Result<String> = safeApiCall {
        api.sendPlayerCommand("pause")
    }
    
    suspend fun resumePlayback(): Result<String> = safeApiCall {
        api.sendPlayerCommand("play")
    }
    
    suspend fun nextTrack(): Result<String> = safeApiCall {
        api.sendPlayerCommand("next")
    }
    
    suspend fun previousTrack(): Result<String> = safeApiCall {
        api.sendPlayerCommand("prev")
    }
    
    suspend fun switchMode(): Result<String> = safeApiCall {
        api.sendPlayerCommand("switch_mode")
    }
    
    suspend fun setPosition(positionMs: Int): Result<String> = safeApiCall {
        api.sendPlayerCommand("set_pos", position = positionMs)
    }
    
    // ========================================================================
    // WI-FI УПРАВЛЕНИЕ
    // ========================================================================
    
    suspend fun getWifiSettings(): Result<WifiSettings> = safeApiCall {
        api.getWifiSettings()
    }
    
    suspend fun setWifiSettings(settings: WifiSettings): Result<String> = safeApiCall {
        api.setWifiSettings(settings)
    }
    
    suspend fun toggleSniffer(enabled: Boolean): Result<String> = safeApiCall {
        api.toggleSniffer("set_sniffer", if (enabled) 1 else 0)
    }
    
    // ========================================================================
    // СИСТЕМНЫЕ НАСТРОЙКИ
    // ========================================================================
    
    suspend fun getSleepSettings(): Result<SleepSettings> = safeApiCall {
        api.getSleepSettings()
    }
    
    suspend fun setSleepSettings(settings: SleepSettings): Result<String> = safeApiCall {
        api.setSleepSettings(settings)
    }
    
    suspend fun getLogLevel(): Result<String> = safeApiCall {
        api.getLogLevel()
    }
    
    suspend fun setLogLevel(level: String): Result<String> = safeApiCall {
        api.setLogLevel(level)
    }
    
    suspend fun getVerboseLogs(): Result<Boolean> = safeApiCall {
        api.getVerboseLogs() == "1"
    }
    
    suspend fun setVerboseLogs(enabled: Boolean): Result<String> = safeApiCall {
        api.setVerboseLogs(if (enabled) "1" else "0")
    }
    
    suspend fun getComponentLogs(): Result<ComponentLogSettings> = safeApiCall {
        api.getComponentLogs()
    }
    
    suspend fun setComponentLogs(settings: ComponentLogSettings): Result<String> = safeApiCall {
        api.setComponentLogs(settings)
    }
    
    // ========================================================================
    // OTA ОБНОВЛЕНИЯ
    // ========================================================================
    
    suspend fun getOtaStatus(): Result<OtaStatus> = safeApiCall {
        api.getOtaStatus()
    }
    
    suspend fun uploadMasterFirmware(firmware: ByteArray): Result<String> = safeApiCall {
        api.uploadMasterFirmware(firmware)
    }
    
    suspend fun uploadSlaveFirmware(firmware: ByteArray): Result<String> = safeApiCall {
        api.uploadSlaveFirmware(firmware)
    }
    
    // ========================================================================
    // МОНИТОРИНГ
    // ========================================================================
    
    suspend fun getSystemLogs(): Result<String> = safeApiCall {
        api.getSystemLogs()
    }
    
    suspend fun getVersionInfo(): Result<VersionInfo> = safeApiCall {
        api.getVersionInfo()
    }
    
    suspend fun restartDevice(): Result<String> = safeApiCall {
        api.restartDevice()
    }
    
    suspend fun getCanStatistics(): Result<CanStatistics> = safeApiCall {
        api.getCanStatistics()
    }
    
    suspend fun getSystemInfo(): Result<SystemInfo> = safeApiCall {
        api.getSystemInfo()
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
            val status = api.getPlayerStatus()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}