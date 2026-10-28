package com.example.androidplayer.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.androidplayer.data.model.VersionCompare
import com.example.androidplayer.ui.i18n.Loc
import com.example.androidplayer.ui.viewmodel.UpdateViewModel

/**
 * Оновлення з GitHub: застосунок, прошивка Master, прошивка Slave.
 * Живе в вкладці OTA.
 */
@Composable
fun UpdatesCard(
    state: UpdateViewModel.UpdateUiState,
    onCheck: () -> Unit,
    onInstallApp: () -> Unit,
    onFlashFirmware: () -> Unit,
    onFlashSlave: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CardHeader(
                    icon = Icons.Default.SystemUpdate,
                    title = Loc.updTitle(),
                    subtitle = Loc.updSubtitle()
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onCheck, enabled = !state.checking && !state.busy) {
                    if (state.checking) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = Loc.updCheck())
                    }
                }
            }

            UpdateRow(
                label = Loc.updApp(),
                current = VersionCompare.short(state.appCurrent),
                latest = state.appLatest?.let { VersionCompare.short(it) },
                available = state.appUpdate,
                actionText = Loc.updInstall(),
                enabled = state.appUpdate && !state.busy,
                onAction = onInstallApp,
                upToDateText = Loc.updUpToDate()
            )

            UpdateRow(
                label = Loc.updSlave(),
                current = VersionCompare.short(state.slaveCurrent),
                latest = state.slaveLatest?.let { VersionCompare.short(it) },
                available = state.slaveUpdate,
                actionText = if (state.connected) Loc.updFlash() else Loc.updNoConnection(),
                enabled = state.slaveUpdate && state.connected && !state.busy,
                onAction = onFlashSlave,
                upToDateText = if (state.connected) Loc.updUpToDate() else Loc.updNoConnection()
            )

            UpdateRow(
                label = Loc.updFirmware(),
                current = VersionCompare.short(state.fwCurrent),
                latest = state.fwLatest?.let { VersionCompare.short(it) },
                available = state.fwUpdate,
                actionText = if (state.connected) Loc.updFlash() else Loc.updNoConnection(),
                enabled = state.fwUpdate && state.connected && !state.busy,
                onAction = onFlashFirmware,
                upToDateText = if (state.connected) Loc.updUpToDate() else Loc.updNoConnection()
            )

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

@Composable
private fun UpdateRow(
    label: String,
    current: String,
    latest: String?,
    available: Boolean,
    actionText: String,
    enabled: Boolean,
    onAction: () -> Unit,
    upToDateText: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium)
        Text(
            text = if (latest != null && available) "$current  →  $latest" else "$current  ·  $upToDateText",
            style = MaterialTheme.typography.bodySmall,
            color = if (available) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (available) {
            Button(
                onClick = onAction,
                enabled = enabled,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(actionText, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
internal fun CardHeader(
    icon: ImageVector,
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
