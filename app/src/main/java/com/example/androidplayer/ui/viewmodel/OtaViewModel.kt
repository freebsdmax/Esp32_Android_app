package com.example.androidplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidplayer.data.model.*
import com.example.androidplayer.data.repository.Esp32PlayerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * ViewModel для OTA обновлений
 */
@HiltViewModel
class OtaViewModel @Inject constructor(
    private val repository: Esp32PlayerRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(OtaUiState())
    val uiState: StateFlow<OtaUiState> = _uiState.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadOtaStatus()
        loadVersionInfo()
    }
    
    /**
     * Загрузка статуса OTA
     */
    fun loadOtaStatus() {
        viewModelScope.launch {
            repository.getOtaStatus().fold(
                onSuccess = { status ->
                    _uiState.value = _uiState.value.copy(
                        isActive = status.active,
                        isUartSlave = status.is_uart_slave,
                        totalBytes = status.total,
                        writtenBytes = status.written,
                        currentBlock = status.block
                    )
                    Timber.d("OTA status loaded: active=${status.active}")
                },
                onFailure = { error ->
                    Timber.e(error, "Failed to load OTA status")
                }
            )
        }
    }
    
    /**
     * Загрузка информации о версиях
     */
    fun loadVersionInfo() {
        viewModelScope.launch {
            repository.getVersionInfo().fold(
                onSuccess = { versionInfo ->
                    _uiState.value = _uiState.value.copy(
                        masterPlayerVersion = "${versionInfo.player_build_date} ${versionInfo.player_build_time}",
                        masterWebVersion = "${versionInfo.web_build_date} ${versionInfo.web_build_time}",
                        masterUartVersion = "${versionInfo.uart_build_date} ${versionInfo.uart_build_time}",
                        slaveMainVersion = if (versionInfo.slave_main_build_date.isNotEmpty()) 
                            "${versionInfo.slave_main_build_date} ${versionInfo.slave_main_build_time}" 
                        else "Не подключен",
                        slaveCrcVersion = if (versionInfo.slave_crc_build_date.isNotEmpty()) 
                            "${versionInfo.slave_crc_build_date} ${versionInfo.slave_crc_build_time}" 
                        else "Не подключен",
                        slaveUartVersion = if (versionInfo.slave_uart_build_date.isNotEmpty()) 
                            "${versionInfo.slave_uart_build_date} ${versionInfo.slave_uart_build_time}" 
                        else "Не подключен"
                    )
                    Timber.d("Version info loaded")
                },
                onFailure = { error ->
                    Timber.e(error, "Failed to load version info")
                }
            )
        }
    }
    
    /**
     * Загрузка прошивки для Master ESP32
     */
    fun uploadMasterFirmware(firmwareFile: File) {
        viewModelScope.launch {
            try {
                val firmwareBytes = firmwareFile.readBytes()
                
                _uiState.value = _uiState.value.copy(
                    isUploading = true,
                    uploadProgress = 0f,
                    uploadTarget = "Master ESP32",
                    errorMessage = null
                )
                
                repository.uploadMasterFirmware(firmwareBytes).fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            uploadProgress = 1f,
                            uploadMessage = "Загрузка прошивки Master ESP32 завершена",
                            isActive = true
                        )
                        Timber.d("Master firmware uploaded successfully")
                        
                        // Начинаем отслеживать прогресс
                        startProgressTracking()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            errorMessage = "Ошибка загрузки прошивки Master: ${error.message}"
                        )
                        Timber.e(error, "Failed to upload master firmware")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    errorMessage = "Ошибка чтения файла: ${e.message}"
                )
                Timber.e(e, "Failed to read firmware file")
            }
        }
    }
    
    /**
     * Загрузка прошивки для Slave ESP32
     */
    fun uploadSlaveFirmware(firmwareFile: File) {
        viewModelScope.launch {
            try {
                val firmwareBytes = firmwareFile.readBytes()
                
                _uiState.value = _uiState.value.copy(
                    isUploading = true,
                    uploadProgress = 0f,
                    uploadTarget = "Slave ESP32 (через UART)",
                    errorMessage = null
                )
                
                repository.uploadSlaveFirmware(firmwareBytes).fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            uploadProgress = 1f,
                            uploadMessage = "Загрузка прошивки Slave ESP32 завершена",
                            isActive = true,
                            isUartSlave = true
                        )
                        Timber.d("Slave firmware uploaded successfully")
                        
                        // Начинаем отслеживать прогресс
                        startProgressTracking()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            errorMessage = "Ошибка загрузки прошивки Slave: ${error.message}"
                        )
                        Timber.e(error, "Failed to upload slave firmware")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    errorMessage = "Ошибка чтения файла: ${e.message}"
                )
                Timber.e(e, "Failed to read firmware file")
            }
        }
    }
    
    /**
     * Отслеживание прогресса OTA
     */
    private fun startProgressTracking() {
        viewModelScope.launch {
            while (true) {
                val status = repository.getOtaStatus().getOrNull() ?: break
                val progress = if (status.total > 0) {
                    status.written.toFloat() / status.total
                } else {
                    0f
                }
                
                _uiState.value = _uiState.value.copy(
                    totalBytes = status.total,
                    writtenBytes = status.written,
                    currentBlock = status.block,
                    uploadProgress = progress,
                    isActive = status.active
                )
                
                if (!status.active) {
                    Timber.d("OTA process completed")
                    break
                }
                
                kotlinx.coroutines.delay(1000)
            }
        }
    }
    
    /**
     * Перезагрузка устройства
     */
    fun restartDevice() {
        viewModelScope.launch {
            repository.restartDevice().fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        restartMessage = "Устройство перезагружается..."
                    )
                    Timber.d("Device restart initiated")
                },
                onFailure = { error ->
                    _error.value = "Ошибка перезагрузки: ${error.message}"
                    Timber.e(error, "Failed to restart device")
                }
            )
        }
    }
    
    /**
     * Очистка ошибки
     */
    fun clearError() {
        _error.value = null
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    /**
     * Очистка сообщения о перезагрузке
     */
    fun clearRestartMessage() {
        _uiState.value = _uiState.value.copy(restartMessage = null)
    }
    
    /**
     * Очистка сообщения о загрузке
     */
    fun clearUploadMessage() {
        _uiState.value = _uiState.value.copy(uploadMessage = null)
    }
}

/**
 * UI состояние OTA
 */
data class OtaUiState(
    val isActive: Boolean = false,
    val isUartSlave: Boolean = false,
    val totalBytes: Int = 0,
    val writtenBytes: Int = 0,
    val currentBlock: Int = 0,
    val uploadProgress: Float = 0f,
    val isUploading: Boolean = false,
    val uploadTarget: String = "",
    val uploadMessage: String? = null,
    val errorMessage: String? = null,
    val restartMessage: String? = null,
    val masterPlayerVersion: String = "",
    val masterWebVersion: String = "",
    val masterUartVersion: String = "",
    val slaveMainVersion: String = "",
    val slaveCrcVersion: String = "",
    val slaveUartVersion: String = ""
)