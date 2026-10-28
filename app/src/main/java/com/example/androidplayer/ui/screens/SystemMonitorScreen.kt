package com.example.androidplayer.ui.screens

import com.example.androidplayer.ui.i18n.Loc
import com.example.androidplayer.ui.i18n.tr

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidplayer.ui.viewmodel.SystemMonitorUiState
import com.example.androidplayer.ui.viewmodel.SystemMonitorViewModel
import com.example.androidplayer.ui.notify.AppNotify

/**
 * Экран мониторинга системы
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemMonitorScreen(
    viewModel: SystemMonitorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()

    // Log settings are re-read every time this screen is opened, so the
    // "Load" button is only needed for a manual refresh.
    LaunchedEffect(Unit) {
        viewModel.loadCurrentLogSettings()
    }

    LaunchedEffect(error) {
        error?.let { AppNotify.error(it) }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок с кнопкой обновления
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = tr("Огляд"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(
                onClick = { viewModel.refreshAllData() },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.Default.Refresh, contentDescription = tr("Обновить"))
                }
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Системная информация
            item {
                SystemInfoSection(
                    systemInfo = uiState.systemInfo,
                    onFormatInfo = { viewModel.getFormattedSystemInfo() }
                )
            }
            
            // Статистика CAN
            item {
                CanStatisticsSection(
                    canStatistics = uiState.canStatistics,
                    onFormatStats = { viewModel.getFormattedCanStatistics() }
                )
            }
            
            // Настройки логирования
            item {
                LogSettingsSection(
                    uiState = uiState,
                    onUpdateSettings = { level, verbose, components ->
                        viewModel.updateLogSettings(level, verbose, components)
                    },
                    onLoadCurrentSettings = { viewModel.loadCurrentLogSettings() }
                )
            }
            
            // Системные логи
            item {
                SystemLogsSection(
                    logs = uiState.systemLogs,
                    onClearLogs = { viewModel.clearSystemLogs() },
                    onRefreshLogs = { viewModel.loadSystemLogs() }
                )
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
 * Секция системной информации
 */
@Composable
private fun SystemInfoSection(
    systemInfo: com.example.androidplayer.data.model.SystemInfo?,
    onFormatInfo: () -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tr("💻 Системная информация"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            if (systemInfo != null) {
                Text(
                    text = onFormatInfo(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = tr("Загрузка информации о системе..."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Секция статистики CAN
 */
@Composable
private fun CanStatisticsSection(
    canStatistics: com.example.androidplayer.data.model.CanStatistics?,
    onFormatStats: () -> String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tr("📡 Статистика CAN"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            if (canStatistics != null) {
                Text(
                    text = onFormatStats(),
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = tr("Загрузка статистики CAN..."),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Секция настроек логирования
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LogSettingsSection(
    uiState: SystemMonitorUiState,
    onUpdateSettings: (String, Boolean, com.example.androidplayer.data.model.ComponentLogSettings) -> Unit,
    onLoadCurrentSettings: () -> Unit
) {
    var expandedLevel by remember { mutableStateOf(false) }
    var selectedLogLevel by remember(uiState.logLevel) { mutableStateOf(uiState.logLevel) }
    var verboseLogs by remember(uiState.verboseLogs) { mutableStateOf(uiState.verboseLogs) }
    var componentLogs by remember(uiState.componentLogSettings) { mutableStateOf(uiState.componentLogSettings) }
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tr("📝 Настройки логирования"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Уровень логов
            ExposedDropdownMenuBox(
                expanded = expandedLevel,
                onExpandedChange = { expandedLevel = !expandedLevel }
            ) {
                OutlinedTextField(
                    value = getLogLevelName(selectedLogLevel),
                    onValueChange = { },
                    readOnly = true,
                    label = { Text(tr("Уровень логов")) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedLevel) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                
                ExposedDropdownMenu(
                    expanded = expandedLevel,
                    onDismissRequest = { expandedLevel = false }
                ) {
                    LogLevel.values().forEach { level ->
                        DropdownMenuItem(
                            text = { Text(level.displayName) },
                            onClick = {
                                selectedLogLevel = level.value
                                expandedLevel = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Verbose логи
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = verboseLogs,
                    onCheckedChange = { verboseLogs = it }
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(tr("Детальные логи (эмодзи и баннеры)"))
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Настройки компонентов
            Text(
                text = tr("Логи компонентов:"),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            ComponentLogItem(Loc.logGroupPlayer(), componentLogs.group_player == 1) {
                componentLogs = componentLogs.copy(
                    group_player = if (componentLogs.group_player == 1) 0 else 1
                )
            }
            ComponentLogItem(Loc.logGroupConnect(), componentLogs.group_connect == 1) {
                componentLogs = componentLogs.copy(
                    group_connect = if (componentLogs.group_connect == 1) 0 else 1
                )
            }
            ComponentLogItem(Loc.logGroupOta(), componentLogs.group_ota == 1) {
                componentLogs = componentLogs.copy(
                    group_ota = if (componentLogs.group_ota == 1) 0 else 1
                )
            }
            ComponentLogItem(Loc.logGroupCar(), componentLogs.group_car == 1) {
                componentLogs = componentLogs.copy(
                    group_car = if (componentLogs.group_car == 1) 0 else 1
                )
            }
            ComponentLogItem(Loc.logGroupSystem(), componentLogs.group_system == 1) {
                componentLogs = componentLogs.copy(
                    group_system = if (componentLogs.group_system == 1) 0 else 1
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onLoadCurrentSettings,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(tr("Загрузить"))
                }
                
                Button(
                    onClick = {
                        onUpdateSettings(
                            selectedLogLevel,
                            verboseLogs,
                            componentLogs
                        )
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(tr("Сохранить"))
                }
            }
        }
    }
}

/**
 * Элемент настройки логов компонента
 */
@Composable
private fun ComponentLogItem(
    name: String,
    enabled: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Switch(
            checked = enabled,
            onCheckedChange = { onToggle() }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(name)
    }
}

/**
 * Секция системных логов
 */
@Composable
private fun SystemLogsSection(
    logs: String,
    onClearLogs: () -> Unit,
    onRefreshLogs: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tr("📋 Системные логи"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Row {
                    IconButton(onClick = onRefreshLogs) {
                        Icon(Icons.Default.Refresh, contentDescription = tr("Обновить логи"))
                    }
                    IconButton(onClick = onClearLogs) {
                        Icon(Icons.Default.Delete, contentDescription = tr("Очистить логи"))
                    }
                }
            }
            
            if (logs.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    val listState = rememberLazyListState()
                    
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    ) {
                        items(logs.lines()) { line ->
                            Text(
                                text = line,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = tr("Логи отсутствуют"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                )
            }
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

/**
 * Уровни логирования
 */
enum class LogLevel(val value: String, val displayName: String) {
    NONE("0", "None"),
    ERROR("1", "Error"),
    WARN("2", "Warning"),
    INFO("3", "Info"),
    DEBUG("4", "Debug"),
    VERBOSE("5", "Verbose")
}

/**
 * Получение названия уровня логирования
 */
private fun getLogLevelName(level: String): String {
    return when (level) {
        "0" -> "None"
        "1" -> "Error"
        "2" -> "Warning"
        "3" -> "Info"
        "4" -> "Debug"
        "5" -> "Verbose"
        else -> "Unknown"
    }
}