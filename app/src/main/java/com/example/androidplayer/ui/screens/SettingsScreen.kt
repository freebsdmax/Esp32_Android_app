package com.example.androidplayer.ui.screens

import java.io.File
import com.example.androidplayer.ui.viewmodel.OtaViewModel
import com.example.androidplayer.ui.viewmodel.OtaUiState
import com.example.androidplayer.ui.components.CardHeader
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material.icons.filled.Wifi
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import android.net.Uri
import com.example.androidplayer.ui.i18n.Loc
import com.example.androidplayer.ui.i18n.tr

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidplayer.data.ble.BlePlayerClient
import com.example.androidplayer.ui.viewmodel.SettingsViewModel
import androidx.compose.ui.graphics.Color
import com.example.androidplayer.ui.notify.AppNotify

@Composable
fun SettingsScreen(
    ble: BlePlayerClient,
    onOpenBle: () -> Unit,
    onOpenUsbFlash: () -> Unit = {},
    onOpenCanKeys: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val canLearn by viewModel.canLearnStatus.collectAsStateWithLifecycle()
    val canKeys by viewModel.canKeys.collectAsStateWithLifecycle()
    val connected by ble.isConnected.collectAsStateWithLifecycle()
    val otaViewModel: OtaViewModel = hiltViewModel()
    val otaState by otaViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.saveMessage) {
        uiState.saveMessage?.let { AppNotify.info(it) }
    }
    LaunchedEffect(otaState.uploadMessage) {
        otaState.uploadMessage?.let { AppNotify.info(it); otaViewModel.clearUploadMessage() }
    }
    LaunchedEffect(otaState.errorMessage) {
        otaState.errorMessage?.let { AppNotify.error(it) }
    }

    // Ручне прошивання: вибір .bin з телефона
    val masterPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { input ->
                val temp = File(context.cacheDir, "firmware_ble_${System.currentTimeMillis()}.bin")
                temp.outputStream().use { out -> input.copyTo(out) }
                otaViewModel.uploadMasterFirmwareBle(temp)
            }
        }
    }
    val slavePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { input ->
                val temp = File(context.cacheDir, "slave_ble_${System.currentTimeMillis()}.bin")
                temp.outputStream().use { out -> input.copyTo(out) }
                otaViewModel.uploadSlaveFirmwareBle(temp)
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BleConnectionCard(
                connected = connected,
                deviceLabel = ble.currentDeviceLabel(),
                onOpenBle = onOpenBle,
                onConnect = { ble.connectRememberedDevice() },
                onDisconnect = { ble.disconnect() },
                onReconnect = { ble.reconnect() }
            )
        }
        item {
            AppearanceCard(
                themeMode = uiState.themeMode,
                onThemeMode = { viewModel.setThemeMode(it) },
                appLabel = uiState.appLabel,
                onSaveAppLabel = { viewModel.setAppLabel(it) },
                onPinShortcut = { viewModel.pinAppShortcut(it) }
            )
        }
        item {
            NetCard(
                useStatic = uiState.netStatic,
                ip = uiState.netIp,
                gw = uiState.netGw,
                mask = uiState.netMask,
                dns1 = uiState.netDns1,
                dns2 = uiState.netDns2,
                onStatic = { viewModel.setNetStatic(it) },
                onField = { k, v -> viewModel.setNetField(k, v) },
                onSave = { viewModel.saveNetConfig() }
            )
        }
        item {
            AutoplayCard(
                bleEnabled = uiState.autoplay,
                slaveEnabled = uiState.autoplaySlave,
                onBleChange = { viewModel.setAutoplay("ble", it) },
                onSlaveChange = { viewModel.setAutoplay("slave", it) }
            )
        }
        item {
            NetworkCard(
                wifi = uiState.netWifi,
                web = uiState.netWeb,
                onWifi = { viewModel.setNetEnabled("wifi", it) },
                onWeb = { viewModel.setNetEnabled("web", it) }
            )
        }
        item {
            AudioProfileCard(
                profile = uiState.audioProfile,
                onSelect = { viewModel.saveAudioProfile(it) }
            )
        }
        item {
            UpdatesAutoCard(
                auto = uiState.updateAuto,
                intervalHours = uiState.updateIntervalHours,
                beta = uiState.updateBeta,
                onAuto = { viewModel.setUpdateAuto(it) },
                onInterval = { viewModel.setUpdateInterval(it) },
                onChannel = { viewModel.setUpdateChannel(it) }
            )
        }
        item {
            LogsCard(
                bufferKb = uiState.logBufferKb,
                persist = uiState.logPersist,
                onBuffer = { viewModel.setLogBufferKb(it) },
                onPersist = { viewModel.setLogPersist(it) },
                onSave = { viewModel.saveLogsConfig() }
            )
        }
        item {
            BleNameCard(
                name = uiState.bleName,
                onChange = { viewModel.setBleNameText(it) },
                onSave = { viewModel.saveBleName() }
            )
        }
        item {
            AuthCard(
                token = uiState.webToken,
                enabled = uiState.webTokenEnabled,
                onToken = { viewModel.setWebToken(it) },
                onSave = { viewModel.saveWebToken() }
            )
        }
        item {
            CanCard(
                bitrate = uiState.canBitrate,
                id = uiState.canId,
                maskNext = uiState.canMaskNext,
                maskPrev = uiState.canMaskPrev,
                maskMode = uiState.canMaskMode,
                debounceMs = uiState.canDebounceMs,
                onBitrate = { viewModel.setCanField("bitrate", it.toLong()) },
                onField = { key, value -> viewModel.setCanField(key, value) },
                onSave = { viewModel.saveCanConfig() },
                learnStatus = canLearn,
                onLearn = { action, secs -> viewModel.startCanLearn(action, secs) },
                onLearnCancel = { viewModel.cancelCanLearn() },
                keyCount = canKeys.size,
                onOpenKeys = onOpenCanKeys
            )
        }
        item {
            SeekCard(
                stepSeconds = uiState.seekStepSeconds,
                hold = uiState.seekHold,
                onChange = { step, holdMode -> viewModel.setSeekConfig(step, holdMode) }
            )
        }
        item {
            MiniPlayerCard(
                always = uiState.miniPlayerAlways,
                onChange = { viewModel.updateSetting(SettingsViewModel.MINI_PLAYER_ALWAYS, it) }
            )
        }
        item {
            SleepCard(
                sleepMode = uiState.sleepMode,
                timeoutSec = uiState.sleepTimeoutSec,
                onModeChange = { viewModel.setSleepMode(it) },
                onTimeoutChange = { viewModel.setSleepTimeoutSeconds(it) },
                onSave = { viewModel.saveSleepSettings() }
            )
        }
        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SectionHeader(
                        icon = Icons.Default.Usb,
                        title = Loc.usbTitle(),
                        subtitle = Loc.usbSubtitle()
                    )
                    Button(onClick = onOpenUsbFlash, modifier = Modifier.fillMaxWidth()) {
                        Text(tr("Відкрити"))
                    }
                }
            }
        }
        item {
            ManualFirmwareCard(
                state = otaState,
                onPickMaster = { masterPicker.launch("application/octet-stream") },
                onPickSlave = { slavePicker.launch("application/octet-stream") }
            )
        }
    }
}

@Composable
private fun BleConnectionCard(
    connected: Boolean,
    deviceLabel: String,
    onOpenBle: () -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onReconnect: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(
                icon = if (connected) Icons.Default.BluetoothConnected else Icons.Default.Bluetooth,
                title = tr("Підключення"),
                subtitle = if (connected) tr("Підключено по BLE") else tr("Немає з'єднання")
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = if (connected) MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = deviceLabel,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (connected) MaterialTheme.colorScheme.secondary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (connected) {
                    OutlinedButton(
                        onClick = onDisconnect,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.LinkOff, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(tr("Від'єднати"), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                    OutlinedButton(
                        onClick = onReconnect,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(tr("Оновити"), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                } else {
                    Button(
                        onClick = onConnect,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Bluetooth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(tr("Підключити"), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            Button(
                onClick = onOpenBle,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(tr("Сканувати"))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AppearanceCard(
    themeMode: String,
    onThemeMode: (String) -> Unit,
    appLabel: String,
    onSaveAppLabel: (String) -> Unit,
    onPinShortcut: (String) -> Unit
) {
    var labelText by remember(appLabel) { mutableStateOf(appLabel) }
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionHeader(
                icon = Icons.Default.DarkMode,
                title = tr("Зовнішній вигляд"),
                subtitle = tr("Оформлення інтерфейсу")
            )
            Text(Loc.themeHint(), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = themeMode == "system",
                    onClick = { onThemeMode("system") },
                    modifier = Modifier.weight(1f),
                    label = {
                        Text(Loc.themeSystem(), maxLines = 1,
                            style = MaterialTheme.typography.labelMedium)
                    }
                )
                FilterChip(
                    selected = themeMode == "light",
                    onClick = { onThemeMode("light") },
                    modifier = Modifier.weight(1f),
                    label = {
                        Text(Loc.themeLight(), maxLines = 1,
                            style = MaterialTheme.typography.labelMedium)
                    }
                )
                FilterChip(
                    selected = themeMode == "dark",
                    onClick = { onThemeMode("dark") },
                    modifier = Modifier.weight(1f),
                    label = {
                        Text(Loc.themeDark(), maxLines = 1,
                            style = MaterialTheme.typography.labelMedium)
                    }
                )
            }

            Text(Loc.themeAppliedNow(), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            Spacer(Modifier.height(6.dp))

            Text(Loc.appNameTitle(), style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium)
            Text(Loc.appNameHint(), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            OutlinedTextField(
                value = labelText,
                onValueChange = { labelText = it },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = { onSaveAppLabel(labelText) }, modifier = Modifier.fillMaxWidth()) {
                Text(Loc.save())
            }
            OutlinedButton(
                onClick = { onPinShortcut(labelText) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(Loc.pinShortcut())
            }
            Text(Loc.pinShortcutHint(), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun MiniPlayerCard(
    always: Boolean,
    onChange: (Boolean) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionHeader(
                icon = Icons.Default.PlayArrow,
                title = Loc.miniPlayerTitle(),
                subtitle = Loc.miniPlayerSubtitle()
            )
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (always) tr("Завжди доступний") else tr("Тільки для флешки"),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        Loc.miniPlayerHint(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = always, onCheckedChange = onChange)
            }
        }
    }
}

@Composable
private fun SleepCard(
    sleepMode: Int,
    timeoutSec: Int,
    onModeChange: (Int) -> Unit,
    onTimeoutChange: (Int) -> Unit,
    onSave: () -> Unit
) {
    var timeoutText by remember(timeoutSec) { mutableStateOf(timeoutSec.toString()) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            SectionHeader(
                icon = Icons.Default.Bedtime,
                title = tr("Сон та енергозбереження"),
                subtitle = tr("Пристрій засинає після простою")
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                listOf(
                    0 to tr("Вимкнено"),
                    1 to "Light sleep",
                    3 to "Deep sleep"
                ).forEach { (mode, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onModeChange(mode) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = sleepMode == mode,
                            onClick = { onModeChange(mode) }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
            OutlinedTextField(
                value = timeoutText,
                onValueChange = { text ->
                    val digits = text.filter { it.isDigit() }.take(5)
                    timeoutText = digits
                    digits.toIntOrNull()?.let(onTimeoutChange)
                },
                label = { Text(tr("Таймаут простою (с)")) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Text(tr("Зберегти"))
            }
        }
    }
}

@Composable
private fun ManualFirmwareCard(
    state: OtaUiState,
    onPickMaster: () -> Unit,
    onPickSlave: () -> Unit
) {
    val uploading = state.isUploading
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            CardHeader(
                icon = Icons.Default.Upload,
                title = Loc.fwManualTitle(),
                subtitle = Loc.fwManualSubtitle()
            )
            Text(
                Loc.fwManualHint(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onPickMaster,
                enabled = !uploading,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(Loc.fwManualMaster())
            }
            OutlinedButton(
                onClick = onPickSlave,
                enabled = !uploading,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(Loc.fwManualSlave())
            }
            if (uploading) {
                LinearProgressIndicator(
                    progress = state.uploadProgress,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${(state.uploadProgress * 100).toInt()}%  " +
                            Loc.otaPhaseName(state.uploadPhaseApply),
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (state.uploadBytesPerSec > 0) {
                        Text(
                            text = Loc.speedText(state.uploadBytesPerSec),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
                if (state.uploadEtaSeconds >= 0) {
                    Text(
                        text = Loc.etaText(state.uploadEtaSeconds),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.padding(8.dp).size(20.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SavedBanner(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.width(10.dp))
            Text(message, color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun SeekCard(
    stepSeconds: Int,
    hold: Boolean,
    onChange: (Int, Boolean) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(
                icon = Icons.Default.FastForward,
                title = tr("Перемотка (Bluetooth)"),
                subtitle = tr("Крок і метод діють на платі")
            )

            Text(
                tr("Крок одного натискання"),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(5, 10, 15, 30).forEach { step ->
                    FilterChip(
                        selected = stepSeconds == step,
                        onClick = { onChange(step, hold) },
                        label = { Text("$step с") }
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        tr("Довге утримання"),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        if (hold) tr("Одне утримання замість кількох натискань")
                        else tr("Кілька коротких натискань"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = hold, onCheckedChange = { onChange(stepSeconds, it) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun CanCard(
    bitrate: Int,
    id: Long,
    maskNext: Int,
    maskPrev: Int,
    maskMode: Int,
    debounceMs: Int,
    onBitrate: (Int) -> Unit,
    onField: (String, Long) -> Unit,
    onSave: () -> Unit,
    learnStatus: String?,
    onLearn: (Int, Int) -> Unit,
    onLearnCancel: () -> Unit,
    keyCount: Int,
    onOpenKeys: () -> Unit
) {
    var idText by remember(id) { mutableStateOf("0x" + id.toString(16).uppercase()) }
    var mnText by remember(maskNext) { mutableStateOf("0x" + maskNext.toString(16).uppercase()) }
    var mpText by remember(maskPrev) { mutableStateOf("0x" + maskPrev.toString(16).uppercase()) }
    var mmText by remember(maskMode) { mutableStateOf("0x" + maskMode.toString(16).uppercase()) }
    var debText by remember(debounceMs) { mutableStateOf(debounceMs.toString()) }

    fun parseHex(text: String): Long? =
        text.trim().removePrefix("0x").removePrefix("0X").toLongOrNull(16)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(
                icon = Icons.Default.DirectionsCar,
                title = tr("CAN-шина"),
                subtitle = tr("Швидкість, ID кнопок і маски")
            )

            Text(tr("Швидкість"), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(125, 250, 500).forEach { rate ->
                    FilterChip(
                        selected = bitrate == rate,
                        onClick = { onBitrate(rate) },
                        label = { Text("$rate") }
                    )
                }
            }

            OutlinedTextField(
                value = idText,
                onValueChange = { idText = it; parseHex(it)?.let { v -> onField("id", v) } },
                label = { Text(tr("ID кнопок (hex)")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = mnText,
                    onValueChange = { mnText = it; parseHex(it)?.let { v -> onField("mask_next", v) } },
                    label = { Text(tr("NEXT")) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = mpText,
                    onValueChange = { mpText = it; parseHex(it)?.let { v -> onField("mask_prev", v) } },
                    label = { Text(tr("PREV")) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = mmText,
                    onValueChange = { mmText = it; parseHex(it)?.let { v -> onField("mask_mode", v) } },
                    label = { Text(tr("MODE")) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = debText,
                onValueChange = { debText = it; it.toLongOrNull()?.let { v -> onField("debounce", v) } },
                label = { Text(tr("Антибрязкіт, мс")) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Text(tr("Зберегти"))
            }

            Spacer(Modifier.height(6.dp))

            Text(Loc.canLearnTitle(), style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium)
            Text(Loc.canLearnHint(), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                var learnSecs by remember { mutableStateOf("20") }
                OutlinedTextField(
                    value = learnSecs,
                    onValueChange = { learnSecs = it.filter { ch -> ch.isDigit() }.take(3) },
                    label = { Text(Loc.canLearnSeconds()) },
                    singleLine = true,
                    modifier = Modifier.width(110.dp)
                )
                OutlinedButton(onClick = { onLearn(0, learnSecs.toIntOrNull() ?: 20) }) {
                    Text(Loc.canLearnNext())
                }
                OutlinedButton(onClick = { onLearn(1, learnSecs.toIntOrNull() ?: 20) }) {
                    Text(Loc.canLearnPrev())
                }
                OutlinedButton(onClick = { onLearn(2, learnSecs.toIntOrNull() ?: 20) }) {
                    Text(Loc.canLearnMode())
                }
                OutlinedButton(onClick = onLearnCancel) { Text(Loc.canLearnCancel()) }
            }

            learnStatus?.let {
                Text(it, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(6.dp))
            OutlinedButton(onClick = onOpenKeys, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.VpnKey, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("${Loc.canKeysTitle()} ($keyCount)")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AuthCard(
    token: String,
    enabled: Boolean,
    onToken: (String) -> Unit,
    onSave: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(
                icon = Icons.Default.Lock,
                title = tr("Доступ до веб-інтерфейсу"),
                subtitle = tr("Токен для HTTP/OTA")
            )
            Text(
                if (enabled) tr("Захищено: веб і OTA вимагають токен")
                else tr("Відкрито: будь-хто в мережі може прошити плату"),
                style = MaterialTheme.typography.bodySmall,
                color = if (enabled) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.error
            )
            OutlinedTextField(
                value = token,
                onValueChange = onToken,
                label = { Text(tr("Токен (порожній = вимкнути)")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Text(tr("Зберегти"))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun BleNameCard(
    name: String,
    onChange: (String) -> Unit,
    onSave: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(
                icon = Icons.Default.BluetoothConnected,
                title = tr("Ім'я пристрою (BLE)"),
                subtitle = tr("Те, що бачить телефон при скануванні")
            )
            OutlinedTextField(
                value = name,
                onValueChange = onChange,
                label = { Text(tr("Назва (до 31 символа)")) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                tr("Застосується після перезавантаження плати"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Text(tr("Зберегти"))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun LogsCard(
    bufferKb: Int,
    persist: Boolean,
    onBuffer: (Int) -> Unit,
    onPersist: (Boolean) -> Unit,
    onSave: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(
                icon = Icons.Default.Description,
                title = tr("Логи"),
                subtitle = tr("Буфер і збереження у /storage")
            )

            Text(tr("Розмір буфера"), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(2, 4, 8, 16).forEach { kb ->
                    FilterChip(
                        selected = bufferKb == kb,
                        onClick = { onBuffer(kb) },
                        label = { Text("$kb КБ") }
                    )
                }
            }
            Text(
                tr("Застосується після перезавантаження плати"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        tr("Чорна скринька"),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        tr("Дописувати логи у /storage/log.txt (ротація 256 КБ)"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = persist, onCheckedChange = onPersist)
            }

            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Text(tr("Зберегти"))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun UpdatesAutoCard(
    auto: Boolean,
    intervalHours: Int,
    beta: Boolean,
    onAuto: (Boolean) -> Unit,
    onInterval: (Int) -> Unit,
    onChannel: (Boolean) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(
                icon = Icons.Default.SystemUpdate,
                title = Loc.updAutoTitle(),
                subtitle = Loc.updAutoSubtitle()
            )

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    Loc.updAutoSwitch(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Switch(checked = auto, onCheckedChange = onAuto)
            }

            if (auto) {
                Text(
                    Loc.updInterval(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                    listOf(6, 12, 24).forEach { h ->
                        FilterChip(
                            selected = intervalHours == h,
                            onClick = { onInterval(h) },
                            label = { Text("$h год") }
                        )
                    }
                }

                Text(
                    Loc.updChannel(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                    FilterChip(
                        selected = !beta,
                        onClick = { onChannel(false) },
                        label = { Text(tr("Стабільний")) }
                    )
                    FilterChip(
                        selected = beta,
                        onClick = { onChannel(true) },
                        label = { Text(tr("Бета")) }
                    )
                }
            }
        }
    }
}

/**
 * Автопрогравання при підключенні застосунку по BLE:
 * USB-режим — стартує трек / знімається пауза, External — AVRCP-play на телефоні.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AutoplayCard(
    bleEnabled: Boolean,
    slaveEnabled: Boolean,
    onBleChange: (Boolean) -> Unit,
    onSlaveChange: (Boolean) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(Loc.autoplayTitle(), style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold)
            Text(Loc.autoplaySubtitle(), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            Divider()

            /* Slave: головний перемикач — музика стартує, коли телефон
               підключається до Bluetooth-модуля (працює без застосунку). */
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(Loc.autoplaySlaveTitle(), style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium)
                    Text(
                        if (slaveEnabled) Loc.autoplayOn() else Loc.autoplayOff(),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (slaveEnabled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = slaveEnabled, onCheckedChange = onSlaveChange)
            }
            Text(Loc.autoplaySlaveHint(), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

            Divider()

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(Loc.autoplayBleTitle(), style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium)
                    Text(
                        if (bleEnabled) Loc.autoplayOn() else Loc.autoplayOff(),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (bleEnabled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = bleEnabled, onCheckedChange = onBleChange)
            }
            Text(Loc.autoplayHint(), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

/**
 * Wi-Fi та веб-інтерфейс плати: застосунок працює по BLE, тому обидва
 * можна вимкнути, щоб не тримати зайвий радіомодуль і HTTP-сервер.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun NetworkCard(
    wifi: Boolean,
    web: Boolean,
    onWifi: (Boolean) -> Unit,
    onWeb: (Boolean) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(
                icon = Icons.Default.Wifi,
                title = Loc.netTitle(),
                subtitle = Loc.netSubtitle()
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(Loc.netWifi(), style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium)
                    Text(
                        if (wifi) tr("увімкнено") else tr("вимкнено"),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (wifi) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = wifi, onCheckedChange = onWifi)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(Loc.netWeb(), style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium)
                    Text(
                        if (!wifi) tr("недоступно без Wi-Fi")
                        else if (web) tr("увімкнено") else tr("вимкнено"),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (web && wifi) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = web, onCheckedChange = onWeb, enabled = wifi)
            }

            Text(Loc.netHint(), style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AudioProfileCard(
    profile: Int,
    onSelect: (Int) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(
                icon = Icons.Default.GraphicEq,
                title = tr("Аудіо-буфери"),
                subtitle = tr("Затримка проти стійкості до затинів")
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = profile == 0,
                    onClick = { onSelect(0) },
                    label = { Text(tr("Низька")) }
                )
                FilterChip(
                    selected = profile == 1,
                    onClick = { onSelect(1) },
                    label = { Text(tr("Збалансований")) }
                )
                FilterChip(
                    selected = profile == 2,
                    onClick = { onSelect(2) },
                    label = { Text(tr("Стійкий")) }
                )
            }
            Text(
                tr("Застосовується після зміни режиму або перезавантаження плати"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun NetCard(
    useStatic: Boolean,
    ip: String,
    gw: String,
    mask: String,
    dns1: String,
    dns2: String,
    onStatic: (Boolean) -> Unit,
    onField: (String, String) -> Unit,
    onSave: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SectionHeader(
                icon = Icons.Default.Wifi,
                title = tr("Мережа (STA)"),
                subtitle = tr("Статична IP і DNS")
            )

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    tr("Статична IP"),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                Switch(checked = useStatic, onCheckedChange = onStatic)
            }

            if (useStatic) {
                listOf(
                    Triple("ip", tr("IP-адреса"), ip),
                    Triple("gw", tr("Шлюз"), gw),
                    Triple("mask", tr("Маска"), mask),
                    Triple("dns1", tr("DNS 1"), dns1),
                    Triple("dns2", tr("DNS 2"), dns2)
                ).forEach { (key, label, value) ->
                    OutlinedTextField(
                        value = value,
                        onValueChange = { onField(key, it) },
                        label = { Text(label) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Text(
                tr("Застосовується після перезавантаження плати"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(onClick = onSave, modifier = Modifier.fillMaxWidth()) {
                Text(tr("Зберегти"))
            }
        }
    }
}
