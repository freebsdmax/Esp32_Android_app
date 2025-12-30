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
import com.example.androidplayer.ui.viewmodel.SettingsViewModel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\b\u0010\u0000\u001a\u00020\u0001H\u0003\u001a:\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0005\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\u0006\u0010\t\u001a\u00020\u0004H\u0003\u001a:\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\f2\u0018\u0010\u000f\u001a\u0014\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u0010H\u0003\u001a4\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00042\u0006\u0010\u0013\u001a\u00020\u00042\u0006\u0010\u0014\u001a\u00020\f2\u0012\u0010\u0015\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a\u0012\u0010\u0016\u001a\u00020\u00012\b\b\u0002\u0010\u0017\u001a\u00020\u0018H\u0007\u001a\u0010\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u0004H\u0003\u00a8\u0006\u001b"}, d2 = {"AppInfoSection", "", "ConnectionSettingsSection", "ip", "", "onIpChange", "Lkotlin/Function1;", "onSaveIp", "Lkotlin/Function0;", "defaultIp", "GeneralSettingsSection", "autoReconnect", "", "showNotifications", "darkTheme", "onSettingChange", "Lkotlin/Function2;", "SettingSwitch", "title", "description", "checked", "onCheckedChange", "SettingsScreen", "viewModel", "Lcom/example/androidplayer/ui/viewmodel/SettingsViewModel;", "SuccessCard", "message", "app_debug"})
public final class SettingsScreenKt {
    
    /**
     * Экран настроек приложения
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable
    public static final void SettingsScreen(@org.jetbrains.annotations.NotNull
    com.example.androidplayer.ui.viewmodel.SettingsViewModel viewModel) {
    }
    
    /**
     * Секция настроек подключения
     */
    @androidx.compose.runtime.Composable
    private static final void ConnectionSettingsSection(java.lang.String ip, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onIpChange, kotlin.jvm.functions.Function0<kotlin.Unit> onSaveIp, java.lang.String defaultIp) {
    }
    
    /**
     * Секция общих настроек
     */
    @androidx.compose.runtime.Composable
    private static final void GeneralSettingsSection(boolean autoReconnect, boolean showNotifications, boolean darkTheme, kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.Boolean, kotlin.Unit> onSettingChange) {
    }
    
    /**
     * Переключатель настройки
     */
    @androidx.compose.runtime.Composable
    private static final void SettingSwitch(java.lang.String title, java.lang.String description, boolean checked, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onCheckedChange) {
    }
    
    /**
     * Секция информации о приложении
     */
    @androidx.compose.runtime.Composable
    private static final void AppInfoSection() {
    }
    
    /**
     * Карточка успеха
     */
    @androidx.compose.runtime.Composable
    private static final void SuccessCard(java.lang.String message) {
    }
}