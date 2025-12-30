package com.example.androidplayer.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel для настроек приложения
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    private val prefs = context.getSharedPreferences("esp32_player_prefs", Context.MODE_PRIVATE)
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
    }
    
    /**
     * Загрузка настроек
     */
    private fun loadSettings() {
        val savedIp = prefs.getString("esp32_ip", DEFAULT_ESP32_IP) ?: DEFAULT_ESP32_IP
        val autoReconnect = prefs.getBoolean("auto_reconnect", true)
        val showNotifications = prefs.getBoolean("show_notifications", true)
        val darkTheme = prefs.getBoolean("dark_theme", false)
        
        _uiState.value = SettingsUiState(
            esp32Ip = savedIp,
            autoReconnect = autoReconnect,
            showNotifications = showNotifications,
            darkTheme = darkTheme
        )
    }
    
    /**
     * Обновление IP адреса
     */
    fun updateEsp32Ip(ip: String) {
        _uiState.value = _uiState.value.copy(esp32Ip = ip)
    }
    
    /**
     * Сохранение IP адреса
     */
    fun saveEsp32Ip() {
        val ip = _uiState.value.esp32Ip
        prefs.edit().putString("esp32_ip", ip).apply()
        _uiState.value = _uiState.value.copy(
            isSaved = true,
            saveMessage = "IP адрес сохранен: $ip"
        )
    }
    
    /**
     * Обновление настроек
     */
    fun updateSetting(key: String, value: Boolean) {
        when (key) {
            "auto_reconnect" -> {
                prefs.edit().putBoolean("auto_reconnect", value).apply()
                _uiState.value = _uiState.value.copy(autoReconnect = value)
            }
            "show_notifications" -> {
                prefs.edit().putBoolean("show_notifications", value).apply()
                _uiState.value = _uiState.value.copy(showNotifications = value)
            }
            "dark_theme" -> {
                prefs.edit().putBoolean("dark_theme", value).apply()
                _uiState.value = _uiState.value.copy(darkTheme = value)
            }
        }
    }
    
    /**
     * Сброс сообщения о сохранении
     */
    fun clearSaveMessage() {
        _uiState.value = _uiState.value.copy(
            isSaved = false,
            saveMessage = null
        )
    }
    
    /**
     * Получение сохраненного IP
     */
    fun getSavedIp(): String {
        return prefs.getString("esp32_ip", DEFAULT_ESP32_IP) ?: DEFAULT_ESP32_IP
    }
    
    companion object {
        const val DEFAULT_ESP32_IP = "http://192.168.4.1"
    }
}

/**
 * UI состояние настроек
 */
data class SettingsUiState(
    val esp32Ip: String = SettingsViewModel.DEFAULT_ESP32_IP,
    val autoReconnect: Boolean = true,
    val showNotifications: Boolean = true,
    val darkTheme: Boolean = false,
    val isSaved: Boolean = false,
    val saveMessage: String? = null
)