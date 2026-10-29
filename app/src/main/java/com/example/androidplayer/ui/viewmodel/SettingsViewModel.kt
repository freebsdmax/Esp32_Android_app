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
import com.example.androidplayer.data.model.SleepSettings
import com.example.androidplayer.data.repository.Esp32PlayerRepository
import com.example.androidplayer.ui.i18n.tr
import timber.log.Timber

/**
 * ViewModel для настроек приложения
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: Esp32PlayerRepository
) : ViewModel() {
    
    private val prefs = context.getSharedPreferences("esp32_player_prefs", Context.MODE_PRIVATE)
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
        loadSleepSettings()
    }

    /** Read the sleep/idle configuration from the device. */
    fun loadSleepSettings() {
        viewModelScope.launch {
            repository.getSleepSettings().fold(
                onSuccess = { sleep ->
                    _uiState.value = _uiState.value.copy(
                        sleepMode = sleep.sleepMode,
                        sleepTimeoutSec = sleep.idleTimeoutSec.coerceAtLeast(10)
                    )
                },
                onFailure = { Timber.w(it, "Failed to load sleep settings") }
            )
        }
    }

    fun setSleepMode(mode: Int) {
        _uiState.value = _uiState.value.copy(sleepMode = mode)
    }

    fun setSleepTimeoutSeconds(seconds: Int) {
        _uiState.value = _uiState.value.copy(sleepTimeoutSec = seconds.coerceIn(10, 28800))
    }

    fun saveSleepSettings() {
        val state = _uiState.value
        viewModelScope.launch {
            repository.setSleepSettings(
                SleepSettings(
                    sleepMode = state.sleepMode,
                    idleTimeoutSec = state.sleepTimeoutSec
                )
            ).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSaved = true,
                        saveMessage = tr("Налаштування сну збережено")
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaved = false,
                        saveMessage = error.message ?: "Failed to save sleep settings"
                    )
                }
            )
        }
    }
    
    /**
     * Загрузка настроек
     */
    private fun loadSettings() {
        val savedIp = prefs.getString("esp32_ip", DEFAULT_ESP32_IP) ?: DEFAULT_ESP32_IP
        val autoReconnect = prefs.getBoolean("auto_reconnect", true)
        val showNotifications = prefs.getBoolean("show_notifications", true)
        val darkTheme = prefs.getBoolean("dark_theme", false)
        val miniPlayerAlways = prefs.getBoolean(MINI_PLAYER_ALWAYS, false)
        
        _uiState.value = SettingsUiState(
            esp32Ip = savedIp,
            autoReconnect = autoReconnect,
            showNotifications = showNotifications,
            darkTheme = darkTheme,
            miniPlayerAlways = miniPlayerAlways
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
            saveMessage = tr("IP-адресу збережено: %s").format(ip)
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
            MINI_PLAYER_ALWAYS -> {
                prefs.edit().putBoolean(MINI_PLAYER_ALWAYS, value).apply()
                _uiState.value = _uiState.value.copy(miniPlayerAlways = value)
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

        /** SharedPreferences key: keep the mini player visible in Bluetooth mode. */
        const val MINI_PLAYER_ALWAYS = "mini_player_always"
        const val PREFS_NAME = "esp32_player_prefs"
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
    val miniPlayerAlways: Boolean = false,
    val sleepMode: Int = 0,
    val sleepTimeoutSec: Int = 30,
    val isSaved: Boolean = false,
    val saveMessage: String? = null
)