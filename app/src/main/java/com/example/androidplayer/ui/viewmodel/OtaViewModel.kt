package com.example.androidplayer.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidplayer.data.model.*
import com.example.androidplayer.data.repository.Esp32PlayerRepository
import com.example.androidplayer.data.ble.BlePlayerClient
import com.example.androidplayer.ota.OtaNotifications
import com.example.androidplayer.ota.OtaUploadService
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import com.example.androidplayer.ui.i18n.Loc
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import com.example.androidplayer.ui.i18n.tr
import kotlinx.coroutines.delay

/**
 * ViewModel для OTA обновлений
 */
@HiltViewModel
class OtaViewModel @Inject constructor(
    private val repository: Esp32PlayerRepository
,
    private val bleClient: BlePlayerClient,
    @ApplicationContext private val appContext: Context
) : ViewModel() {
    init {
        viewModelScope.launch {
            var wasReady = bleClient.serviceReady.value
            bleClient.serviceReady.collect { ready ->
                if (ready && !wasReady) {
                    Timber.d("BLE reconnected: refreshing Ota")
                    _uiState.value = _uiState.value.copy(restartMessage = null)
                    loadOtaStatus(); loadVersionInfo()
                }
                wasReady = ready
            }
        }
    }

    
    // ---- OTA notifications (visible in the notification shade) ----
    private var otaNotifLabel: String = ""
    private var otaNotifPercent: Int = -1
    private var otaNotifLast = 0L

    private fun startOtaNotification(label: String) {
        otaNotifLabel = label
        otaNotifPercent = -1
        otaNotifLast = 0L
        OtaUploadService.start(appContext)
        OtaNotifications.updateProgress(
            appContext,
            0,
            Loc.otaProgressText(label, 0, 0, -1)
        )
    }

    private fun updateOtaNotification(progress: com.example.androidplayer.data.model.OtaTransferProgress) {
        if (otaNotifLabel.isEmpty()) return
        val now = android.os.SystemClock.elapsedRealtime()
        if (progress.percent == otaNotifPercent && now - otaNotifLast < 1000) return
        if (progress.percent < otaNotifPercent) return
        otaNotifPercent = progress.percent
        otaNotifLast = now
        val label = if (progress.isApplyPhase) {
            "$otaNotifLabel · ${Loc.otaPhaseName(true)}"
        } else {
            otaNotifLabel
        }
        OtaNotifications.updateProgress(
            appContext,
            progress.percent,
            Loc.otaProgressText(
                label,
                progress.percent,
                progress.bytesPerSecond,
                progress.etaSeconds
            )
        )
    }

    private fun finishOtaNotification(success: Boolean, message: String) {
        OtaUploadService.stop(appContext)
        OtaNotifications.showResult(appContext, success, message)
        otaNotifLabel = ""
        otaNotifPercent = -1
    }

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
            val currentState = _uiState.value
            var masterPlayer = currentState.masterPlayerVersion
            var masterWeb = currentState.masterWebVersion
            var masterUart = currentState.masterUartVersion
            var slaveMain = currentState.slaveMainVersion
            var slaveCrc = currentState.slaveCrcVersion
            var slaveUart = currentState.slaveUartVersion

            // Сначала пробуем получить из /version
            repository.getVersionInfo().fold(
                onSuccess = { versionInfo ->
                    masterPlayer = "${versionInfo.player_build_date} ${versionInfo.player_build_time}".trim()
                    masterWeb = "${versionInfo.web_build_date} ${versionInfo.web_build_time}".trim()
                    masterUart = "${versionInfo.uart_build_date} ${versionInfo.uart_build_time}".trim()

                    if (versionInfo.slave_main_build_date.isNotEmpty()) {
                        slaveMain = "${versionInfo.slave_main_build_date} ${versionInfo.slave_main_build_time}".trim()
                    }
                    if (versionInfo.slave_crc_build_date.isNotEmpty()) {
                        slaveCrc = "${versionInfo.slave_crc_build_date} ${versionInfo.slave_crc_build_time}".trim()
                    }
                    if (versionInfo.slave_uart_build_date.isNotEmpty()) {
                        slaveUart = "${versionInfo.slave_uart_build_date} ${versionInfo.slave_uart_build_time}".trim()
                    }
                    Timber.d("Version info loaded from /version")
                },
                onFailure = { error ->
                    Timber.e(error, "Failed to load version info from /version")
                }
            )

            // Дополнительно мерджим из /ota_status (как в веб-морде)
            repository.getOtaStatus().fold(
                onSuccess = { status ->
                    if (status.player_build_date.isNotEmpty() && status.player_build_time.isNotEmpty()) {
                        masterPlayer = "${status.player_build_date} ${status.player_build_time}".trim()
                    }
                    if (status.web_build_date.isNotEmpty() && status.web_build_time.isNotEmpty()) {
                        masterWeb = "${status.web_build_date} ${status.web_build_time}".trim()
                    }
                    if (status.uart_build_date.isNotEmpty() && status.uart_build_time.isNotEmpty()) {
                        masterUart = "${status.uart_build_date} ${status.uart_build_time}".trim()
                    }

                    if (status.slave_main_build_date.isNotEmpty() && status.slave_main_build_time.isNotEmpty()) {
                        slaveMain = "${status.slave_main_build_date} ${status.slave_main_build_time}".trim()
                    }
                    if (status.slave_crc_build_date.isNotEmpty() && status.slave_crc_build_time.isNotEmpty()) {
                        slaveCrc = "${status.slave_crc_build_date} ${status.slave_crc_build_time}".trim()
                    }
                    if (status.slave_uart_build_date.isNotEmpty() && status.slave_uart_build_time.isNotEmpty()) {
                        slaveUart = "${status.slave_uart_build_date} ${status.slave_uart_build_time}".trim()
                    }
                    Timber.d("Version info merged from /ota_status")
                },
                onFailure = { error ->
                    Timber.e(error, "Failed to load version info from /ota_status")
                }
            )

            _uiState.value = _uiState.value.copy(
                masterPlayerVersion = if (masterPlayer.isBlank()) "—" else masterPlayer,
                masterWebVersion = if (masterWeb.isBlank()) "—" else masterWeb,
                masterUartVersion = if (masterUart.isBlank()) "—" else masterUart,
                slaveMainVersion = if (slaveMain.isBlank()) tr("Не підключено") else slaveMain,
                slaveCrcVersion = if (slaveCrc.isBlank()) tr("Не підключено") else slaveCrc,
                slaveUartVersion = if (slaveUart.isBlank()) tr("Не підключено") else slaveUart
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
                            uploadMessage = tr("Прошивку Master ESP32 завантажено"),
                            isActive = true
                        )
                        Timber.d("Master firmware uploaded successfully")
                        
                        // Начинаем отслеживать прогресс
                        startProgressTracking()
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            errorMessage = Loc.otaErrorMaster(error.message ?: error.toString())
                        )
                        Timber.e(error, "Failed to upload master firmware")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    errorMessage = Loc.fileReadError(e.message ?: e.toString())
                )
                Timber.e(e, "Failed to read firmware file")
            }
        }
    }
    

    /**
     * Update Master firmware over BLE (slower than Wi-Fi, works without Wi-Fi).
     */

    fun uploadSlaveFirmwareBle(firmwareFile: File) {
        viewModelScope.launch {
            try {
                val firmwareBytes = firmwareFile.readBytes()
                _uiState.value = _uiState.value.copy(
                    isUploading = true,
                    uploadProgress = 0f,
                    uploadTarget = "Slave ESP32 (BLE → UART)",
                    errorMessage = null
                )
                startOtaNotification("Slave (BLE \u2192 UART)")
                repository.bleFileOtaUpdate(firmwareBytes, "slave") { progress ->
                    _uiState.value = _uiState.value.copy(
                        uploadProgress = progress.percent / 100f,
                        uploadBytes = progress.bytesDone,
                        uploadTotal = progress.bytesTotal,
                        uploadBytesPerSec = progress.bytesPerSecond,
                        uploadEtaSeconds = progress.etaSeconds,
                        uploadPhaseApply = progress.isApplyPhase
                    )
                    updateOtaNotification(progress)
                }.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            uploadProgress = 1f,
                            uploadMessage = "Slave firmware updated via BLE",
                            isActive = false
                        )
                        finishOtaNotification(true, Loc.otaDoneSlave())
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            errorMessage = Loc.otaErrorSlave(error.message ?: error.toString())
                        )
                        finishOtaNotification(
                            false,
                            Loc.otaErrorSlave(error.message ?: error.toString())
                        )
                        Timber.e(error, "Slave BLE OTA failed")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    errorMessage = Loc.fileReadError(e.message ?: e.toString())
                )
            }
        }
    }

    fun uploadMasterFirmwareBle(firmwareFile: File) {
        viewModelScope.launch {
            try {
                val firmwareBytes = firmwareFile.readBytes()
                _uiState.value = _uiState.value.copy(
                    isUploading = true,
                    uploadProgress = 0f,
                    uploadTarget = "Master ESP32 (BLE)",
                    errorMessage = null
                )
                startOtaNotification("Master (BLE)")
                repository.bleFileOtaUpdate(firmwareBytes, "master") { progress ->
                    _uiState.value = _uiState.value.copy(
                        uploadProgress = progress.percent / 100f,
                        uploadBytes = progress.bytesDone,
                        uploadTotal = progress.bytesTotal,
                        uploadBytesPerSec = progress.bytesPerSecond,
                        uploadEtaSeconds = progress.etaSeconds,
                        uploadPhaseApply = progress.isApplyPhase
                    )
                    updateOtaNotification(progress)
                }.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            uploadProgress = 1f,
                            uploadMessage = "Master firmware sent over BLE, device restarts",
                            isActive = false
                        )
                        finishOtaNotification(true, Loc.otaDoneMaster())
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            errorMessage = Loc.otaErrorBle(error.message ?: error.toString())
                        )
                        finishOtaNotification(
                            false,
                            Loc.otaErrorBle(error.message ?: error.toString())
                        )
                        Timber.e(error, "BLE OTA failed")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    errorMessage = Loc.fileReadError(e.message ?: e.toString())
                )
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
                    uploadTarget = tr("Slave ESP32 (через UART)"),
                    errorMessage = null
                )
                
                repository.uploadSlaveFirmware(firmwareBytes).fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            isUploading = false,
                            uploadProgress = 1f,
                            uploadMessage = tr("Прошивку Slave ESP32 завантажено"),
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
                            errorMessage = Loc.otaErrorSlave(error.message ?: error.toString())
                        )
                        Timber.e(error, "Failed to upload slave firmware")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploading = false,
                    errorMessage = Loc.fileReadError(e.message ?: e.toString())
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
                        restartMessage = tr("Пристрій перезавантажується…")
                    )
                    // The device reboots and drops BLE; do not leave the banner forever.
                    viewModelScope.launch {
                        delay(12000)
                        _uiState.value = _uiState.value.copy(restartMessage = null)
                    }
                    Timber.d("Device restart initiated")
                },
                onFailure = { error ->
                    _error.value = tr("Помилка перезавантаження: %s").format(error.message ?: error.toString())
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
    val uploadBytes: Long = 0,
    val uploadTotal: Long = 0,
    val uploadBytesPerSec: Long = 0,
    val uploadEtaSeconds: Long = -1,
    val uploadPhaseApply: Boolean = false,
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