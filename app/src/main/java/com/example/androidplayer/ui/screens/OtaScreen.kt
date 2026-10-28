package com.example.androidplayer.ui.screens

import com.example.androidplayer.ui.i18n.tr

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidplayer.ui.i18n.Loc
import com.example.androidplayer.ui.viewmodel.OtaUiState
import com.example.androidplayer.ui.viewmodel.OtaViewModel
import java.io.File
import com.example.androidplayer.ui.notify.AppNotify

/**
 * Экран OTA обновлений
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtaScreen(
    viewModel: OtaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    LaunchedEffect(error) { error?.let { AppNotify.error(it) } }
    LaunchedEffect(uiState.errorMessage) { uiState.errorMessage?.let { AppNotify.error(it) } }
    LaunchedEffect(uiState.uploadMessage) { uiState.uploadMessage?.let { AppNotify.info(it) } }
    LaunchedEffect(uiState.restartMessage) { uiState.restartMessage?.let { AppNotify.info(it) } }
    
    val context = LocalContext.current
    
    // File picker для выбора прошивки
    val bleFirmwarePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                val tempFile = File(context.cacheDir, "firmware_ble_${System.currentTimeMillis()}.bin")
                tempFile.outputStream().use { out -> inputStream.copyTo(out) }
                viewModel.uploadMasterFirmwareBle(tempFile)
            }
        }
    }

    val slaveFirmwarePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                val tempFile = File(context.cacheDir, "slave_ble_${System.currentTimeMillis()}.bin")
                tempFile.outputStream().use { out -> inputStream.copyTo(out) }
                viewModel.uploadSlaveFirmwareBle(tempFile)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок
        
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Информация о версиях
            item {
                VersionInfoSection(uiState = uiState)
            }
            
            // Прогресс OTA
            if (uiState.isActive || uiState.isUploading) {
                item {
                    OtaProgressSection(uiState = uiState)
                }
            }
            
            // Загрузка прошивки Master
                        item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "BLE OTA (Master)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = tr("Works without Wi-Fi. Slow (1-5 min)."),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Button(
                            onClick = { bleFirmwarePicker.launch("application/octet-stream") },
                            enabled = !uiState.isUploading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(tr("Update over BLE"))
                        }
                    }
                }
            }
            
            // Загрузка прошивки Slave
                        
            // Управление устройством
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "BLE OTA (Slave)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = tr("Master передає прошивку Slave по UART. Не вимикайте живлення."),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Button(
                            onClick = { slaveFirmwarePicker.launch("application/octet-stream") },
                            enabled = !uiState.isUploading,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(tr("Оновити Slave"))
                        }
                    }
                }
            }

            item {
                DeviceControlSection(
                    onRestart = { viewModel.restartDevice() }
                )
            }
            
            // Сообщения
            uiState.uploadMessage?.let { message ->
                item {
                    SuccessCard(message = message)
                }
            }
            
            uiState.restartMessage?.let { message ->
                item {
                    InfoCard(message = message)
                }
            }
        }
        
        // Обработка ошибок
        error?.let { errorMessage ->
            ErrorCard(
                message = errorMessage,
                onDismiss = { viewModel.clearError() }
            )
        }

        uiState.errorMessage?.let { errorMessage ->
            ErrorCard(
                message = errorMessage,
                onDismiss = { viewModel.clearError() }
            )
        }
    }
}

/**
 * Секция информации о версиях
 */
@Composable
private fun VersionInfoSection(
    uiState: OtaUiState
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tr("📋 Информация о версиях"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Master версии
            Text(
                text = tr("🖥️ Master ESP32:"),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            
            VersionItem("Player", uiState.masterPlayerVersion)
            VersionItem("Web", uiState.masterWebVersion)
            VersionItem("UART", uiState.masterUartVersion)
            
            // Slave версии
            Text(
                text = tr("🎵 Slave ESP32:"),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp)
            )
            
            VersionItem("Main", uiState.slaveMainVersion)
            VersionItem("CRC", uiState.slaveCrcVersion)
            VersionItem("UART", uiState.slaveUartVersion)
        }
    }
}

/**
 * Элемент версии
 */
@Composable
private fun VersionItem(
    label: String,
    version: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = version,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (Loc.isDisconnectedText(version)) {
                MaterialTheme.colorScheme.error
            } else {
                MaterialTheme.colorScheme.primary
            }
        )
    }
}

/**
 * Секция прогресса OTA
 */
@Composable
private fun OtaProgressSection(
    uiState: OtaUiState
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tr("📊 Прогресс OTA"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = tr("Ціль: %s").format(uiState.uploadTarget),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            LinearProgressIndicator(
                progress = uiState.uploadProgress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${(uiState.uploadProgress * 100).toInt()}%  " +
                        Loc.otaPhaseName(uiState.uploadPhaseApply),
                    style = MaterialTheme.typography.bodySmall
                )
                if (uiState.uploadTotal > 0) {
                    Text(
                        text = Loc.sizeText(uiState.uploadBytes) + " / " +
                            Loc.sizeText(uiState.uploadTotal),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (uiState.uploadBytesPerSec > 0)
                        Loc.speedText(uiState.uploadBytesPerSec)
                    else "…",
                    style = MaterialTheme.typography.bodySmall
                )
                if (uiState.uploadEtaSeconds >= 0) {
                    Text(
                        text = Loc.etaText(uiState.uploadEtaSeconds),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (uiState.isUartSlave) {
                Text(
                    text = tr("Блок: %d").format(uiState.currentBlock),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

/**
 * Секция загрузки прошивки Master
 */
@Composable
private fun MasterFirmwareSection(
    onSelectFile: () -> Unit,
    onUpload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tr("🖥️ Master ESP32 (этот прибор)"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = tr("Обновление основной прошивки ESP32. Устройство будет перезагружено после обновления."),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Button(
                onClick = onUpload,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Upload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(tr("Выбрать и загрузить прошивку Master"))
            }
        }
    }
}

/**
 * Секция загрузки прошивки Slave
 */
@Composable
private fun SlaveFirmwareSection(
    onSelectFile: () -> Unit,
    onUpload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tr("🎵 Slave ESP32 (Bluetooth модуль через UART)"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = tr("Обновление прошивки Slave ESP32 через UART. Передача происходит блоками по 240 байт с проверкой CRC."),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Button(
                onClick = onUpload,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(Icons.Default.Upload, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(tr("Выбрать и загрузить прошивку Slave"))
            }
        }
    }
}

/**
 * Секция управления устройством
 */
@Composable
private fun DeviceControlSection(
    onRestart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tr("⚙️ Управление устройством"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(tr("Перезагрузить устройство"))
            }
            
            Text(
                text = tr("⚠️ Внимание: Перезагрузка прервет текущее воспроизведение"),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

/**
 * Карточка успеха
 */
@Composable
private fun SuccessCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.CheckCircle,
                contentDescription = tr("Успех"),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Карточка информации
 */
@Composable
private fun InfoCard(
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = tr("Информация"),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

/**
 * Карточка ошибки
 */
@Composable
private fun ErrorCard(
    message: String,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = tr("Ошибка"),
                tint = MaterialTheme.colorScheme.onErrorContainer
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = message,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = tr("Закрыть"),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}