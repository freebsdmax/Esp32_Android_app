package com.example.androidplayer.ui.screens

import com.example.androidplayer.ui.i18n.tr

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothConnected
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidplayer.data.ble.BlePlayerClient
import com.example.androidplayer.ui.viewmodel.SettingsViewModel
import com.example.androidplayer.ui.notify.AppNotify

@Composable
fun SettingsScreen(
    ble: BlePlayerClient,
    onOpenBle: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val connected by ble.isConnected.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saveMessage) {
        uiState.saveMessage?.let { AppNotify.info(it) }
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
                onDisconnect = { ble.disconnect() }
            )
        }
        item {
            AppearanceCard(
                darkTheme = uiState.darkTheme,
                onDarkThemeChange = { viewModel.updateSetting("dark_theme", it) }
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
        item { AboutCard() }
        if (uiState.isSaved && uiState.saveMessage != null) {
            item { SavedBanner(uiState.saveMessage!!) }
        }
    }
}

@Composable
private fun BleConnectionCard(
    connected: Boolean,
    deviceLabel: String,
    onOpenBle: () -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
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
                        Text(tr("Від'єднати"))
                    }
                } else {
                    Button(
                        onClick = onConnect,
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(Icons.Default.Bluetooth, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(tr("Підключити"))
                    }
                }
                Button(
                    onClick = onOpenBle,
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(tr("Сканувати"))
                }
            }
        }
    }
}

@Composable
private fun AppearanceCard(
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit
) {
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
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(tr("Темна тема"), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    Text(
                        tr("Застосовується одразу"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = darkTheme, onCheckedChange = onDarkThemeChange)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
private fun AboutCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            SectionHeader(
                icon = Icons.Default.Info,
                title = tr("Про Ford Player"),
                subtitle = "Ford Player"
            )
            Text(tr("Версія 1.0.0"), style = MaterialTheme.typography.bodyMedium)
            Text(
                tr("Керування Ford Player через Bluetooth: плеєр, плейлист, OTA та моніторинг."),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
