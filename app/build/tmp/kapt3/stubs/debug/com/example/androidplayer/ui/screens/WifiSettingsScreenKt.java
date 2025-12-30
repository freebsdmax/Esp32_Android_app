package com.example.androidplayer.ui.screens;

import androidx.compose.foundation.layout.*;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.input.KeyboardType;
import androidx.compose.ui.text.input.PasswordVisualTransformation;
import androidx.compose.ui.text.input.VisualTransformation;
import com.example.androidplayer.ui.viewmodel.WifiSettingsUiState;
import com.example.androidplayer.ui.viewmodel.WifiSettingsViewModel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u001a@\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a\u001e\u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\u00032\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u000bH\u0003\u001a\u001e\u0010\f\u001a\u00020\u00012\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u000b2\u0006\u0010\u000e\u001a\u00020\u000fH\u0003\u001a@\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a\u0010\u0010\u0011\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\u0003H\u0003\u001a$\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a\u0012\u0010\u0016\u001a\u00020\u00012\b\b\u0002\u0010\u0017\u001a\u00020\u0018H\u0007\u00a8\u0006\u0019"}, d2 = {"ApSettingsSection", "", "ssid", "", "password", "onSsidChange", "Lkotlin/Function1;", "onPasswordChange", "ErrorCard", "message", "onDismiss", "Lkotlin/Function0;", "SaveButton", "onSave", "isLoading", "", "StaSettingsSection", "SuccessCard", "WiFiModeSection", "mode", "", "onModeChange", "WifiSettingsScreen", "viewModel", "Lcom/example/androidplayer/ui/viewmodel/WifiSettingsViewModel;", "app_debug"})
public final class WifiSettingsScreenKt {
    
    /**
     * Экран настроек Wi-Fi
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable
    public static final void WifiSettingsScreen(@org.jetbrains.annotations.NotNull
    com.example.androidplayer.ui.viewmodel.WifiSettingsViewModel viewModel) {
    }
    
    /**
     * Секция выбора режима Wi-Fi
     */
    @androidx.compose.runtime.Composable
    private static final void WiFiModeSection(int mode, kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onModeChange) {
    }
    
    /**
     * Секция настроек STA режима
     */
    @androidx.compose.runtime.Composable
    private static final void StaSettingsSection(java.lang.String ssid, java.lang.String password, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSsidChange, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onPasswordChange) {
    }
    
    /**
     * Секция настроек AP режима
     */
    @androidx.compose.runtime.Composable
    private static final void ApSettingsSection(java.lang.String ssid, java.lang.String password, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSsidChange, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onPasswordChange) {
    }
    
    /**
     * Кнопка сохранения настроек
     */
    @androidx.compose.runtime.Composable
    private static final void SaveButton(kotlin.jvm.functions.Function0<kotlin.Unit> onSave, boolean isLoading) {
    }
    
    /**
     * Карточка успешного сохранения
     */
    @androidx.compose.runtime.Composable
    private static final void SuccessCard(java.lang.String message) {
    }
    
    /**
     * Карточка ошибки
     */
    @androidx.compose.runtime.Composable
    private static final void ErrorCard(java.lang.String message, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
}