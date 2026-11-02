package com.example.androidplayer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidplayer.ui.i18n.Loc
import com.example.androidplayer.ui.i18n.tr
import com.example.androidplayer.ui.notify.AppNotify
import com.example.androidplayer.ui.viewmodel.UsbFlashViewModel

/**
 * Аварійна прошивка плати по USB-кабелю з телефона.
 */
@OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)
@Composable
fun UsbFlashScreen(
    viewModel: UsbFlashViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.error) {
        state.error?.let { AppNotify.error(it) }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, top = 16.dp, end = 20.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        tr("Потрібен USB-OTG перехідник. Плата має бути в режимі завантажувача (BOOT + RESET)."),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        Loc.usbHint(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Usb, contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text(Loc.usbTitle(), style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold)
                    }

                    if (state.devices.isEmpty()) {
                        Text(Loc.usbNoDevices(), style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    } else {
                        state.devices.forEach { dev ->
                            val selected = state.selected?.deviceId == dev.deviceId
                            Text(
                                text = dev.name,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (selected) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .clickable { viewModel.select(dev) }
                                    .padding(10.dp)
                            )
                        }
                    }

                    Text(
                        if (state.permission) Loc.usbPermissionGranted() else Loc.usbPermissionNeeded(),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (state.permission) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.error
                    )

                    OutlinedButton(
                        onClick = { viewModel.requestPermission() },
                        enabled = !state.busy && state.selected != null,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(Loc.usbGrantPermission())
                    }

                    OutlinedButton(
                        onClick = { viewModel.refreshDevices() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text(Loc.usbRefresh())
                    }
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(Loc.usbTargetTitle(), style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = state.target == "master",
                            onClick = { viewModel.setTarget("master") },
                            label = { Text(Loc.usbTargetMaster()) }
                        )
                        FilterChip(
                            selected = state.target == "slave",
                            onClick = { viewModel.setTarget("slave") },
                            label = { Text(Loc.usbTargetSlave()) }
                        )
                    }

                    Text(tr("Образ прошивки"), style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)

                    Text(
                        state.imageName?.let { "$it · ${state.imageSize / 1024} КБ${state.imageTag?.let { t -> " · $t" } ?: ""}" }
                            ?: tr("Образ ще не завантажений"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = { viewModel.downloadImage() },
                        enabled = !state.busy,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(Loc.usbDownloadImage())
                    }

                    Button(
                        onClick = { viewModel.flash() },
                        enabled = !state.busy,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(Loc.usbFlash())
                    }

                    if (state.busy) {
                        LinearProgressIndicator(
                            progress = state.progress / 100f,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    state.status?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary)
                    }
                    state.error?.let {
                        Text(it, style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }

        if (state.log.isNotEmpty()) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(tr("Лог прошивки"), style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Medium)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = state.log.joinToString("\n"),
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
