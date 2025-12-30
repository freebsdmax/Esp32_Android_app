package com.example.androidplayer.ui.screens;

import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextAlign;
import com.example.androidplayer.ui.viewmodel.SystemMonitorUiState;
import com.example.androidplayer.ui.viewmodel.SystemMonitorViewModel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000D\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a \u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0003\u001a&\u0010\u0007\u001a\u00020\u00012\u0006\u0010\b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\n2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\u001e\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u00062\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a>\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u001e\u0010\u0012\u001a\u001a\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\n\u0012\u0004\u0012\u00020\u0014\u0012\u0004\u0012\u00020\u00010\u00132\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a \u0010\u0016\u001a\u00020\u00012\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0003\u001a,\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u001b\u001a\u00020\u00062\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\u0012\u0010\u001e\u001a\u00020\u00012\b\b\u0002\u0010\u001f\u001a\u00020 H\u0007\u001a\u0010\u0010!\u001a\u00020\u00062\u0006\u0010\"\u001a\u00020\u0006H\u0002\u00a8\u0006#"}, d2 = {"CanStatisticsSection", "", "canStatistics", "Lcom/example/androidplayer/data/model/CanStatistics;", "onFormatStats", "Lkotlin/Function0;", "", "ComponentLogItem", "name", "enabled", "", "onToggle", "ErrorCard", "message", "onDismiss", "LogSettingsSection", "uiState", "Lcom/example/androidplayer/ui/viewmodel/SystemMonitorUiState;", "onUpdateSettings", "Lkotlin/Function3;", "Lcom/example/androidplayer/data/model/ComponentLogSettings;", "onLoadCurrentSettings", "SystemInfoSection", "systemInfo", "Lcom/example/androidplayer/data/model/SystemInfo;", "onFormatInfo", "SystemLogsSection", "logs", "onClearLogs", "onRefreshLogs", "SystemMonitorScreen", "viewModel", "Lcom/example/androidplayer/ui/viewmodel/SystemMonitorViewModel;", "getLogLevelName", "level", "app_debug"})
public final class SystemMonitorScreenKt {
    
    /**
     * Экран мониторинга системы
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable
    public static final void SystemMonitorScreen(@org.jetbrains.annotations.NotNull
    com.example.androidplayer.ui.viewmodel.SystemMonitorViewModel viewModel) {
    }
    
    /**
     * Секция системной информации
     */
    @androidx.compose.runtime.Composable
    private static final void SystemInfoSection(com.example.androidplayer.data.model.SystemInfo systemInfo, kotlin.jvm.functions.Function0<java.lang.String> onFormatInfo) {
    }
    
    /**
     * Секция статистики CAN
     */
    @androidx.compose.runtime.Composable
    private static final void CanStatisticsSection(com.example.androidplayer.data.model.CanStatistics canStatistics, kotlin.jvm.functions.Function0<java.lang.String> onFormatStats) {
    }
    
    /**
     * Секция настроек логирования
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable
    private static final void LogSettingsSection(com.example.androidplayer.ui.viewmodel.SystemMonitorUiState uiState, kotlin.jvm.functions.Function3<? super java.lang.String, ? super java.lang.Boolean, ? super com.example.androidplayer.data.model.ComponentLogSettings, kotlin.Unit> onUpdateSettings, kotlin.jvm.functions.Function0<kotlin.Unit> onLoadCurrentSettings) {
    }
    
    /**
     * Элемент настройки логов компонента
     */
    @androidx.compose.runtime.Composable
    private static final void ComponentLogItem(java.lang.String name, boolean enabled, kotlin.jvm.functions.Function0<kotlin.Unit> onToggle) {
    }
    
    /**
     * Секция системных логов
     */
    @androidx.compose.runtime.Composable
    private static final void SystemLogsSection(java.lang.String logs, kotlin.jvm.functions.Function0<kotlin.Unit> onClearLogs, kotlin.jvm.functions.Function0<kotlin.Unit> onRefreshLogs) {
    }
    
    /**
     * Карточка ошибки
     */
    @androidx.compose.runtime.Composable
    private static final void ErrorCard(java.lang.String message, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    /**
     * Получение названия уровня логирования
     */
    private static final java.lang.String getLogLevelName(java.lang.String level) {
        return null;
    }
}