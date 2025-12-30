package com.example.androidplayer.ui.navigation

/**
 * Навигационные константы для приложения
 */
object NavConstants {
    const val PLAYER_SCREEN = "player"
    const val SETTINGS_SCREEN = "settings"
    const val WIFI_SETTINGS_SCREEN = "wifi_settings"
    const val OTA_SCREEN = "ota"
    const val SYSTEM_MONITOR_SCREEN = "system_monitor"
}

/**
 * Аргументы для навигации
 */
object NavArguments {
    const val ESP32_IP_ADDRESS = "esp32_ip"
}

/**
 * Маршруты навигации
 */
object NavRoutes {
    const val PLAYER = "/player"
    const val SETTINGS = "/settings"
    const val WIFI_SETTINGS = "/wifi_settings"
    const val OTA = "/ota"
    const val SYSTEM_MONITOR = "/system_monitor"
}