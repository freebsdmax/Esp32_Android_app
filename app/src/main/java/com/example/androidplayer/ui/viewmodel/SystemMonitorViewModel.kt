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
 * ViewModel для мониторинга системы
 */
@HiltViewModel
class SystemMonitorViewModel @Inject constructor(
    private val repository: Esp32PlayerRepository
,
    private val bleClient: BlePlayerClient
) : ViewModel() {
    init {
        viewModelScope.launch {
            var wasReady = bleClient.serviceReady.value
            bleClient.serviceReady.collect { ready ->
                if (ready && !wasReady) {
                    Timber.d("BLE reconnected: refreshing SystemMonitor")
                    loadSystemLogs(); loadSystemInfo(); loadCanStatistics(); loadCurrentLogSettings()
                }
                wasReady = ready
            }
        }
    }

    
    private val _uiState = MutableStateFlow(SystemMonitorUiState())
    val uiState: StateFlow<SystemMonitorUiState> = _uiState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        // Загружаем начальные данные
        loadSystemLogs()
        loadSystemInfo()
        loadCanStatistics()
        
        // Запускаем автообновление логов
        startLogsAutoRefresh()
    }
    
    /**
     * Загрузка системных логов
     */
    fun loadSystemLogs() {
        // No BLE link yet (e.g. the screen was opened during a reconnect):
        // just stay silent instead of showing a pointless error in the tray.
        if (!bleClient.serviceReady.value) {
            Timber.d("Logs: BLE is not ready, skipping load")
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.getSystemLogs().fold(
                    onSuccess = { logs ->
                        _uiState.value = _uiState.value.copy(
                            systemLogs = logs,
                            lastLogsUpdate = System.currentTimeMillis()
                        )
                        Timber.d("System logs loaded: ${logs.length} characters")
                    },
                    onFailure = { error ->
                        Timber.e(error, "Failed to load system logs")
                        // A dropped link is not an error worth a notification.
                        if (bleClient.serviceReady.value) {
                            _error.value = tr("Помилка завантаження логів: %s")
                                .format(error.message ?: error.toString())
                        }
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Загрузка системной информации
     */
    fun loadSystemInfo() {
        viewModelScope.launch {
            repository.getSystemInfo().fold(
                onSuccess = { systemInfo ->
                    _uiState.value = _uiState.value.copy(
                        systemInfo = systemInfo
                    )
                    Timber.d("System info loaded")
                },
                onFailure = { error ->
                    Timber.e(error, "Failed to load system info")
                }
            )
        }
    }
    
    /**
     * Загрузка статистики CAN
     */
    fun loadCanStatistics() {
        viewModelScope.launch {
            repository.getCanStatistics().fold(
                onSuccess = { canStats ->
                    _uiState.value = _uiState.value.copy(
                        canStatistics = canStats
                    )
                    Timber.d("CAN statistics loaded: ${canStats.framesPerSecond} fps")
                },
                onFailure = { error ->
                    Timber.e(error, "Failed to load CAN statistics")
                }
            )
        }
    }
    
    /**
     * Очистка логов
     */
    fun clearSystemLogs() {
        _uiState.value = _uiState.value.copy(
            systemLogs = "",
            lastLogsUpdate = System.currentTimeMillis()
        )
        Timber.d("System logs cleared")
    }
    
    /**
     * Автообновление логов каждые 3 секунды
     */
    private fun startLogsAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(3000)
                if (!bleClient.serviceReady.value) continue
                repository.getSystemLogs().fold(
                    onSuccess = { logs ->
                        val currentState = _uiState.value
                        if (logs != currentState.systemLogs) {
                            _uiState.value = currentState.copy(
                                systemLogs = logs,
                                lastLogsUpdate = System.currentTimeMillis()
                            )
                        }
                    },
                    onFailure = { error ->
                        Timber.w(error, "Auto-refresh logs failed")
                    }
                )
            }
        }
    }
    
    /**
     * Ручное обновление всех данных
     */
    fun refreshAllData() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                loadSystemLogs()
                loadSystemInfo()
                loadCanStatistics()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Обновление настроек логирования
     */
    fun updateLogSettings(
        logLevel: String,
        verboseLogs: Boolean,
        componentSettings: ComponentLogSettings
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            
            val results = mutableListOf<Result<Any>>()
            
            // Устанавливаем уровень логов
            repository.setLogLevel(logLevel).fold(
                onSuccess = { results.add(Result.success("Log level set")) },
                onFailure = { results.add(Result.failure(it)) }
            )
            
            // Устанавливаем verbose логи
            repository.setVerboseLogs(verboseLogs).fold(
                onSuccess = { results.add(Result.success("Verbose logs set")) },
                onFailure = { results.add(Result.failure(it)) }
            )
            
            // Устанавливаем настройки компонентов
            repository.setComponentLogs(componentSettings).fold(
                onSuccess = { results.add(Result.success("Component logs set")) },
                onFailure = { results.add(Result.failure(it)) }
            )
            
            val hasErrors = results.any { it.isFailure }
            if (hasErrors) {
                _error.value = tr("Помилка збереження налаштувань логування")
                Timber.e("Failed to update log settings")
            } else {
                _uiState.value = _uiState.value.copy(
                    logLevel = logLevel,
                    verboseLogs = verboseLogs,
                    componentLogSettings = componentSettings
                )
                Timber.d("Log settings updated successfully")
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Загрузка текущих настроек логирования
     */
    fun loadCurrentLogSettings() {
        viewModelScope.launch {
            try {
                // Загружаем уровень логов
                repository.getLogLevel().fold(
                    onSuccess = { level ->
                        _uiState.value = _uiState.value.copy(logLevel = level)
                    },
                    onFailure = { Timber.e(it, "Failed to load log level") }
                )
                
                // Загружаем verbose настройки
                repository.getVerboseLogs().fold(
                    onSuccess = { enabled ->
                        _uiState.value = _uiState.value.copy(verboseLogs = enabled)
                    },
                    onFailure = { Timber.e(it, "Failed to load verbose logs") }
                )
                
                // Загружаем настройки компонентов
                repository.getComponentLogs().fold(
                    onSuccess = { settings ->
                        _uiState.value = _uiState.value.copy(componentLogSettings = settings)
                    },
                    onFailure = { Timber.e(it, "Failed to load component logs") }
                )
            } catch (e: Exception) {
                Timber.e(e, "Failed to load current log settings")
            }
        }
    }
    
    /**
     * Форматирование системной информации для отображения
     */
    fun getFormattedSystemInfo(): String {
        val systemInfo = _uiState.value.systemInfo ?: return tr("Немає даних")
        
        return buildString {
            appendLine(tr("=== ІНФОРМАЦІЯ ПРО СИСТЕМУ ==="))
            appendLine(tr("Версія прошивки: %s").format(systemInfo.firmwareVersion))
            appendLine(tr("Дата збірки: %s %s").format(systemInfo.buildDate, systemInfo.buildTime))
            appendLine()
            appendLine(tr("=== ПАМ'ЯТЬ ==="))
            appendLine(tr("Heap: %s вільно з %s").format(formatBytes(systemInfo.freeHeap.toLong()), formatBytes(systemInfo.totalHeap.toLong())))
            if (systemInfo.freePsram != null && systemInfo.totalPsram != null) {
                appendLine(tr("PSRAM: %s вільно з %s").format(formatBytes(systemInfo.freePsram.toLong()), formatBytes(systemInfo.totalPsram.toLong())))
            }
            appendLine()
            appendLine(tr("=== ЧАС РОБОТИ ==="))
            appendLine(tr("Час роботи: %s").format(formatUptime(systemInfo.uptimeMs)))
        }
    }
    
    /**
     * Форматирование статистики CAN
     */
    fun getFormattedCanStatistics(): String {
        val canStats = _uiState.value.canStatistics ?: return tr("Немає даних")
        
        return buildString {
            appendLine(tr("=== CAN СТАТИСТИКА ==="))
            appendLine(tr("Кадрів за секунду: %d").format(canStats.framesPerSecond))
            appendLine(tr("Байт за секунду: %s").format(formatBytes(canStats.bytesPerSecond.toLong())))
            appendLine(tr("Всього кадрів: %d").format(canStats.totalFrames))
            appendLine(tr("Всього байт: %s").format(formatBytes(canStats.totalBytes)))
            appendLine(tr("Швидкість CAN: %d кбіт/с").format(canStats.bitrate))
        }
    }
    
    /**
     * Форматирование байт
     */
    private fun formatBytes(bytes: Long): String {
        return when {
            bytes < 1024 -> tr("%d Б").format(bytes)
            bytes < 1024 * 1024 -> tr("%d КБ").format(bytes / 1024)
            else -> tr("%d МБ").format(bytes / (1024 * 1024))
        }
    }
    
    /**
     * Форматирование времени работы
     */
    private fun formatUptime(uptimeMs: Long): String {
        val seconds = uptimeMs / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        
        return when {
            days > 0 -> tr("%d дн %d ч").format(days, hours % 24)
            hours > 0 -> tr("%d ч %d хв").format(hours, minutes % 60)
            minutes > 0 -> tr("%d хв %d с").format(minutes, seconds % 60)
            else -> tr("%d с").format(seconds)
        }
    }
    
    /**
     * Очистка ошибки
     */
    fun clearError() {
        _error.value = null
    }
    
    override fun onCleared() {
        super.onCleared()
        Timber.d("SystemMonitorViewModel cleared")
    }
}

/**
 * UI состояние мониторинга системы
 */
data class SystemMonitorUiState(
    val systemLogs: String = "",
    val lastLogsUpdate: Long = 0,
    val systemInfo: SystemInfo? = null,
    val canStatistics: CanStatistics? = null,
    val logLevel: String = "2", // По умолчанию WARN
    val verboseLogs: Boolean = false,
    val componentLogSettings: ComponentLogSettings = ComponentLogSettings()
)