package com.example.androidplayer.ui.screens

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
import com.example.androidplayer.ui.viewmodel.OtaUiState
import com.example.androidplayer.ui.viewmodel.OtaViewModel
import java.io.File

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
    
    val context = LocalContext.current
    
    // File picker для выбора прошивки
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { inputStream ->
                val fileName = "firmware_${System.currentTimeMillis()}.bin"
                val tempFile = File(context.cacheDir, fileName)
                tempFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                
                // Определяем тип прошивки по имени файла
                when {
                    fileName.contains("master", ignoreCase = true) || 
                    fileName.contains("main", ignoreCase = true) -> {
                        viewModel.uploadMasterFirmware(tempFile)
                    }
                    fileName.contains("slave", ignoreCase = true) -> {
                        viewModel.uploadSlaveFirmware(tempFile)
                    }
                    else -> {
                        // По умолчанию загружаем на Master
                        viewModel.uploadMasterFirmware(tempFile)
                    }
                }
            }
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        // Заголовок
        Text(
            text = "🔧 OTA Обновления",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
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
                MasterFirmwareSection(
                    onSelectFile = {
                        filePickerLauncher.launch("application/octet-stream")
                    },
                    onUpload = {
                        filePickerLauncher.launch("application/octet-stream")
                    }
                )
            }
            
            // Загрузка прошивки Slave
            item {
                SlaveFirmwareSection(
                    onSelectFile = {
                        filePickerLauncher.launch("application/octet-stream")
                    },
                    onUpload = {
                        filePickerLauncher.launch("application/octet-stream")
                    }
                )
            }
            
            // Управление устройством
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
                text = "📋 Информация о версиях",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Master версии
            Text(
                text = "🖥️ Master ESP32:",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            
            VersionItem("Player", uiState.masterPlayerVersion)
            VersionItem("Web", uiState.masterWebVersion)
            VersionItem("UART", uiState.masterUartVersion)
            
            // Slave версии
            Text(
                text = "🎵 Slave ESP32:",
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
            color = if (version.contains("Не подключен")) {
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
                text = "📊 Прогресс OTA",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "Цель: ${uiState.uploadTarget}",
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
                    text = "${(uiState.uploadProgress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "${uiState.writtenBytes} / ${uiState.totalBytes} байт",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            if (uiState.isUartSlave) {
                Text(
                    text = "Блок: ${uiState.currentBlock}",
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
                text = "🖥️ Master ESP32 (этот прибор)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "Обновление основной прошивки ESP32. Устройство будет перезагружено после обновления.",
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
                Text("Выбрать и загрузить прошивку Master")
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
                text = "🎵 Slave ESP32 (Bluetooth модуль через UART)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "Обновление прошивки Slave ESP32 через UART. Передача происходит блоками по 240 байт с проверкой CRC.",
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
                Text("Выбрать и загрузить прошивку Slave")
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
                text = "⚙️ Управление устройством",
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
                Text("Перезагрузить устройство")
            }
            
            Text(
                text = "⚠️ Внимание: Перезагрузка прервет текущее воспроизведение",
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
                contentDescription = "Успех",
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
                contentDescription = "Информация",
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
                contentDescription = "Ошибка",
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
                    contentDescription = "Закрыть",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}