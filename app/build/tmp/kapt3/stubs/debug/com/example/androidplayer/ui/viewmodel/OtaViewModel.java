package com.example.androidplayer.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.androidplayer.data.model.*;
import com.example.androidplayer.data.repository.Esp32PlayerRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.ExperimentalCoroutinesApi;
import timber.log.Timber;
import java.io.File;
import javax.inject.Inject;

/**
 * ViewModel для OTA обновлений
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\u0012\u001a\u00020\u0011J\u0006\u0010\u0013\u001a\u00020\u0011J\u0006\u0010\u0014\u001a\u00020\u0011J\u0006\u0010\u0015\u001a\u00020\u0011J\u0006\u0010\u0016\u001a\u00020\u0011J\b\u0010\u0017\u001a\u00020\u0011H\u0002J\u000e\u0010\u0018\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u001aJ\u000e\u0010\u001b\u001a\u00020\u00112\u0006\u0010\u0019\u001a\u00020\u001aR\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\r\u00a8\u0006\u001c"}, d2 = {"Lcom/example/androidplayer/ui/viewmodel/OtaViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/example/androidplayer/data/repository/Esp32PlayerRepository;", "(Lcom/example/androidplayer/data/repository/Esp32PlayerRepository;)V", "_error", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_uiState", "Lcom/example/androidplayer/ui/viewmodel/OtaUiState;", "error", "Lkotlinx/coroutines/flow/StateFlow;", "getError", "()Lkotlinx/coroutines/flow/StateFlow;", "uiState", "getUiState", "clearError", "", "clearRestartMessage", "clearUploadMessage", "loadOtaStatus", "loadVersionInfo", "restartDevice", "startProgressTracking", "uploadMasterFirmware", "firmwareFile", "Ljava/io/File;", "uploadSlaveFirmware", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class OtaViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.example.androidplayer.data.repository.Esp32PlayerRepository repository = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.androidplayer.ui.viewmodel.OtaUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.example.androidplayer.ui.viewmodel.OtaUiState> uiState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _error = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> error = null;
    
    @javax.inject.Inject
    public OtaViewModel(@org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.repository.Esp32PlayerRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.example.androidplayer.ui.viewmodel.OtaUiState> getUiState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getError() {
        return null;
    }
    
    /**
     * Загрузка статуса OTA
     */
    public final void loadOtaStatus() {
    }
    
    /**
     * Загрузка информации о версиях
     */
    public final void loadVersionInfo() {
    }
    
    /**
     * Загрузка прошивки для Master ESP32
     */
    public final void uploadMasterFirmware(@org.jetbrains.annotations.NotNull
    java.io.File firmwareFile) {
    }
    
    /**
     * Загрузка прошивки для Slave ESP32
     */
    public final void uploadSlaveFirmware(@org.jetbrains.annotations.NotNull
    java.io.File firmwareFile) {
    }
    
    /**
     * Отслеживание прогресса OTA
     */
    private final void startProgressTracking() {
    }
    
    /**
     * Перезагрузка устройства
     */
    public final void restartDevice() {
    }
    
    /**
     * Очистка ошибки
     */
    public final void clearError() {
    }
    
    /**
     * Очистка сообщения о перезагрузке
     */
    public final void clearRestartMessage() {
    }
    
    /**
     * Очистка сообщения о загрузке
     */
    public final void clearUploadMessage() {
    }
}