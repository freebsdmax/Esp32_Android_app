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
    private val repository: Esp32PlayerRepository,
    private val checker: com.example.androidplayer.data.update.UpdateChecker
) : ViewModel() {
    
    private val prefs = context.getSharedPreferences("esp32_player_prefs", Context.MODE_PRIVATE)
    
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()
    
    init {
        loadSettings()
        loadSleepSettings()
        loadSeekConfig()
        loadCanConfig()
        loadAuthConfig()
        loadBleName()
        loadLogsConfig()
        loadAudioProfile()
        loadNetConfig()
    }

    /** Крок і метод перемотки зберігаються на платі (NVS Master + Slave). */
    fun loadSeekConfig() {
        viewModelScope.launch {
            repository.getSeekConfig().onSuccess { cfg ->
                _uiState.value = _uiState.value.copy(
                    seekStepSeconds = cfg.stepSeconds,
                    seekHold = cfg.hold
                )
            }.onFailure { Timber.w(it, "Failed to load seek config") }
        }
    }

    fun setSeekConfig(stepSeconds: Int, hold: Boolean) {
        _uiState.value = _uiState.value.copy(seekStepSeconds = stepSeconds, seekHold = hold)
        viewModelScope.launch {
            repository.setSeekConfig(stepSeconds, hold).fold(
                onSuccess = { cfg ->
                    _uiState.value = _uiState.value.copy(
                        seekStepSeconds = cfg.stepSeconds,
                        seekHold = cfg.hold,
                        isSaved = true,
                        saveMessage = tr("Налаштування перемотки збережено")
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaved = false,
                        saveMessage = error.message ?: "Failed to save seek settings"
                    )
                }
            )
        }
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
            miniPlayerAlways = miniPlayerAlways,
            updateAuto = checker.autoEnabled,
            updateIntervalHours = checker.intervalHours,
            updateBeta = checker.betaChannel
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
    fun setUpdateAuto(enabled: Boolean) {
        checker.autoEnabled = enabled
        _uiState.value = _uiState.value.copy(updateAuto = enabled)
    }

    fun setUpdateInterval(hours: Int) {
        checker.intervalHours = hours
        _uiState.value = _uiState.value.copy(updateIntervalHours = hours)
    }

    fun setUpdateChannel(beta: Boolean) {
        checker.betaChannel = beta
        _uiState.value = _uiState.value.copy(updateBeta = beta)
    }

    fun loadNetConfig() {
        viewModelScope.launch {
            repository.getNetConfig().onSuccess { cfg ->
                _uiState.value = _uiState.value.copy(
                    netStatic = cfg.useStatic,
                    netIp = cfg.ip,
                    netGw = cfg.gw,
                    netMask = cfg.mask,
                    netDns1 = cfg.dns1,
                    netDns2 = cfg.dns2
                )
            }.onFailure { Timber.w(it, "Failed to load network config") }
        }
    }

    fun setNetField(key: String, value: String) {
        val st = _uiState.value
        _uiState.value = when (key) {
            "ip" -> st.copy(netIp = value)
            "gw" -> st.copy(netGw = value)
            "mask" -> st.copy(netMask = value)
            "dns1" -> st.copy(netDns1 = value)
            "dns2" -> st.copy(netDns2 = value)
            else -> st
        }
    }

    fun setNetStatic(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(netStatic = enabled)
    }

    fun saveNetConfig() {
        val st = _uiState.value
        viewModelScope.launch {
            repository.setNetConfig(
                com.example.androidplayer.data.ble.BlePlayerClient.NetConfig(
                    st.netStatic, st.netIp, st.netGw, st.netMask, st.netDns1, st.netDns2
                )
            ).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSaved = true,
                        saveMessage = tr("Мережу збережено — застосується після перезавантаження")
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaved = false,
                        saveMessage = error.message ?: "Failed to save network settings"
                    )
                }
            )
        }
    }

    fun loadAudioProfile() {
        viewModelScope.launch {
            repository.getAudioProfile().onSuccess { p ->
                _uiState.value = _uiState.value.copy(audioProfile = p)
            }.onFailure { Timber.w(it, "Failed to load audio profile") }
        }
    }

    fun saveAudioProfile(profile: Int) {
        _uiState.value = _uiState.value.copy(audioProfile = profile)
        viewModelScope.launch {
            repository.setAudioProfile(profile).fold(
                onSuccess = { applied ->
                    _uiState.value = _uiState.value.copy(
                        audioProfile = applied,
                        isSaved = true,
                        saveMessage = tr("Профіль збережено — застосується після перезавантаження")
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaved = false,
                        saveMessage = error.message ?: "Failed to save profile"
                    )
                }
            )
        }
    }

    fun loadLogsConfig() {
        viewModelScope.launch {
            repository.getLogsConfig().onSuccess { cfg ->
                _uiState.value = _uiState.value.copy(
                    logBufferKb = cfg.bufferKb,
                    logPersist = cfg.persist
                )
            }.onFailure { Timber.w(it, "Failed to load logs config") }
        }
    }

    fun setLogBufferKb(kb: Int) {
        _uiState.value = _uiState.value.copy(logBufferKb = kb)
    }

    fun setLogPersist(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(logPersist = enabled)
    }

    fun saveLogsConfig() {
        val st = _uiState.value
        viewModelScope.launch {
            repository.setLogsConfig(
                com.example.androidplayer.data.ble.BlePlayerClient.LogsConfig(st.logBufferKb, st.logPersist)
            ).fold(
                onSuccess = { cfg ->
                    _uiState.value = _uiState.value.copy(
                        logBufferKb = cfg.bufferKb,
                        logPersist = cfg.persist,
                        isSaved = true,
                        saveMessage = tr("Налаштування логів збережено (буфер — після перезавантаження)")
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaved = false,
                        saveMessage = error.message ?: "Failed to save log settings"
                    )
                }
            )
        }
    }

    fun loadBleName() {
        viewModelScope.launch {
            repository.getBleName().onSuccess { name ->
                _uiState.value = _uiState.value.copy(bleName = name)
            }.onFailure { Timber.w(it, "Failed to load BLE name") }
        }
    }

    fun setBleNameText(text: String) {
        _uiState.value = _uiState.value.copy(bleName = text.take(31))
    }

    fun saveBleName() {
        val name = _uiState.value.bleName.trim()
        if (name.isEmpty()) return
        viewModelScope.launch {
            repository.setBleName(name).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        isSaved = true,
                        saveMessage = tr("Ім'я збережено — застосується після перезавантаження плати")
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaved = false,
                        saveMessage = error.message ?: "Failed to save name"
                    )
                }
            )
        }
    }

    fun loadAuthConfig() {
        viewModelScope.launch {
            repository.getAuth().onSuccess { cfg ->
                _uiState.value = _uiState.value.copy(
                    webToken = cfg.token,
                    webTokenEnabled = cfg.enabled
                )
            }.onFailure { Timber.w(it, "Failed to load auth config") }
        }
    }

    fun setWebToken(text: String) {
        _uiState.value = _uiState.value.copy(webToken = text.take(32))
    }

    fun saveWebToken() {
        val token = _uiState.value.webToken.trim()
        viewModelScope.launch {
            repository.setAuth(token).fold(
                onSuccess = { cfg ->
                    _uiState.value = _uiState.value.copy(
                        webToken = cfg.token,
                        webTokenEnabled = cfg.enabled,
                        isSaved = true,
                        saveMessage = if (cfg.enabled) tr("Токен збережено — веб-інтерфейс захищено")
                        else tr("Токен вимкнено — доступ відкритий")
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaved = false,
                        saveMessage = error.message ?: "Failed to save token"
                    )
                }
            )
        }
    }

    fun loadCanConfig() {
        viewModelScope.launch {
            repository.getCanConfig().onSuccess { cfg ->
                _uiState.value = _uiState.value.copy(
                    canBitrate = cfg.bitrate,
                    canId = cfg.id,
                    canMaskNext = cfg.maskNext,
                    canMaskPrev = cfg.maskPrev,
                    canMaskMode = cfg.maskMode,
                    canDebounceMs = cfg.debounceMs
                )
            }.onFailure { Timber.w(it, "Failed to load CAN config") }
        }
    }

    fun saveCanConfig() {
        val st = _uiState.value
        viewModelScope.launch {
            val cfg = com.example.androidplayer.data.ble.BlePlayerClient.CanConfig(
                bitrate = st.canBitrate,
                id = st.canId,
                maskNext = st.canMaskNext,
                maskPrev = st.canMaskPrev,
                maskMode = st.canMaskMode,
                debounceMs = st.canDebounceMs
            )
            repository.setCanConfig(cfg).fold(
                onSuccess = { saved ->
                    _uiState.value = _uiState.value.copy(
                        canBitrate = saved.bitrate,
                        canId = saved.id,
                        canMaskNext = saved.maskNext,
                        canMaskPrev = saved.maskPrev,
                        canMaskMode = saved.maskMode,
                        canDebounceMs = saved.debounceMs,
                        isSaved = true,
                        saveMessage = tr("Налаштування CAN збережено")
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isSaved = false,
                        saveMessage = error.message ?: "Failed to save CAN settings"
                    )
                }
            )
        }
    }

    fun setCanField(key: String, value: Long) {
        val st = _uiState.value
        _uiState.value = when (key) {
            "bitrate" -> st.copy(canBitrate = value.toInt())
            "id" -> st.copy(canId = value)
            "mask_next" -> st.copy(canMaskNext = value.toInt())
            "mask_prev" -> st.copy(canMaskPrev = value.toInt())
            "mask_mode" -> st.copy(canMaskMode = value.toInt())
            "debounce" -> st.copy(canDebounceMs = value.toInt())
            else -> st
        }
    }

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
    val seekStepSeconds: Int = 10,
    val seekHold: Boolean = false,
    val canBitrate: Int = 125,
    val canId: Long = 0x2D5,
    val canMaskNext: Int = 0x40,
    val canMaskPrev: Int = 0x80,
    val canMaskMode: Int = 0x10,
    val canDebounceMs: Int = 500,
    val webToken: String = "",
    val webTokenEnabled: Boolean = false,
    val bleName: String = "",
    val logBufferKb: Int = 4,
    val logPersist: Boolean = true,
    val updateAuto: Boolean = true,
    val updateIntervalHours: Int = 12,
    val updateBeta: Boolean = false,
    val audioProfile: Int = 1,
    val netStatic: Boolean = false,
    val netIp: String = "",
    val netGw: String = "",
    val netMask: String = "255.255.255.0",
    val netDns1: String = "",
    val netDns2: String = "",
    val sleepMode: Int = 0,
    val sleepTimeoutSec: Int = 30,
    val isSaved: Boolean = false,
    val saveMessage: String? = null
)