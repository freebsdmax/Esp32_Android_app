package com.example.androidplayer.data.network

import com.example.androidplayer.data.model.*
import retrofit2.http.*

/**
 * API интерфейс для взаимодействия с ESP32 плеером
 */
interface Esp32PlayerApi {
    
    // ========================================================================
    // ПЛЕЕР УПРАВЛЕНИЕ
    // ========================================================================
    
    @GET("/player_status")
    suspend fun getPlayerStatus(): PlayerStatus
    
    @GET("/playlist")
    suspend fun getPlaylist(): List<String>
    
    @GET("/player_cmd")
    suspend fun sendPlayerCommand(
        @Query("cmd") command: String,
        @Query("track") track: Int? = null,
        @Query("pos") position: Int? = null
    ): String
    
    // ========================================================================
    // WI-FI УПРАВЛЕНИЕ
    // ========================================================================
    
    @GET("/wifi_settings")
    suspend fun getWifiSettings(): WifiSettings
    
    @POST("/wifi_settings")
    suspend fun setWifiSettings(@Body settings: WifiSettings): String
    
    @GET("/player_cmd")
    suspend fun toggleSniffer(
        @Query("cmd") command: String = "set_sniffer",
        @Query("track") enabled: Int
    ): String
    
    // ========================================================================
    // СИСТЕМНЫЕ НАСТРОЙКИ
    // ========================================================================
    
    @GET("/sleep_settings")
    suspend fun getSleepSettings(): SleepSettings
    
    @POST("/sleep_settings")
    suspend fun setSleepSettings(@Body settings: SleepSettings): String
    
    @GET("/log_level")
    suspend fun getLogLevel(): String
    
    @POST("/log_level")
    suspend fun setLogLevel(@Body level: String): String
    
    @GET("/verbose_logs")
    suspend fun getVerboseLogs(): String
    
    @POST("/verbose_logs")
    suspend fun setVerboseLogs(@Body enabled: String): String
    
    @GET("/component_logs")
    suspend fun getComponentLogs(): ComponentLogSettings
    
    @POST("/component_logs")
    suspend fun setComponentLogs(@Body settings: ComponentLogSettings): String
    
    // ========================================================================
    // OTA ОБНОВЛЕНИЯ
    // ========================================================================
    
    @GET("/ota_status")
    suspend fun getOtaStatus(): OtaStatus
    
    @POST("/upload")
    @Headers("Content-Type: application/octet-stream")
    suspend fun uploadMasterFirmware(@Body firmware: ByteArray): String
    
    @POST("/upload_slave")
    @Headers("Content-Type: application/octet-stream")
    suspend fun uploadSlaveFirmware(@Body firmware: ByteArray): String
    
    // ========================================================================
    // МОНИТОРИНГ
    // ========================================================================
    
    @GET("/logs")
    suspend fun getSystemLogs(): String
    
    @GET("/version")
    suspend fun getVersionInfo(): VersionInfo
    
    @POST("/restart")
    suspend fun restartDevice(): String
    
    // ========================================================================
    // CAN СНИФЕР
    // ========================================================================
    
    @GET("/can_statistics")
    suspend fun getCanStatistics(): CanStatistics
    
    @GET("/system_info")
    suspend fun getSystemInfo(): SystemInfo
}