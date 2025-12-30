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

/**
 * Уровни логирования
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0017\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007j\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000e\u00a8\u0006\u000f"}, d2 = {"Lcom/example/androidplayer/ui/screens/LogLevel;", "", "value", "", "displayName", "(Ljava/lang/String;ILjava/lang/String;Ljava/lang/String;)V", "getDisplayName", "()Ljava/lang/String;", "getValue", "NONE", "ERROR", "WARN", "INFO", "DEBUG", "VERBOSE", "app_debug"})
public enum LogLevel {
    /*public static final*/ NONE /* = new NONE(null, null) */,
    /*public static final*/ ERROR /* = new ERROR(null, null) */,
    /*public static final*/ WARN /* = new WARN(null, null) */,
    /*public static final*/ INFO /* = new INFO(null, null) */,
    /*public static final*/ DEBUG /* = new DEBUG(null, null) */,
    /*public static final*/ VERBOSE /* = new VERBOSE(null, null) */;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String value = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String displayName = null;
    
    LogLevel(java.lang.String value, java.lang.String displayName) {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getValue() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getDisplayName() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public static kotlin.enums.EnumEntries<com.example.androidplayer.ui.screens.LogLevel> getEntries() {
        return null;
    }
}