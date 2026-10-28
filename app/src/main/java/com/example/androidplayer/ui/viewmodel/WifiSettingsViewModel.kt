package com.example.androidplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidplayer.data.model.*
import com.example.androidplayer.data.repository.Esp32PlayerRepository
import com.example.androidplayer.data.ble.BlePlayerClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.example.androidplayer.ui.i18n.tr

/**
 * ViewModel для настроек Wi-Fi
 */
@HiltViewModel
class WifiSettingsViewModel @Inject constructor(
    private val repository: Esp32PlayerRepository
,
    private val bleClient: BlePlayerClient
) : ViewModel() {
    init {
        viewModelScope.launch {
            var wasReady = bleClient.serviceReady.value
            bleClient.serviceReady.collect { ready ->
                if (ready && !wasReady) {
                    Timber.d("BLE reconnected: refreshing WifiSettings")
                    loadWifiSettings()
                }
                wasReady = ready
            }
        }
    }

    
    private val _uiState = MutableStateFlow(WifiSettingsUiState())
    val uiState: StateFlow<WifiSettingsUiState> = _uiState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadWifiSettings()
    }
    
    /**
     * Загрузка настроек Wi-Fi
     */
    fun loadWifiSettings() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getWifiSettings().fold(
                onSuccess = { settings ->
                    _uiState.value = WifiSettingsUiState(
                        staSsid = settings.staSsid,
                        staPassword = settings.staPassword,
                        apSsid = settings.apSsid,
                        apPassword = settings.apPassword,
                        mode = settings.mode
                    )
                    Timber.d("WiFi settings loaded: mode=${settings.mode}")
                },
                onFailure = { error ->
                    _error.value = tr("Помилка завантаження налаштувань Wi-Fi: %s").format(error.message ?: error.toString())
                    Timber.e(error, "Failed to load WiFi settings")
                }
            )
            
            _isLoading.value = false
        }
    }
    
    /**
     * Обновление STA SSID
     */
    fun updateStaSsid(ssid: String) {
        _uiState.value = _uiState.value.copy(staSsid = ssid)
    }
    
    /**
     * Обновление STA пароля
     */
    fun updateStaPassword(password: String) {
        _uiState.value = _uiState.value.copy(staPassword = password)
    }
    
    /**
     * Обновление AP SSID
     */
    fun updateApSsid(ssid: String) {
        _uiState.value = _uiState.value.copy(apSsid = ssid)
    }
    
    /**
     * Обновление AP пароля
     */
    fun updateApPassword(password: String) {
        _uiState.value = _uiState.value.copy(apPassword = password)
    }
    
    /**
     * Обновление режима Wi-Fi
     */
    fun updateMode(mode: Int) {
        _uiState.value = _uiState.value.copy(mode = mode)
    }
    
    /**
     * Сохранение настроек Wi-Fi
     */
    fun saveWifiSettings() {
        val currentState = _uiState.value
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val settings = WifiSettings(
                staSsid = currentState.staSsid,
                staPassword = currentState.staPassword,
                apSsid = currentState.apSsid,
                apPassword = currentState.apPassword,
                mode = currentState.mode
            )
            
            repository.setWifiSettings(settings).fold(
                onSuccess = { response ->
                    _uiState.value = currentState.copy(
                        isSaved = true,
                        saveMessage = tr("Налаштування збережено. Перезавантажте пристрій для застосування.")
                    )
                    Timber.d("WiFi settings saved")
                },
                onFailure = { error ->
                    _error.value = tr("Помилка збереження налаштувань: %s").format(error.message ?: error.toString())
                    Timber.e(error, "Failed to save WiFi settings")
                }
            )
            
            _isLoading.value = false
        }
    }
    
    /**
     * Очистка сообщения о сохранении
     */
    fun clearSaveMessage() {
        _uiState.value = _uiState.value.copy(
            isSaved = false,
            saveMessage = null
        )
    }
    
    /**
     * Очистка ошибки
     */
    fun clearError() {
        _error.value = null
    }
}

/**
 * UI состояние настроек Wi-Fi
 */
data class WifiSettingsUiState(
    val staSsid: String = "",
    val staPassword: String = "",
    val apSsid: String = "",
    val apPassword: String = "",
    val mode: Int = 0, // 0 = AP, 1 = STA
    val isSaved: Boolean = false,
    val saveMessage: String? = null
)