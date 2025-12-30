package com.example.androidplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidplayer.ui.viewmodel.SettingsViewModel

/**
 * Экран настроек приложения
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var ipText by remember(uiState.esp32Ip) { mutableStateOf(uiState.esp32Ip) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        // Заголовок
        Text(
            text = "⚙️ Настройки",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Настройки подключения
            item {
                ConnectionSettingsSection(
                    ip = ipText,
                    onIpChange = { ipText = it },
                    onSaveIp = { 
                        viewModel.updateEsp32Ip(ipText)
                        viewModel.saveEsp32Ip()
                    },
                    defaultIp = SettingsViewModel.DEFAULT_ESP32_IP
                )
            }
            
            // Общие настройки
            item {
                GeneralSettingsSection(
                    autoReconnect = uiState.autoReconnect,
                    showNotifications = uiState.showNotifications,
                    darkTheme = uiState.darkTheme,
                    onSettingChange = { key, value -> 
                        viewModel.updateSetting(key, value)
                    }
                )
            }
            
            // Информация о приложении
            item {
                AppInfoSection()
            }
            
            // Сообщение о сохранении
            if (uiState.isSaved && uiState.saveMessage != null) {
                item {
                    SuccessCard(message = uiState.saveMessage!!)
                }
            }
        }
    }
}

/**
 * Секция настроек подключения
 */
@Composable
private fun ConnectionSettingsSection(
    ip: String,
    onIpChange: (String) -> Unit,
    onSaveIp: () -> Unit,
    defaultIp: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "🔌 Подключение к ESP32",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            OutlinedTextField(
                value = ip,
                onValueChange = onIpChange,
                label = { Text("IP адрес ESP32") },
                placeholder = { Text("http://192.168.4.1") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                leadingIcon = {
                    Icon(Icons.Default.Wifi, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { onIpChange(defaultIp) }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Сбросить")
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "По умолчанию: $defaultIp",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onSaveIp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Сохранить IP адрес")
            }
        }
    }
}

/**
 * Секция общих настроек
 */
@Composable
private fun GeneralSettingsSection(
    autoReconnect: Boolean,
    showNotifications: Boolean,
    darkTheme: Boolean,
    onSettingChange: (String, Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📱 Общие настройки",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            SettingSwitch(
                title = "Автоподключение",
                description = "Автоматически подключаться к ESP32 при запуске",
                checked = autoReconnect,
                onCheckedChange = { onSettingChange("auto_reconnect", it) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            SettingSwitch(
                title = "Уведомления",
                description = "Показывать уведомления о статусе подключения",
                checked = showNotifications,
                onCheckedChange = { onSettingChange("show_notifications", it) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            SettingSwitch(
                title = "Тёмная тема",
                description = "Использовать тёмную тему оформления",
                checked = darkTheme,
                onCheckedChange = { onSettingChange("dark_theme", it) }
            )
        }
    }
}

/**
 * Переключатель настройки
 */
@Composable
private fun SettingSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

/**
 * Секция информации о приложении
 */
@Composable
private fun AppInfoSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "ℹ️ О приложении",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Text(
                text = "ESP32 Player Control",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            
            Text(
                text = "Версия: 1.0.0",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Приложение для управления ESP32 Music Player по Wi-Fi. Поддерживает управление воспроизведением, настройки Wi-Fi, OTA обновления и мониторинг системы.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Карточка успеха
 */
@Composable
private fun SuccessCard(message: String) {
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