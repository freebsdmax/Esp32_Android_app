package com.example.androidplayer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.androidplayer.ui.viewmodel.PlayerUiState
import com.example.androidplayer.ui.viewmodel.PlayerViewModel
import timber.log.Timber

/**
 * Главный экран управления плеером
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val error by viewModel.error.collectAsStateWithLifecycle()
    
    // Автообновление позиции — делаем тихий опрос через ViewModel
    LaunchedEffect(uiState.currentTrack, uiState.durationMs) {
        while (true) {
            kotlinx.coroutines.delay(1000)
            viewModel.pollPlayerStatus()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Заголовок
        Text(
            text = "🎵 ESP32 Music Player",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        // Статус плеера
        PlayerStatusCard(
            uiState = uiState,
            isLoading = isLoading
        )
        
        // Элементы управления
        PlayerControls(
            uiState = uiState,
            onPlayPause = { viewModel.togglePlayback() },
            onNext = { viewModel.nextTrack() },
            onPrevious = { viewModel.previousTrack() },
            onSwitchMode = { viewModel.switchMode() },
            onToggleSniffer = { viewModel.toggleSniffer() }
        )
        
        // Позиция воспроизведения
        PlaybackPosition(
            uiState = uiState,
            onPositionChange = { position -> viewModel.setPosition(position) }
        )
        
        // Плейлист
        PlaylistSection(
            uiState = uiState,
            onTrackSelect = { trackIndex -> viewModel.playTrack(trackIndex) }
        )
        
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
 * Карточка статуса плеера
 */
@Composable
private fun PlayerStatusCard(
    uiState: PlayerUiState,
    isLoading: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Загрузка...")
                }
            } else {
                Text(
                    text = "Трек ${uiState.currentTrack + 1} из ${uiState.playlistSize}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Режим: ${uiState.mode}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Wi-Fi CAN-Sniffer: ${if (uiState.snifferEnabled) "Включен" else "Выключен"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

/**
 * Элементы управления плеером
 */
@Composable
private fun PlayerControls(
    uiState: PlayerUiState,
    onPlayPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSwitchMode: () -> Unit,
    onToggleSniffer: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Управление",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Основные кнопки управления
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = onPrevious,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Предыдущий трек"
                    )
                }
                
                FloatingActionButton(
                    onClick = onPlayPause,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        if (uiState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (uiState.isPaused) "Воспроизвести" else "Пауза"
                    )
                }
                
                IconButton(
                    onClick = onNext,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Следующий трек"
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Дополнительные кнопки
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = onSwitchMode,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Режим")
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Button(
                    onClick = onToggleSniffer,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (uiState.snifferEnabled) 
                            MaterialTheme.colorScheme.primary else 
                            MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.Radio, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CAN-Sniffer")
                }
            }
        }
    }
}

/**
 * Позиция воспроизведения
 */
@Composable
private fun PlaybackPosition(
    uiState: PlayerUiState,
    onPositionChange: (Int) -> Unit
) {
    if (uiState.durationMs > 0) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Позиция",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Interactive seek slider with user-interaction guard
                var isSeeking by remember { mutableStateOf(false) }
                var sliderPosition by remember { mutableStateOf(0f) }

                // Update slider position from uiState when user is not interacting
                LaunchedEffect(uiState.positionMs, uiState.durationMs) {
                    if (!isSeeking && uiState.durationMs > 0) {
                        sliderPosition = uiState.positionMs.toFloat() / uiState.durationMs
                    }
                }

                Slider(
                    value = sliderPosition.coerceIn(0f..1f),
                    onValueChange = { newValue ->
                        sliderPosition = newValue
                        isSeeking = true
                    },
                    onValueChangeFinished = {
                        // Convert slider value back to milliseconds and request seek
                        val newMs = (sliderPosition * uiState.durationMs).toInt()
                        onPositionChange(newMs)
                        isSeeking = false
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    val displayMs = if (isSeeking) (sliderPosition * uiState.durationMs).toInt() else uiState.positionMs
                    Text(formatTime(displayMs))
                    Text(formatTime(uiState.durationMs))
                }
            }
        }
    }
}

/**
 * Секция плейлиста
 */
@Composable
private fun PlaylistSection(
    uiState: PlayerUiState,
    onTrackSelect: (Int) -> Unit
) {
    if (uiState.playlist.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Плейлист (${uiState.playlist.size} треков)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                val listState = rememberLazyListState()
                
                LazyColumn(
                    state = listState,
                    modifier = Modifier.height(200.dp)
                ) {
                    itemsIndexed(uiState.playlist) { index, track ->
                        TrackItem(
                            trackName = track,
                            isPlaying = index == uiState.currentTrack,
                            onClick = { onTrackSelect(index) }
                        )
                        
                        if (index < uiState.playlist.size - 1) {
                            Divider()
                        }
                    }
                }
            }
        }
    }
}

/**
 * Элемент трека в плейлисте
 */
@Composable
private fun TrackItem(
    trackName: String,
    isPlaying: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isPlaying) {
            Icon(
                Icons.Default.VolumeUp,
                contentDescription = "Воспроизводится",
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        
        Text(
            text = trackName,
            modifier = Modifier.weight(1f),
            style = if (isPlaying) {
                MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
            } else {
                MaterialTheme.typography.bodyMedium
            },
            color = if (isPlaying) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            }
        )
        
        IconButton(onClick = onClick) {
            Icon(
                Icons.Default.PlayArrow,
                contentDescription = "Воспроизвести"
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

/**
 * Форматирование времени в миллисекундах
 */
private fun formatTime(milliseconds: Int): String {
    val seconds = milliseconds / 1000
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "%d:%02d".format(minutes, remainingSeconds)
}