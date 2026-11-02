package com.example.androidplayer.ui.viewmodel

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidplayer.data.github.GitHubReleaseClient
import com.example.androidplayer.data.usb.EspFlasher
import com.example.androidplayer.ui.i18n.Loc
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * Прошивка плати по USB-кабелю з телефона (аварійне відновлення,
 * коли BLE/OTA недоступні).
 */
@HiltViewModel
class UsbFlashViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val github: GitHubReleaseClient
) : ViewModel() {

    data class DeviceInfo(val name: String, val vendorId: Int, val productId: Int, val deviceId: Int)

    data class UiState(
        /** "master" або "slave" — яку прошивку заливаємо */
        val target: String = "master",
        val devices: List<DeviceInfo> = emptyList(),
        val selected: DeviceInfo? = null,
        /** Чи надано Android-дозвіл на доступ до вибраного USB-пристрою */
        val permission: Boolean = false,
        val imageName: String? = null,
        val imageSize: Long = 0,
        val imageTag: String? = null,
        val busy: Boolean = false,
        val progress: Int = 0,
        val status: String? = null,
        val error: String? = null,
        val log: List<String> = emptyList()
    )

    private val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val permissionAction = "${context.packageName}.USB_PERMISSION"
    private var pendingDevice: UsbDevice? = null
    private var permission: CompletableDeferred<Boolean>? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            if (intent?.action != permissionAction) return
            val extra = intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)
            // Android 12+ may drop the extra from an immutable PendingIntent,
            // so ask the system directly instead of trusting the broadcast.
            val granted = extra || pendingDevice?.let { usbManager.hasPermission(it) } == true
            permission?.complete(granted)
            permission = null
            _uiState.value = _uiState.value.copy(permission = granted)
        }
    }

    init {
        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(receiver, IntentFilter(permissionAction), Context.RECEIVER_NOT_EXPORTED)
            } else {
                ContextCompat.registerReceiver(
                    context, receiver, IntentFilter(permissionAction),
                    ContextCompat.RECEIVER_NOT_EXPORTED
                )
            }
        }
        refreshDevices()
    }

    override fun onCleared() {
        runCatching { context.unregisterReceiver(receiver) }
        super.onCleared()
    }

    fun refreshDevices() {
        val list = usbManager.deviceList.values.mapNotNull { dev ->
            val driver = UsbSerialProber.getDefaultProber().probeDevice(dev) ?: return@mapNotNull null
            DeviceInfo(
                name = "${dev.productName ?: "USB"} (${dev.vendorId.toString(16)}:${dev.productId.toString(16)})",
                vendorId = dev.vendorId,
                productId = dev.productId,
                deviceId = dev.deviceId
            )
        }
        val selected = _uiState.value.selected ?: list.firstOrNull()
        _uiState.value = _uiState.value.copy(
            devices = list,
            selected = selected,
            permission = selected?.let { info ->
                findDevice(info)?.let { usbManager.hasPermission(it) }
            } ?: false
        )
    }

    /** Вибір плати: Master або Slave (у кожної свій репозиторій з образами). */
    fun setTarget(target: String) {
        if (_uiState.value.busy || target == _uiState.value.target) return
        _uiState.value = _uiState.value.copy(
            target = target,
            imageName = null,
            imageSize = 0,
            imageTag = null,
            progress = 0,
            status = null,
            error = null,
            log = emptyList()
        )
    }

    fun select(item: DeviceInfo) {
        val dev = findDevice(item)
        _uiState.value = _uiState.value.copy(
            selected = item,
            error = null,
            permission = dev != null && usbManager.hasPermission(dev)
        )
    }

    private fun findDevice(info: DeviceInfo): UsbDevice? =
        usbManager.deviceList.values.firstOrNull { it.deviceId == info.deviceId }

    /** Asks the system for one USB permission dialog; null = no answer in time. */
    private suspend fun requestOnce(device: UsbDevice, timeoutMs: Long): Boolean? {
        val deferred = CompletableDeferred<Boolean>()
        permission = deferred
        pendingDevice = device
        // FLAG_MUTABLE: the system fills EXTRA_PERMISSION_GRANTED into the
        // intent. With FLAG_IMMUTABLE the extra can be lost on Android 12+,
        // which looked like "the user denied" while the dialog was accepted.
        val flags = PendingIntent.FLAG_UPDATE_CURRENT or
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_MUTABLE else 0
        val intent = Intent(permissionAction).setPackage(context.packageName)
        return try {
            usbManager.requestPermission(device, PendingIntent.getBroadcast(context, 0, intent, flags))
            withTimeoutOrNull(timeoutMs) { deferred.await() }
        } catch (e: Exception) {
            Timber.e(e, "requestPermission failed")
            null
        } finally {
            permission = null
        }
    }

    private suspend fun ensurePermission(device: UsbDevice): Boolean {
        if (usbManager.hasPermission(device)) {
            _uiState.value = _uiState.value.copy(permission = true)
            return true
        }

        val first = requestOnce(device, 45_000)
        if (first == true || usbManager.hasPermission(device)) {
            _uiState.value = _uiState.value.copy(permission = true)
            return true
        }
        if (first == false) {
            _uiState.value = _uiState.value.copy(permission = false)
            return false
        }

        // No broadcast arrived (some OEM ROMs drop it) — one more attempt.
        requestOnce(device, 20_000)
        val granted = usbManager.hasPermission(device)
        _uiState.value = _uiState.value.copy(permission = granted)
        return granted
    }

    /** Explicit "Allow USB access" button: shows the system dialog on demand. */
    fun requestPermission() {
        val info = _uiState.value.selected
        if (info == null) {
            _uiState.value = _uiState.value.copy(error = Loc.usbNoDevice())
            return
        }
        viewModelScope.launch {
            val device = findDevice(info) ?: run {
                _uiState.value = _uiState.value.copy(error = Loc.usbNoDevice())
                return@launch
            }
            val granted = ensurePermission(device)
            _uiState.value = _uiState.value.copy(
                permission = granted,
                error = if (granted) null else Loc.usbPermissionDenied(),
                status = if (granted) Loc.usbPermissionGranted() else _uiState.value.status
            )
        }
    }

    /** Завантажує злитий образ (`-full.bin`) останнього релізу Master. */
    fun downloadImage() {
        if (_uiState.value.busy) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(busy = true, progress = 0, error = null,
                status = Loc.usbDownloading())
            try {
                val repo = if (_uiState.value.target == "slave") SLAVE_REPO else FW_REPO
                val release = github.latestRelease(repo) ?: throw IllegalStateException("GitHub unavailable")
                val asset = release.asset(listOf(".bin"), exclude = listOf("bootloader", "partition", "ota_data"))
                    ?: release.asset(listOf("-full.bin"))
                    ?: throw IllegalStateException("Release has no -full.bin")
                val file = File(context.cacheDir, "usb/${asset.name}")
                github.download(asset.url, file) { p ->
                    _uiState.value = _uiState.value.copy(progress = p, status = Loc.usbDownloadingPercent(p))
                }
                _uiState.value = _uiState.value.copy(
                    busy = false,
                    progress = 0,
                    imageName = asset.name,
                    imageSize = file.length(),
                    imageTag = release.tag,
                    status = Loc.usbImageReady(asset.name, file.length() / 1024)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(busy = false, error = e.message ?: "download failed")
            }
        }
    }

    /** Прошиває вибраний пристрій завантаженим образом. */
    fun flash() {
        val st = _uiState.value
        if (st.busy) return
        val info = st.selected
        if (info == null) {
            _uiState.value = st.copy(error = Loc.usbNoDevice())
            return
        }
        val name = st.imageName ?: run {
            downloadImage()
            return
        }
        val file = File(context.cacheDir, "usb/$name")
        if (!file.exists()) {
            _uiState.value = st.copy(error = Loc.usbNoImage())
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(busy = true, progress = 0, log = emptyList(),
                error = null, status = Loc.usbFlashing())

            val device = findDevice(info) ?: run {
                _uiState.value = _uiState.value.copy(busy = false, error = Loc.usbNoDevice())
                return@launch
            }
            if (!ensurePermission(device)) {
                _uiState.value = _uiState.value.copy(busy = false, error = Loc.usbPermissionDenied())
                return@launch
            }

            val driver = UsbSerialProber.getDefaultProber().probeDevice(device) ?: run {
                _uiState.value = _uiState.value.copy(busy = false, error = Loc.usbUnsupported())
                return@launch
            }
            val port: UsbSerialPort = driver.ports.firstOrNull() ?: run {
                _uiState.value = _uiState.value.copy(busy = false, error = Loc.usbUnsupported())
                return@launch
            }

            val flasher = EspFlasher(usbManager, port)
            val listener = object : EspFlasher.Listener {
                override fun onProgress(percent: Int, detail: String) {
                    _uiState.value = _uiState.value.copy(progress = percent, status = detail)
                }

                override fun onLog(line: String) {
                    _uiState.value = _uiState.value.copy(log = (_uiState.value.log + line).takeLast(200))
                }
            }

            try {
                withContext(Dispatchers.IO) { flasher.open() }
                val bytes = withContext(Dispatchers.IO) { file.readBytes() }
                when (val res = flasher.flash(bytes, 0, listener)) {
                    is EspFlasher.Result.Ok -> {
                        _uiState.value = _uiState.value.copy(
                            busy = false, progress = 100, status = Loc.usbDone()
                        )
                    }
                    is EspFlasher.Result.Error -> {
                        _uiState.value = _uiState.value.copy(busy = false, error = res.message)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "USB flash failed")
                _uiState.value = _uiState.value.copy(busy = false, error = e.message ?: "USB error")
            } finally {
                withContext(Dispatchers.IO) { flasher.close() }
            }
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    companion object {
        const val FW_REPO = "freebsdmax/Ford-DensO-mp3-master"
        const val SLAVE_REPO = "freebsdmax/Ford-DensO-bluetooth-slave"
    }
}
