package com.example.androidplayer.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.androidplayer.data.model.*;
import com.example.androidplayer.data.repository.Esp32PlayerRepository;
import com.example.androidplayer.data.ble.BlePlayerClient;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import timber.log.Timber;
import javax.inject.Inject;

/**
 * Главный ViewModel для управления плеером
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\n\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\u0017\u001a\u00020\u0018J\u0006\u0010\u0019\u001a\u00020\u0018J\u0006\u0010\u001a\u001a\u00020\u0018J\u0006\u0010\u001b\u001a\u00020\u0018J\u0006\u0010\u001c\u001a\u00020\u0018J\u0006\u0010\u001d\u001a\u00020\u0018J\u0006\u0010\u001e\u001a\u00020\u0018J\u000e\u0010\u001f\u001a\u00020\u00182\u0006\u0010 \u001a\u00020!J\u0006\u0010\"\u001a\u00020\u0018J\u0006\u0010#\u001a\u00020\u0018J\u000e\u0010$\u001a\u00020\u00182\u0006\u0010%\u001a\u00020!J\u0006\u0010&\u001a\u00020\u0018J\u0006\u0010\'\u001a\u00020\u0018J\u0006\u0010(\u001a\u00020\u0018J\u000e\u0010)\u001a\u00020\u00182\u0006\u0010*\u001a\u00020!R\u0016\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0019\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\r0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0013\u00a8\u0006+"}, d2 = {"Lcom/example/androidplayer/ui/viewmodel/PlayerViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/example/androidplayer/data/repository/Esp32PlayerRepository;", "bleClient", "Lcom/example/androidplayer/data/ble/BlePlayerClient;", "(Lcom/example/androidplayer/data/repository/Esp32PlayerRepository;Lcom/example/androidplayer/data/ble/BlePlayerClient;)V", "_error", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_isLoading", "", "_uiState", "Lcom/example/androidplayer/ui/viewmodel/PlayerUiState;", "getBleClient", "()Lcom/example/androidplayer/data/ble/BlePlayerClient;", "error", "Lkotlinx/coroutines/flow/StateFlow;", "getError", "()Lkotlinx/coroutines/flow/StateFlow;", "isLoading", "uiState", "getUiState", "clearError", "", "connectBleClient", "disconnectBle", "loadAfterBleReady", "loadPlayerStatus", "loadPlaylist", "nextTrack", "playTrack", "trackIndex", "", "pollPlayerStatus", "previousTrack", "setPosition", "positionMs", "switchMode", "togglePlayback", "toggleSniffer", "updatePosition", "currentPositionMs", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class PlayerViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.example.androidplayer.data.repository.Esp32PlayerRepository repository = null;
    @org.jetbrains.annotations.NotNull
    private final com.example.androidplayer.data.ble.BlePlayerClient bleClient = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.androidplayer.ui.viewmodel.PlayerUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.example.androidplayer.ui.viewmodel.PlayerUiState> uiState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _error = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> error = null;
    
    @javax.inject.Inject
    public PlayerViewModel(@org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.repository.Esp32PlayerRepository repository, @org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.ble.BlePlayerClient bleClient) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.data.ble.BlePlayerClient getBleClient() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.example.androidplayer.ui.viewmodel.PlayerUiState> getUiState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getError() {
        return null;
    }
    
    /**
     * Підключити BLE клієнт та перемкнути в BLE режим
     */
    public final void connectBleClient() {
    }
    
    public final void loadAfterBleReady() {
    }
    
    /**
     * Відключити BLE та повернутись в HTTP режим
     */
    public final void disconnectBle() {
    }
    
    /**
     * Загрузка статуса плеера
     */
    public final void loadPlayerStatus() {
    }
    
    /**
     * Загрузка плейлиста
     */
    public final void loadPlaylist() {
    }
    
    /**
     * Воспроизведение трека
     */
    public final void playTrack(int trackIndex) {
    }
    
    /**
     * Пауза/воспроизведение
     */
    public final void togglePlayback() {
    }
    
    /**
     * Следующий трек
     */
    public final void nextTrack() {
    }
    
    /**
     * Предыдущий трек
     */
    public final void previousTrack() {
    }
    
    /**
     * Переключение режима (USB/External I2S)
     */
    public final void switchMode() {
    }
    
    /**
     * Установка позиции воспроизведения
     */
    public final void setPosition(int positionMs) {
    }
    
    /**
     * Переключение Wi-Fi CAN-Sniffer
     */
    public final void toggleSniffer() {
    }
    
    /**
     * Очистка ошибки
     */
    public final void clearError() {
    }
    
    /**
     * Обновление позиции в реальном времени
     */
    public final void updatePosition(int currentPositionMs) {
    }
    
    /**
     * Quietly poll player status without toggling loading/error UI (used for live position updates)
     */
    public final void pollPlayerStatus() {
    }
}