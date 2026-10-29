package com.example.androidplayer.ota

import android.content.Context
import com.example.androidplayer.data.ble.BlePlayerClient
import com.example.androidplayer.data.github.GitHubReleaseClient
import com.example.androidplayer.data.model.ReleaseAsset
import com.example.androidplayer.data.repository.Esp32PlayerRepository
import com.example.androidplayer.ui.i18n.Loc
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Runs firmware download + BLE flashing outside the ViewModel lifecycle.
 *
 * A 1.2-1.6 MB image takes several minutes over BLE. If the transfer lived in
 * `viewModelScope`, leaving the screen (or the system reclaiming the activity)
 * would cancel it. Here the job runs in an application-scoped coroutine, the
 * process is kept alive by [OtaUploadService] and progress is mirrored both to
 * the notification shade and to any UI that collects [progress].
 */
@Singleton
class UpdateInstaller @Inject constructor(
    private val github: GitHubReleaseClient,
    private val repository: Esp32PlayerRepository,
    private val bleClient: BlePlayerClient,
    @ApplicationContext private val context: Context
) {
    data class Progress(
        val running: Boolean = false,
        val percent: Int = 0,
        val status: String? = null,
        val error: String? = null,
        /** Incremented every time a transfer finishes (success or failure). */
        val finishedCount: Int = 0,
        val lastSuccess: Boolean = false
    )

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _progress = MutableStateFlow(Progress())
    val progress: StateFlow<Progress> = _progress.asStateFlow()

    private var job: Job? = null
    private var lastNotifyPercent = -1
    private var lastNotifyAt = 0L

    fun isRunning(): Boolean = job?.isActive == true

    /** Downloads [asset] and flashes it to the master (or the slave over UART). */
    fun flash(asset: ReleaseAsset, target: String) = synchronized(this) {
        if (isRunning()) {
            Timber.w("Update already running, ignoring new request")
            return
        }
        val label = if (target == "slave") Loc.updSlave() else Loc.updFirmware()
        job = scope.launch {
            _progress.value = Progress(running = true, percent = 0, status = Loc.updateDownloading(asset.name))
            lastNotifyPercent = -1
            lastNotifyAt = 0L
            OtaUploadService.start(context)
            notifyProgress(label, 0, apply = false, forced = true)

            try {
                val file = File(context.cacheDir, "updates/${asset.name}")
                github.download(asset.url, file) { percent ->
                    val overall = if (target == "slave") percent / 3 else percent / 2
                    _progress.value = _progress.value.copy(
                        percent = overall,
                        status = Loc.updateDownloading(asset.name)
                    )
                    notifyProgress(label, overall, apply = false)
                }

                _progress.value = _progress.value.copy(status = Loc.updateFlashing(), percent = 33)
                val bytes = file.readBytes()
                val uploadShare = if (target == "slave") 67 else 50
                val uploadBase = if (target == "slave") 33 else 50

                repository.bleFileOtaUpdate(bytes, target) { transfer ->
                    val overall = uploadBase + (transfer.percent * uploadShare / 100)
                    _progress.value = _progress.value.copy(
                        percent = overall,
                        status = Loc.updateFlashingPercent(transfer.percent)
                    )
                    notifyProgress(
                        label,
                        overall,
                        apply = transfer.phase == "apply",
                        speed = transfer.bytesPerSecond,
                        eta = transfer.etaSeconds
                    )
                }.fold(
                    onSuccess = {
                        val text = if (target == "slave") Loc.updateFlashedSlave() else Loc.updateFlashed()
                        _progress.value = _progress.value.copy(
                            running = false,
                            percent = 100,
                            status = text,
                            error = null,
                            finishedCount = _progress.value.finishedCount + 1,
                            lastSuccess = true
                        )
                        finishNotification(true, text)
                    },
                    onFailure = { error ->
                        val text = Loc.updateErrorFlash(error.message ?: "unknown")
                        _progress.value = _progress.value.copy(
                            running = false,
                            status = null,
                            error = text,
                            finishedCount = _progress.value.finishedCount + 1,
                            lastSuccess = false
                        )
                        finishNotification(false, text)
                    }
                )

                // The master reboots after applying the image: wait for it and restore
                // the link, otherwise Android keeps a half-open GATT (app "connected",
                // but no data until it is restarted).
                if (_progress.value.lastSuccess && target == "master") {
                    val reconnected = bleClient.awaitRebootAndReconnect()
                    Timber.d("BLE after firmware reboot: reconnected=$reconnected")
                }
            } catch (e: Exception) {
                Timber.e(e, "Firmware update failed")
                val text = Loc.updateErrorDownload(e.message ?: "unknown")
                _progress.value = _progress.value.copy(
                    running = false,
                    status = null,
                    error = text,
                    finishedCount = _progress.value.finishedCount + 1,
                    lastSuccess = false
                )
                finishNotification(false, text)
            }
        }
    }

    /** Clears the last error/status (UI dismiss). */
    fun clearMessages() {
        _progress.value = _progress.value.copy(error = null, status = null)
    }

    private fun notifyProgress(
        label: String,
        percent: Int,
        apply: Boolean,
        speed: Long = 0,
        eta: Long = -1,
        forced: Boolean = false
    ) {
        val now = android.os.SystemClock.elapsedRealtime()
        if (!forced) {
            if (percent == lastNotifyPercent && now - lastNotifyAt < 1000) return
            if (percent < lastNotifyPercent) return
        }
        lastNotifyPercent = percent
        lastNotifyAt = now
        val shownLabel = if (apply) "$label · ${Loc.otaPhaseName(true)}" else label
        OtaNotifications.updateProgress(
            context,
            percent,
            Loc.otaProgressText(shownLabel, percent, speed, eta)
        )
    }

    private fun finishNotification(success: Boolean, text: String) {
        OtaUploadService.stop(context)
        OtaNotifications.showResult(context, success, text)
    }
}
