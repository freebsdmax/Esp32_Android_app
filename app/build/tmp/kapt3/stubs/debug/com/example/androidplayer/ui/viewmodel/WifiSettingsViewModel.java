package com.example.androidplayer.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.androidplayer.data.model.*;
import com.example.androidplayer.data.repository.Esp32PlayerRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import timber.log.Timber;
import javax.inject.Inject;

/**
 * ViewModel для настроек Wi-Fi
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\t\n\u0002\u0010\b\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0013\u001a\u00020\u0014J\u0006\u0010\u0015\u001a\u00020\u0014J\u0006\u0010\u0016\u001a\u00020\u0014J\u0006\u0010\u0017\u001a\u00020\u0014J\u000e\u0010\u0018\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u0007J\u000e\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u001b\u001a\u00020\u0007J\u000e\u0010\u001c\u001a\u00020\u00142\u0006\u0010\u001d\u001a\u00020\u001eJ\u000e\u0010\u001f\u001a\u00020\u00142\u0006\u0010\u0019\u001a\u00020\u0007J\u000e\u0010 \u001a\u00020\u00142\u0006\u0010\u001b\u001a\u00020\u0007R\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\t0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000f\u00a8\u0006!"}, d2 = {"Lcom/example/androidplayer/ui/viewmodel/WifiSettingsViewModel;", "Landroidx/lifecycle/ViewModel;", "repository", "Lcom/example/androidplayer/data/repository/Esp32PlayerRepository;", "(Lcom/example/androidplayer/data/repository/Esp32PlayerRepository;)V", "_error", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "_isLoading", "", "_uiState", "Lcom/example/androidplayer/ui/viewmodel/WifiSettingsUiState;", "error", "Lkotlinx/coroutines/flow/StateFlow;", "getError", "()Lkotlinx/coroutines/flow/StateFlow;", "isLoading", "uiState", "getUiState", "clearError", "", "clearSaveMessage", "loadWifiSettings", "saveWifiSettings", "updateApPassword", "password", "updateApSsid", "ssid", "updateMode", "mode", "", "updateStaPassword", "updateStaSsid", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class WifiSettingsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final com.example.androidplayer.data.repository.Esp32PlayerRepository repository = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.androidplayer.ui.viewmodel.WifiSettingsUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.example.androidplayer.ui.viewmodel.WifiSettingsUiState> uiState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isLoading = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isLoading = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _error = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> error = null;
    
    @javax.inject.Inject
    public WifiSettingsViewModel(@org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.repository.Esp32PlayerRepository repository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.example.androidplayer.ui.viewmodel.WifiSettingsUiState> getUiState() {
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
     * Загрузка настроек Wi-Fi
     */
    public final void loadWifiSettings() {
    }
    
    /**
     * Обновление STA SSID
     */
    public final void updateStaSsid(@org.jetbrains.annotations.NotNull
    java.lang.String ssid) {
    }
    
    /**
     * Обновление STA пароля
     */
    public final void updateStaPassword(@org.jetbrains.annotations.NotNull
    java.lang.String password) {
    }
    
    /**
     * Обновление AP SSID
     */
    public final void updateApSsid(@org.jetbrains.annotations.NotNull
    java.lang.String ssid) {
    }
    
    /**
     * Обновление AP пароля
     */
    public final void updateApPassword(@org.jetbrains.annotations.NotNull
    java.lang.String password) {
    }
    
    /**
     * Обновление режима Wi-Fi
     */
    public final void updateMode(int mode) {
    }
    
    /**
     * Сохранение настроек Wi-Fi
     */
    public final void saveWifiSettings() {
    }
    
    /**
     * Очистка сообщения о сохранении
     */
    public final void clearSaveMessage() {
    }
    
    /**
     * Очистка ошибки
     */
    public final void clearError() {
    }
}