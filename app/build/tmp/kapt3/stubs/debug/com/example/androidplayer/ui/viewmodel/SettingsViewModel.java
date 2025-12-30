package com.example.androidplayer.ui.viewmodel;

import android.content.Context;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.lifecycle.HiltViewModel;
import dagger.hilt.android.qualifiers.ApplicationContext;
import kotlinx.coroutines.flow.StateFlow;
import javax.inject.Inject;

/**
 * ViewModel для настроек приложения
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0007\u0018\u0000 \u001b2\u00020\u0001:\u0001\u001bB\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u000f\u001a\u00020\u0010J\u0006\u0010\u0011\u001a\u00020\u0012J\b\u0010\u0013\u001a\u00020\u0010H\u0002J\u0006\u0010\u0014\u001a\u00020\u0010J\u000e\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0016\u001a\u00020\u0012J\u0016\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0019\u001a\u00020\u001aR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u001c"}, d2 = {"Lcom/example/androidplayer/ui/viewmodel/SettingsViewModel;", "Landroidx/lifecycle/ViewModel;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/example/androidplayer/ui/viewmodel/SettingsUiState;", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "clearSaveMessage", "", "getSavedIp", "", "loadSettings", "saveEsp32Ip", "updateEsp32Ip", "ip", "updateSetting", "key", "value", "", "Companion", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel
public final class SettingsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.androidplayer.ui.viewmodel.SettingsUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.example.androidplayer.ui.viewmodel.SettingsUiState> uiState = null;
    @org.jetbrains.annotations.NotNull
    public static final java.lang.String DEFAULT_ESP32_IP = "http://192.168.4.1";
    @org.jetbrains.annotations.NotNull
    public static final com.example.androidplayer.ui.viewmodel.SettingsViewModel.Companion Companion = null;
    
    @javax.inject.Inject
    public SettingsViewModel(@dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.example.androidplayer.ui.viewmodel.SettingsUiState> getUiState() {
        return null;
    }
    
    /**
     * Загрузка настроек
     */
    private final void loadSettings() {
    }
    
    /**
     * Обновление IP адреса
     */
    public final void updateEsp32Ip(@org.jetbrains.annotations.NotNull
    java.lang.String ip) {
    }
    
    /**
     * Сохранение IP адреса
     */
    public final void saveEsp32Ip() {
    }
    
    /**
     * Обновление настроек
     */
    public final void updateSetting(@org.jetbrains.annotations.NotNull
    java.lang.String key, boolean value) {
    }
    
    /**
     * Сброс сообщения о сохранении
     */
    public final void clearSaveMessage() {
    }
    
    /**
     * Получение сохраненного IP
     */
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSavedIp() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lcom/example/androidplayer/ui/viewmodel/SettingsViewModel$Companion;", "", "()V", "DEFAULT_ESP32_IP", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}