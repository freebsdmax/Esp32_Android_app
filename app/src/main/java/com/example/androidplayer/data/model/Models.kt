package com.example.androidplayer.data.model

import com.google.gson.annotations.SerializedName

/**
 * Модель статуса плеера
 */
data class PlayerStatus(
    @SerializedName("current_track")
    val currentTrack: Int,
    
    @SerializedName("position_ms")
    val positionMs: Int,
    
    @SerializedName("duration_ms")
    val durationMs: Int,
    
    @SerializedName("is_paused")
    val isPaused: Boolean,
    
    @SerializedName("playlist_size")
    val playlistSize: Int,
    
    @SerializedName("mode")
    val mode: String,
    
    @SerializedName("sniffer_enabled")
    val snifferEnabled: Boolean
)

/**
 * Модель трека в плейлисте
 */
data class Track(
    @SerializedName("name")
    val name: String,
    
    @SerializedName("path")
    val path: String,
    
    @SerializedName("duration")
    val duration: Int? = null,
    
    @SerializedName("size")
    val size: Long? = null
)

/**
 * Модель плейлиста
 */
data class Playlist(
    val tracks: List<Track>
)

/**
 * Модель настроек Wi-Fi
 */
data class WifiSettings(
    @SerializedName("sta_ssid")
    val staSsid: String,
    
    @SerializedName("sta_password")
    val staPassword: String,
    
    @SerializedName("ap_ssid")
    val apSsid: String,
    
    @SerializedName("ap_password")
    val apPassword: String,
    
    @SerializedName("mode")
    val mode: Int // 0 = AP, 1 = STA
)

/**
 * Модель настроек сна
 */
data class SleepSettings(
    @SerializedName("sleep_mode")
    val sleepMode: Int, // 0=disabled, 1=light sleep, 2=modem sleep, 3=deep sleep
    
    @SerializedName("idle_timeout_sec")
    val idleTimeoutSec: Int // idle timeout in seconds (minimum 10)
)

/**
 * Модель CAN статистики
 */
data class CanStatistics(
    @SerializedName("frames_per_second")
    val framesPerSecond: Int,
    
    @SerializedName("bytes_per_second")
    val bytesPerSecond: Int,
    
    @SerializedName("total_frames")
    val totalFrames: Long,
    
    @SerializedName("total_bytes")
    val totalBytes: Long,
    
    @SerializedName("bitrate")
    val bitrate: Int
)

/**
 * Модель системной информации
 */
data class SystemInfo(
    @SerializedName("free_heap")
    val freeHeap: Int,
    
    @SerializedName("total_heap")
    val totalHeap: Int,
    
    @SerializedName("free_psram")
    val freePsram: Int? = null,
    
    @SerializedName("total_psram")
    val totalPsram: Int? = null,
    
    @SerializedName("uptime_ms")
    val uptimeMs: Long,
    
    @SerializedName("firmware_version")
    val firmwareVersion: String,
    
    @SerializedName("build_date")
    val buildDate: String,
    
    @SerializedName("build_time")
    val buildTime: String
)

/**
 * Модель настроек логов компонентов
 */
data class ComponentLogSettings(
    val player: Int = 1,
    val wifi_can_sniffer: Int = 1,
    val web_ota: Int = 1,
    val uart_ota: Int = 1,
    val elm327: Int = 1,
    val pipeline: Int = 1,
    val can: Int = 1,
    val usb: Int = 1,
    val ble: Int = 1,
    val wifi: Int = 1,
    val sleep: Int = 1,
    val playlist: Int = 1,
    val console: Int = 1,
    val link: Int = 1,
    val nvs: Int = 1,
    // Module-level switches (what the UI actually edits)
    val group_player: Int = 1,
    val group_connect: Int = 1,
    val group_ota: Int = 1,
    val group_car: Int = 1,
    val group_system: Int = 1
)

/**
 * Модель статуса OTA
 */
data class OtaStatus(
    val active: Boolean = false,
    val is_uart_slave: Boolean = false,
    val total: Int = 0,
    val written: Int = 0,
    val block: Int = 0,
    val player_build_date: String = "",
    val player_build_time: String = "",
    val web_build_date: String = "",
    val web_build_time: String = "",
    val uart_build_date: String = "",
    val uart_build_time: String = "",
    val slave_main_build_date: String = "",
    val slave_main_build_time: String = "",
    val slave_crc_build_date: String = "",
    val slave_crc_build_time: String = "",
    val slave_uart_build_date: String = "",
    val slave_uart_build_time: String = "",

)

/**
 * Модель информации о версиях
 */
data class VersionInfo(
    val player_build_date: String,
    val player_build_time: String,
    val web_build_date: String,
    val web_build_time: String,
    val uart_build_date: String,
    val uart_build_time: String,
    val slave_main_build_date: String,
    val slave_main_build_time: String,
    val slave_crc_build_date: String,
    val slave_crc_build_time: String,
    val slave_uart_build_date: String,
    val slave_uart_build_time: String,

    /** Версія прошивки Slave (git-тег) */
    val slave_fw_version: String = "unknown"
)

/**
 * Модель ответа API
 */
data class ApiResponse<T>(
    val success: Boolean,
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
)

/**
 * Модель команды управления плеером
 */
data class PlayerCommand(
    val command: String,
    val track: Int? = null,
    val position: Int? = null,
    val volume: Int? = null
)

/**
 * Модель CAN сообщения
 */
data class CanMessage(
    val id: Int,
    val length: Int,
    val data: List<Int>,
    val timestamp: Long? = null
)

/**
 * Модель настроек приложения
 */
data class AppSettings(
    val autoReconnect: Boolean = true,
    val showNotifications: Boolean = true,
    val themeMode: String = "system", // "light", "dark", "system"
    val defaultVolume: Int = 50,
    val notificationPriority: String = "normal" // "low", "normal", "high"
)