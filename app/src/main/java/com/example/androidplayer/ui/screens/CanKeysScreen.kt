package com.example.androidplayer.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidplayer.data.ble.BlePlayerClient
import com.example.androidplayer.ui.components.CardHeader
import com.example.androidplayer.ui.i18n.Loc
import com.example.androidplayer.ui.i18n.tr
import com.example.androidplayer.ui.viewmodel.SettingsViewModel

/**
 * Окремий екран зі списком навчених CAN-кнопок.
 *
 * Винесений з налаштувань, щоб не подовжувати і не засмічувати екран налаштувань:
 * тут лише список прив'язок (ID/байт/значення) з видаленням і очищенням.
 */
@Composable
fun CanKeysScreen(
    ble: BlePlayerClient,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val keys by viewModel.canKeys.collectAsStateWithLifecycle()
    val connected by ble.isConnected.collectAsStateWithLifecycle()
    var confirmClear by remember { mutableStateOf(false) }

    LaunchedEffect(connected) {
        if (connected) viewModel.loadCanKeys()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                CardHeader(
                    icon = Icons.Default.VpnKey,
                    title = Loc.canKeysTitle(),
                    subtitle = if (keys.isEmpty()) Loc.canKeysEmpty() else "${keys.size}"
                )
                Text(
                    Loc.canKeysScreenHint(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (!connected) {
                    Text(
                        tr("Немає з'єднання з платою — список може бути неповним"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        if (keys.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Default.VpnKey,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    Loc.canKeysEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                itemsIndexed(keys) { index, key ->
                    KeyRow(
                        key = key,
                        onRemove = { viewModel.removeCanKey(index) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { viewModel.loadCanKeys() },
                modifier = Modifier.weight(1f),
                enabled = connected
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(Loc.refresh())
            }
            Button(
                onClick = { confirmClear = true },
                modifier = Modifier.weight(1f),
                enabled = keys.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                Icon(Icons.Default.Delete, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(Loc.clearAll())
            }
        }
    }

    if (confirmClear) {
        AlertDialog(
            onDismissRequest = { confirmClear = false },
            title = { Text(Loc.canKeysClearConfirmTitle()) },
            text = { Text(Loc.canKeysClearConfirmText()) },
            confirmButton = {
                TextButton(onClick = {
                    confirmClear = false
                    viewModel.clearCanKeys()
                }) { Text(Loc.clearAll()) }
            },
            dismissButton = {
                TextButton(onClick = { confirmClear = false }) { Text(Loc.cancel()) }
            }
        )
    }
}

@Composable
private fun KeyRow(
    key: BlePlayerClient.CanKey,
    onRemove: () -> Unit
) {
    val actionName = when (key.action) {
        0 -> Loc.canLearnNext()
        1 -> Loc.canLearnPrev()
        else -> Loc.canLearnMode()
    }
    val badgeColor = when (key.action) {
        0 -> MaterialTheme.colorScheme.primary
        1 -> MaterialTheme.colorScheme.tertiary
        else -> MaterialTheme.colorScheme.secondary
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.small,
                color = badgeColor.copy(alpha = 0.15f)
            ) {
                Text(
                    actionName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = badgeColor,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = Loc.canKeyRow(key.action, key.id, key.byte, key.mask, key.value)
                    .substringAfter(": "),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = Loc.remove(),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
