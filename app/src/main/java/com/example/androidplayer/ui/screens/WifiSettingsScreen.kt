package com.example.androidplayer.ui.screens

import com.example.androidplayer.ui.i18n.tr

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidplayer.ui.viewmodel.WifiSettingsUiState
import com.example.androidplayer.ui.viewmodel.WifiSettingsViewModel

/**
 * Экран настроек Wi-Fi
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WifiSettingsScreen(
    viewModel: WifiSettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Выбор режима Wi-Fi
                item {
                    WiFiModeSection(
                        mode = uiState.mode,
                        onModeChange = { viewModel.updateMode(it) }
                    )
                }
                
                // Настройки STA режима
                item {
                    StaSettingsSection(
                        ssid = uiState.staSsid,
                        password = uiState.staPassword,
                        onSsidChange = { viewModel.updateStaSsid(it) },
                        onPasswordChange = { viewModel.updateStaPassword(it) }
                    )
                }
                
                // Настройки AP режима
                item {
                    ApSettingsSection(
                        ssid = uiState.apSsid,
                        password = uiState.apPassword,
                        onSsidChange = { viewModel.updateApSsid(it) },
                        onPasswordChange = { viewModel.updateApPassword(it) }
                    )
                }
                
                // Кнопка сохранения
                item {
                    SaveButton(
                        onSave = { viewModel.saveWifiSettings() },
                        isLoading = isLoading
                    )
                }
                
                // Сообщение о сохранении
                uiState.saveMessage?.let { message ->
                    item {
                        SuccessCard(message = message)
                    }
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
 * Секция выбора режима Wi-Fi
 */
@Composable
private fun WiFiModeSection(
    mode: Int,
    onModeChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tr("Режим Wi-Fi"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Кнопка AP режима
                Button(
                    onClick = { onModeChange(0) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mode == 0) 
                            MaterialTheme.colorScheme.primary else 
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(Icons.Default.Router, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("AP")
                }
                
                // Кнопка STA режима
                Button(
                    onClick = { onModeChange(1) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mode == 1) 
                            MaterialTheme.colorScheme.primary else 
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(Icons.Default.Wifi, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("STA")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = when (mode) {
                    0 -> tr("AP (Access Point) — ESP32 створює власну Wi-Fi мережу")
                    1 -> tr("STA (Station) — ESP32 підключається до наявної Wi-Fi мережі")
                    else -> tr("Невідомий режим")
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Секция настроек STA режима
 */
@Composable
private fun StaSettingsSection(
    ssid: String,
    password: String,
    onSsidChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tr("STA Настройки (подключение к сети)"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            OutlinedTextField(
                value = ssid,
                onValueChange = onSsidChange,
                label = { Text(tr("SSID (название сети)")) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Wifi, contentDescription = null)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            var passwordVisible by remember { mutableStateOf(false) }
            
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text(tr("Пароль")) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) tr("Приховати пароль") else tr("Показати пароль")
                        )
                    }
                }
            )
        }
    }
}

/**
 * Секция настроек AP режима
 */
@Composable
private fun ApSettingsSection(
    ssid: String,
    password: String,
    onSsidChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = tr("AP Настройки (создание сети)"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            OutlinedTextField(
                value = ssid,
                onValueChange = onSsidChange,
                label = { Text(tr("SSID (название сети)")) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Router, contentDescription = null)
                }
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            var passwordVisible by remember { mutableStateOf(false) }
            
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text(tr("Пароль")) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (passwordVisible) tr("Приховати пароль") else tr("Показати пароль")
                        )
                    }
                }
            )
        }
    }
}

/**
 * Кнопка сохранения настроек
 */
@Composable
private fun SaveButton(
    onSave: () -> Unit,
    isLoading: Boolean
) {
    Button(
        onClick = onSave,
        modifier = Modifier.fillMaxWidth(),
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(tr("Сохранить настройки"))
    }
}

/**
 * Карточка успешного сохранения
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