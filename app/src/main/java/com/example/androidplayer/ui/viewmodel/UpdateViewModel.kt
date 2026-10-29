package com.example.androidplayer.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidplayer.BuildConfig
import com.example.androidplayer.data.github.GitHubError
import com.example.androidplayer.data.github.GitHubReleaseClient
import com.example.androidplayer.data.model.ReleaseInfo
import com.example.androidplayer.data.model.VersionCompare
import com.example.androidplayer.data.repository.Esp32PlayerRepository
import com.example.androidplayer.ota.OtaNotifications
import com.example.androidplayer.ota.OtaUploadService
import com.example.androidplayer.ota.UpdateInstaller
import com.example.androidplayer.ui.i18n.Loc
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    private val github: GitHubReleaseClient,
    private val repository: Esp32PlayerRepository,
    private val bleClient: com.example.androidplayer.data.ble.BlePlayerClient,
    private val installer: UpdateInstaller,
    @ApplicationContext private val context: Context
) : ViewModel() {

    data class UpdateUiState(
        val checking: Boolean = false,
        val appCurrent: String = BuildConfig.VERSION_NAME,
        val appLatest: String? = null,
        val fwCurrent: String? = null,
        val fwLatest: String? = null,
        val appNotes: String? = null,
        val fwNotes: String? = null,
        val appUpdate: Boolean = false,
        val fwUpdate: Boolean = false,
        val slaveCurrent: String? = null,
        val slaveLatest: String? = null,
        val slaveUpdate: Boolean = false,
        val busy: Boolean = false,
        val progress: Int = 0,
        val status: String? = null,
        val error: String? = null,
        val connected: Boolean = false
    )

    private val _uiState = MutableStateFlow(UpdateUiState())
    val uiState: StateFlow<UpdateUiState> = _uiState.asStateFlow()

    private var appRelease: ReleaseInfo? = null
    private var fwRelease: ReleaseInfo? = null
    private var slaveRelease: ReleaseInfo? = null
    private var lastFinishedCount = 0

    init {
        viewModelScope.launch {
            bleClient.isConnected.collect { connected ->
                _uiState.value = _uiState.value.copy(connected = connected)
            }
        }
        // Flashing runs in the singleton installer (survives leaving the screen);
        // here we only mirror its state into the UI.
        viewModelScope.launch {
            installer.progress.collect { p ->
                _uiState.value = _uiState.value.copy(
                    busy = p.running,
                    progress = p.percent,
                    status = p.status,
                    error = p.error
                )
                if (p.finishedCount != lastFinishedCount) {
                    lastFinishedCount = p.finishedCount
                    if (p.lastSuccess) checkForUpdates()
                }
            }
        }
        checkForUpdates()
    }

    fun checkForUpdates(force: Boolean = false) {
        if (_uiState.value.checking || _uiState.value.busy) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(checking = true, error = null, status = null)

            val maxAge = if (force) 0L else GitHubReleaseClient.CACHE_TTL_MS
            val app = github.latestRelease(APP_REPO, maxAge)
            val fw = github.latestRelease(FW_REPO, maxAge)
            val slave = github.latestRelease(SLAVE_REPO, maxAge)
            appRelease = app
            fwRelease = fw
            slaveRelease = slave

            // версія прошивки береться з самого пристрою
            val deviceFw = runCatching {
                if (bleClient.isConnected.value) repository.getSystemInfo().getOrThrow().firmwareVersion
                else null
            }.getOrNull()

            val slaveFw = runCatching {
                if (bleClient.isConnected.value) {
                    repository.getVersionInfo().getOrThrow().slave_fw_version
                        .takeIf { it.isNotBlank() && it != "unknown" }
                } else null
            }.getOrNull()

            val appCurrent = BuildConfig.VERSION_NAME
            val appLatest = app?.tag
            val fwCurrent = deviceFw
            val fwLatest = fw?.tag

            _uiState.value = _uiState.value.copy(
                checking = false,
                appCurrent = appCurrent,
                appLatest = appLatest,
                fwCurrent = fwCurrent,
                fwLatest = fwLatest,
                appNotes = app?.notes,
                fwNotes = fw?.notes,
                appUpdate = VersionCompare.isNewer(appLatest, appCurrent),
                fwUpdate = VersionCompare.isNewer(fwLatest, fwCurrent),
                slaveCurrent = slaveFw,
                slaveLatest = slave?.tag,
                slaveUpdate = VersionCompare.isNewer(slave?.tag, slaveFw)
            )

            if (app == null && fw == null && slave == null) {
                _uiState.value = _uiState.value.copy(
                    error = when (val failure = github.lastError) {
                        is GitHubError.RateLimited -> {
                            val time = if (failure.resetAt > 0) {
                                java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                                    .format(java.util.Date(failure.resetAt))
                            } else "~1 год"
                            Loc.updateErrorRateLimit(time)
                        }
                        is GitHubError.Network -> Loc.updateErrorNetwork()
                        else -> Loc.updateErrorCheck()
                    }
                )
            } else if (github.lastError != null) {
                // Releases came from the local cache: still usable, but say why.
                _uiState.value = _uiState.value.copy(status = Loc.updateFromCache())
            }
        }
    }

    /** Завантажити APK і відкрити системний інсталятор. */
    fun downloadAndInstallApp() {
        val release = appRelease ?: return
        val asset = release.asset(listOf(".apk")) ?: run {
            _uiState.value = _uiState.value.copy(error = Loc.updateErrorNoApk())
            return
        }
        if (_uiState.value.busy) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                busy = true, progress = 0, error = null,
                status = Loc.updateDownloading(asset.name)
            )
            OtaUploadService.start(context)
            OtaNotifications.updateProgress(context, 0, Loc.otaProgressText(asset.name, 0, 0, -1))
            try {
                val file = File(context.cacheDir, "updates/${asset.name}")
                var lastPercent = -1
                github.download(asset.url, file) { percent ->
                    _uiState.value = _uiState.value.copy(progress = percent)
                    if (percent != lastPercent) {
                        lastPercent = percent
                        OtaNotifications.updateProgress(
                            context,
                            percent,
                            Loc.otaProgressText(asset.name, percent, 0, -1)
                        )
                    }
                }
                OtaUploadService.stop(context)
                _uiState.value = _uiState.value.copy(
                    busy = false,
                    progress = 100,
                    status = Loc.updateInstalling()
                )
                installApk(file)
            } catch (e: Exception) {
                Timber.e(e, "APK download failed")
                OtaUploadService.stop(context)
                _uiState.value = _uiState.value.copy(
                    busy = false,
                    error = Loc.updateErrorDownload(e.message ?: "unknown")
                )
            }
        }
    }

    private fun installApk(file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    /** Завантажити образ прошивки і прошити Master по BLE (працює у фоні). */
    fun downloadAndFlashFirmware() {
        val release = fwRelease ?: return
        val asset = release.asset(listOf(".bin"), exclude = listOf("-full", "bootloader", "partition", "ota_data"))
            ?: run {
                _uiState.value = _uiState.value.copy(error = Loc.updateErrorNoBin())
                return
            }
        if (_uiState.value.busy) return
        installer.flash(asset, "master")
    }

    /** Завантажити образ Slave і прошити його через Master (BLE -> UART, працює у фоні). */
    fun downloadAndFlashSlave() {
        val release = slaveRelease ?: return
        val asset = release.asset(listOf(".bin"), exclude = listOf("-full", "bootloader", "partition", "ota_data"))
            ?: run {
                _uiState.value = _uiState.value.copy(error = Loc.updateErrorNoBin())
                return
            }
        if (_uiState.value.busy) return
        installer.flash(asset, "slave")
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null, status = null)
        installer.clearMessages()
    }

    companion object {
        const val APP_REPO = "freebsdmax/Esp32_Android_app"
        const val FW_REPO = "freebsdmax/Ford-DensO-mp3-master"
        const val SLAVE_REPO = "freebsdmax/Ford-DensO-bluetooth-slave"
    }
}
