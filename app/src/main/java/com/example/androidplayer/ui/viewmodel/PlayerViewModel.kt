package com.example.androidplayer.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidplayer.data.model.*
import com.example.androidplayer.data.repository.Esp32PlayerRepository
import com.example.androidplayer.data.ble.BlePlayerClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import com.example.androidplayer.ui.i18n.tr
import com.example.androidplayer.service.PlaybackService

/**
 * Главный ViewModel для управления плеером
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: Esp32PlayerRepository,
    val bleClient: BlePlayerClient
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    init {
        // Mirror the device state into the media notification (mini player).
        viewModelScope.launch {
            _uiState.collect { state ->
                val name = state.playlist.getOrNull(state.currentTrack)
                    ?.substringAfterLast('/')
                    ?.substringBeforeLast('.')
                    ?: com.example.androidplayer.ui.i18n.Loc.trackTitle(state.currentTrack + 1)
                PlaybackService.update(
                    name,
                    state.isPaused,
                    state.positionMs.toLong(),
                    state.durationMs.toLong()
                )
            }
        }
    }
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        if (bleClient.hasRememberedDevice()) {
            repository.setBleClient(bleClient)
            bleClient.connectRememberedDevice()
        }
        viewModelScope.launch {
            bleClient.serviceReady.collect { ready ->
                if (ready) {
                    repository.setBleClient(bleClient)
                    loadAfterBleReady()
                } else if (!bleClient.hasRememberedDevice()) {
                    repository.switchTransport(false)
                }
            }
        }
    }
    
    /**
     * Підключити BLE клієнт та перемкнути в BLE режим
     */
    fun connectBleClient() {
        Timber.d("📡 connectBleClient() called")
        Timber.d("📡 bleClient isConnected: ${bleClient.isConnected.value}")
        repository.setBleClient(bleClient)
        Timber.d("✅ BLE client connected, switching to BLE transport")
        Timber.d("📡 Repository now using BLE: ${repository.isBleTransport()}")
    }

    fun loadAfterBleReady() {
        loadPlayerStatus()
        loadPlaylist()
        refreshMedia()
    }
    
    /**
     * Відключити BLE та повернутись в HTTP режим
     */
    fun disconnectBle() {
        repository.disconnectBle()
        Timber.d("🔌 BLE disconnected, switching to HTTP transport")
    }
    
    /**
     * Загрузка статуса плеера
     */
    fun loadPlayerStatus() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            repository.getPlayerStatus().fold(
                onSuccess = { status ->
                    // In external/BT mode the local player is idle: its position and
                    // pause flag are meaningless and make the progress jump to 0:00.
                    val ext = status.mode == "external"
                    _uiState.value = _uiState.value.copy(
                        currentTrack = status.currentTrack,
                        positionMs = if (ext) _uiState.value.positionMs else status.positionMs,
                        durationMs = if (ext) _uiState.value.durationMs else status.durationMs,
                        isPaused = if (ext) _uiState.value.isPaused else status.isPaused,
                        playlistSize = status.playlistSize,
                        mode = status.mode,
                        snifferEnabled = status.snifferEnabled
                    )
                    if (ext) refreshMedia()
                    Timber.d("Player status loaded: $status")
                },
                onFailure = { error ->
                    _error.value = tr("Помилка завантаження статусу: %s").format(error.message ?: error.toString())
                    Timber.e(error, "Failed to load player status")
                }
            )
            
            _isLoading.value = false
        }
    }
    
    /**
     * Загрузка плейлиста
     */
    fun loadPlaylist() {
        viewModelScope.launch {
            repository.getPlaylist().fold(
                onSuccess = { playlist ->
                    _uiState.value = _uiState.value.copy(playlist = playlist)
                    Timber.d("Playlist loaded: ${playlist.size} tracks")
                },
                onFailure = { error ->
                    Timber.e(error, "Failed to load playlist")
                }
            )
        }
    }
    
    /**
     * Воспроизведение трека
     */
    fun playTrack(trackIndex: Int) {
        viewModelScope.launch {
            repository.playTrack(trackIndex).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(
                        currentTrack = trackIndex,
                        isPaused = false
                    )
                    Timber.d("Playing track: $trackIndex")
                },
                onFailure = { error ->
                    _error.value = tr("Помилка відтворення: %s").format(error.message ?: error.toString())
                    Timber.e(error, "Failed to play track")
                }
            )
        }
    }
    
    /**
     * Пауза/воспроизведение
     */
    fun togglePlayback() {
        val currentState = _uiState.value
        viewModelScope.launch {
            val command = if (currentState.isPaused) "play" else "pause"
            
            repository.sendPlayerCommand(command).fold(
                onSuccess = {
                    _uiState.value = currentState.copy(isPaused = !currentState.isPaused)
                    Timber.d("Toggled playback: $command")
                },
                onFailure = { error ->
                    _error.value = tr("Помилка керування відтворенням: %s").format(error.message ?: error.toString())
                    Timber.e(error, "Failed to toggle playback")
                }
            )
        }
    }
    
    /**
     * Следующий трек
     */
    fun nextTrack() {
        viewModelScope.launch {
            repository.nextTrack().fold(
                onSuccess = {
                    val currentTrack = _uiState.value.currentTrack
                    val playlistSize = _uiState.value.playlistSize
                    if (playlistSize > 0) {
                        val nextTrack = (currentTrack + 1) % playlistSize
                        _uiState.value = _uiState.value.copy(currentTrack = nextTrack)
                    }
                    Timber.d("Next track")
                },
                onFailure = { error ->
                    _error.value = tr("Помилка перемикання треку: %s").format(error.message ?: error.toString())
                    Timber.e(error, "Failed to play next track")
                }
            )
        }
    }
    
    /**
     * Предыдущий трек
     */
    fun previousTrack() {
        viewModelScope.launch {
            repository.previousTrack().fold(
                onSuccess = {
                    val currentTrack = _uiState.value.currentTrack
                    val playlistSize = _uiState.value.playlistSize
                    if (playlistSize > 0) {
                        val prevTrack = if (currentTrack > 0) currentTrack - 1 else playlistSize - 1
                        _uiState.value = _uiState.value.copy(currentTrack = prevTrack)
                    }
                    Timber.d("Previous track")
                },
                onFailure = { error ->
                    _error.value = tr("Помилка перемикання треку: %s").format(error.message ?: error.toString())
                    Timber.e(error, "Failed to play previous track")
                }
            )
        }
    }
    
    /**
     * Переключение режима (USB/External I2S)
     */
    fun switchMode() {
        viewModelScope.launch {
            Timber.d("🔄 switchMode() called, useBleTransport=${repository.isBleTransport()}")
            repository.switchMode().fold(
                onSuccess = {
                    val currentMode = _uiState.value.mode
                    val newMode = if (currentMode == "USB") "external" else "USB"
                    _uiState.value = _uiState.value.copy(mode = newMode)
                    Timber.d("Switched mode to: $newMode")
                },
                onFailure = { error ->
                    _error.value = tr("Помилка перемикання режиму: %s").format(error.message ?: error.toString())
                    Timber.e(error, "Failed to switch mode")
                }
            )
        }
    }
    
    /**
     * Установка позиции воспроизведения
     */
    fun setPosition(positionMs: Int) {
        viewModelScope.launch {
            repository.setPosition(positionMs).fold(
                onSuccess = {
                    _uiState.value = _uiState.value.copy(positionMs = positionMs)
                    Timber.d("Position set to: $positionMs ms")
                },
                onFailure = { error ->
                    _error.value = tr("Помилка перемотування: %s").format(error.message ?: error.toString())
                    Timber.e(error, "Failed to set position")
                }
            )
        }
    }
    
    /**
     * Переключение Wi-Fi CAN-Sniffer
     */
    fun toggleSniffer() {
        val currentState = _uiState.value
        viewModelScope.launch {
            repository.toggleSniffer(!currentState.snifferEnabled).fold(
                onSuccess = {
                    _uiState.value = currentState.copy(snifferEnabled = !currentState.snifferEnabled)
                    Timber.d("Sniffer toggled to: ${!currentState.snifferEnabled}")
                },
                onFailure = { error ->
                    _error.value = tr("Помилка керування CAN-Sniffer: %s").format(error.message ?: error.toString())
                    Timber.e(error, "Failed to toggle sniffer")
                }
            )
        }
    }
    
    /**
     * Relative seek (used by the -10 s / +10 s buttons in Bluetooth mode:
     * AVRCP has no absolute seek, the device converts this into FF/REW steps).
     */
    fun seekBy(deltaMs: Int) {
        val state = _uiState.value
        val target = (state.positionMs + deltaMs).coerceAtLeast(0)
        val limit = if (state.durationMs > 0) state.durationMs else target
        setPosition(target.coerceAtMost(limit))
    }

    /**
     * Очистка ошибки
     */
    fun clearError() {
        _error.value = null
    }
    
    /**
     * Обновление позиции в реальном времени
     */
    fun updatePosition(currentPositionMs: Int) {
        _uiState.value = _uiState.value.copy(positionMs = currentPositionMs)
    }

    /**
     * Metadata of the track playing on the phone (external/BT mode).
     * The master polls the slave over UART, so this is a cheap cached read.
     */
    fun refreshMedia() {
        viewModelScope.launch {
            repository.getMedia().onSuccess { media ->
                if (media.valid) {
                    _uiState.value = _uiState.value.copy(
                        mediaTitle = media.title,
                        mediaArtist = media.artist,
                        positionMs = media.posMs,
                        durationMs = if (media.durMs > 0) media.durMs else _uiState.value.durationMs,
                        isPaused = !media.playing
                    )
                } else {
                    _uiState.value = _uiState.value.copy(mediaTitle = "", mediaArtist = "")
                }
            }
        }
    }

    /**
     * Quietly poll player status without toggling loading/error UI (used for live position updates)
     */
    fun pollPlayerStatus() {
        // Якщо BLE ще не активований, не робимо HTTP-запросів (уникаємо 192.168.4.1)
        if (!repository.isBleTransport()) return

        viewModelScope.launch {
            try {
                repository.getPlayerStatus().fold(
                    onSuccess = { status ->
                        // Update UI state but don't set global loading or error
                        val ext = status.mode == "external"
                        _uiState.value = _uiState.value.copy(
                            currentTrack = status.currentTrack,
                            positionMs = if (ext) _uiState.value.positionMs else status.positionMs,
                            durationMs = if (ext) _uiState.value.durationMs else status.durationMs,
                            isPaused = if (ext) _uiState.value.isPaused else status.isPaused,
                            playlistSize = status.playlistSize,
                            mode = status.mode,
                            snifferEnabled = status.snifferEnabled
                        )
                        if (ext) refreshMedia()
                    },
                    onFailure = { e ->
                        Timber.w(e, "Quiet poll failed")
                    }
                )
            } catch (e: Exception) {
                Timber.w(e, "Quiet poll exception")
            }
        }
    }
}

/**
 * UI состояние плеера
 */
data class PlayerUiState(
    val currentTrack: Int = 0,
    val positionMs: Int = 0,
    val durationMs: Int = 0,
    val isPaused: Boolean = true,
    val playlistSize: Int = 0,
    val playlist: List<String> = emptyList(),
    val mode: String = "USB", // "USB" или "external"
    val snifferEnabled: Boolean = false,
    /** Зараз грає на телефоні (Bluetooth): назва / виконавець */
    val mediaTitle: String = "",
    val mediaArtist: String = ""
)