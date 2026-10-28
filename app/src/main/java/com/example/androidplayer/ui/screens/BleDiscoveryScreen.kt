package com.example.androidplayer.ui.screens

import com.example.androidplayer.ui.i18n.tr

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidplayer.data.ble.BlePlayerClient
import com.example.androidplayer.ui.viewmodel.PlayerViewModel
import timber.log.Timber

/**
 * Екран для пошуку та підключення до BLE пристрою
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BleDiscoveryScreen(
    onDeviceSelected: (String) -> Unit,
    onNavigateBack: () -> Unit,
    playerViewModel: PlayerViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val bleClient = playerViewModel.bleClient
    
    val isConnected by bleClient.isConnected.collectAsStateWithLifecycle()
    val discoveredDevices by bleClient.discoveredDevices.collectAsStateWithLifecycle()
    var isScanning by remember { mutableStateOf(false) }

    val notificationsReady by bleClient.notificationsReady.collectAsStateWithLifecycle()
    val serviceReady by bleClient.serviceReady.collectAsStateWithLifecycle()
    val serviceError by bleClient.serviceError.collectAsStateWithLifecycle()

    LaunchedEffect(serviceReady) {
        if (serviceReady) {
            playerViewModel.connectBleClient()
        }
    }

    // Коли підключились через BLE і CCCD/notify готові, перемикаємо транспорт і вантажимо дані
    LaunchedEffect(isConnected, notificationsReady) {
        Timber.d("🔍 LaunchedEffect: isConnected=$isConnected, notificationsReady=$notificationsReady")
        if (isConnected && notificationsReady) {
            Timber.d("✅ BLE notifications ready, loading player state")
            // Не зупиняємо скан - пристрій залишається видимим
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.BLUETOOTH_SCAN] == true &&
            permissions[Manifest.permission.BLUETOOTH_CONNECT] == true) {
            if (bleClient.hasRememberedDevice()) {
                bleClient.connectRememberedDevice()
            } else {
                bleClient.startScan()
                isScanning = true
            }
        }
    }

    LaunchedEffect(Unit) {
        val permissions = listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
        
        val needsPermission = permissions.any { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }
        
        if (needsPermission) {
            permissionLauncher.launch(permissions.toTypedArray())
        } else if (bleClient.hasRememberedDevice()) {
            bleClient.connectRememberedDevice()
        } else {
            bleClient.startScan()
            isScanning = true
        }
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Стан підключення
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        serviceError != null -> MaterialTheme.colorScheme.errorContainer
                        notificationsReady -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (isConnected) Icons.Default.BluetoothConnected else Icons.Default.Bluetooth,
                        contentDescription = null,
                        tint = if (isConnected) 
                            MaterialTheme.colorScheme.primary 
                        else 
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            when {
                                serviceError != null -> tr("Підключено, сервіс не готовий")
                                notificationsReady -> tr("Підключено, плеєр готовий")
                                serviceReady -> tr("Підключено, вмикаю сповіщення…")
                                isConnected -> tr("BLE підключено, шукаю сервіс плеєра…")
                                else -> tr("Не підключено")
                            },
                            fontWeight = FontWeight.Bold
                        )
                        serviceError?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
                    }
                }
            }

            if (isConnected) {
                OutlinedButton(
                    onClick = {
                        playerViewModel.disconnectBle()
                        isScanning = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Icon(Icons.Default.LinkOff, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(tr("Отключить BLE и использовать Wi-Fi"))
                }
            }

            // Кнопка сканування
            Button(
                onClick = {
                    bleClient.startScan()
                    isScanning = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = !isScanning
            ) {
                if (isScanning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (isScanning) tr("🔍 Сканування...") else tr("🔍 Сканувати"))
            }

            // Список пристроїв
            if (discoveredDevices.isNotEmpty()) {
                Text(
                    tr("📱 Знайдено пристроїв: %d").format(discoveredDevices.size),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn {
                    items(discoveredDevices) { device ->
                        DeviceCard(
                            device = device,
                            onConnect = { selectedDevice ->
                                if (bleClient.connect(selectedDevice)) {
                                    playerViewModel.connectBleClient()
                                    onDeviceSelected(selectedDevice.address)
                                }
                            }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.BluetoothSearching,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            tr("Пристрої не знайдені"),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceCard(
    device: android.bluetooth.BluetoothDevice,
    onConnect: (android.bluetooth.BluetoothDevice) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    device.name ?: "Unknown Device",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    device.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = { onConnect(device) }
            ) {
                Icon(Icons.Default.Link, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text(tr("Підключ"))
            }
        }
    }
}
