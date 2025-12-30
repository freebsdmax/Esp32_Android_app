package com.example.androidplayer.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.androidplayer.data.model.*;
import com.example.androidplayer.data.repository.Esp32PlayerRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import timber.log.Timber;
import javax.inject.Inject;

/**
 * UI состояние мониторинга системы
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0019\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BO\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u001f\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003J\u000b\u0010 \u001a\u0004\u0018\u00010\tH\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\fH\u00c6\u0003J\t\u0010#\u001a\u00020\u000eH\u00c6\u0003JS\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u00c6\u0001J\u0013\u0010%\u001a\u00020\f2\b\u0010&\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\'\u001a\u00020(H\u00d6\u0001J\t\u0010)\u001a\u00020\u0003H\u00d6\u0001R\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0017R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001c\u00a8\u0006*"}, d2 = {"Lcom/example/androidplayer/ui/viewmodel/SystemMonitorUiState;", "", "systemLogs", "", "lastLogsUpdate", "", "systemInfo", "Lcom/example/androidplayer/data/model/SystemInfo;", "canStatistics", "Lcom/example/androidplayer/data/model/CanStatistics;", "logLevel", "verboseLogs", "", "componentLogSettings", "Lcom/example/androidplayer/data/model/ComponentLogSettings;", "(Ljava/lang/String;JLcom/example/androidplayer/data/model/SystemInfo;Lcom/example/androidplayer/data/model/CanStatistics;Ljava/lang/String;ZLcom/example/androidplayer/data/model/ComponentLogSettings;)V", "getCanStatistics", "()Lcom/example/androidplayer/data/model/CanStatistics;", "getComponentLogSettings", "()Lcom/example/androidplayer/data/model/ComponentLogSettings;", "getLastLogsUpdate", "()J", "getLogLevel", "()Ljava/lang/String;", "getSystemInfo", "()Lcom/example/androidplayer/data/model/SystemInfo;", "getSystemLogs", "getVerboseLogs", "()Z", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "other", "hashCode", "", "toString", "app_debug"})
public final class SystemMonitorUiState {
    @org.jetbrains.annotations.NotNull
    private final java.lang.String systemLogs = null;
    private final long lastLogsUpdate = 0L;
    @org.jetbrains.annotations.Nullable
    private final com.example.androidplayer.data.model.SystemInfo systemInfo = null;
    @org.jetbrains.annotations.Nullable
    private final com.example.androidplayer.data.model.CanStatistics canStatistics = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String logLevel = null;
    private final boolean verboseLogs = false;
    @org.jetbrains.annotations.NotNull
    private final com.example.androidplayer.data.model.ComponentLogSettings componentLogSettings = null;
    
    public SystemMonitorUiState(@org.jetbrains.annotations.NotNull
    java.lang.String systemLogs, long lastLogsUpdate, @org.jetbrains.annotations.Nullable
    com.example.androidplayer.data.model.SystemInfo systemInfo, @org.jetbrains.annotations.Nullable
    com.example.androidplayer.data.model.CanStatistics canStatistics, @org.jetbrains.annotations.NotNull
    java.lang.String logLevel, boolean verboseLogs, @org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.model.ComponentLogSettings componentLogSettings) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSystemLogs() {
        return null;
    }
    
    public final long getLastLogsUpdate() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.example.androidplayer.data.model.SystemInfo getSystemInfo() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.example.androidplayer.data.model.CanStatistics getCanStatistics() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getLogLevel() {
        return null;
    }
    
    public final boolean getVerboseLogs() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.data.model.ComponentLogSettings getComponentLogSettings() {
        return null;
    }
    
    public SystemMonitorUiState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component1() {
        return null;
    }
    
    public final long component2() {
        return 0L;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.example.androidplayer.data.model.SystemInfo component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final com.example.androidplayer.data.model.CanStatistics component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component5() {
        return null;
    }
    
    public final boolean component6() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.data.model.ComponentLogSettings component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.ui.viewmodel.SystemMonitorUiState copy(@org.jetbrains.annotations.NotNull
    java.lang.String systemLogs, long lastLogsUpdate, @org.jetbrains.annotations.Nullable
    com.example.androidplayer.data.model.SystemInfo systemInfo, @org.jetbrains.annotations.Nullable
    com.example.androidplayer.data.model.CanStatistics canStatistics, @org.jetbrains.annotations.NotNull
    java.lang.String logLevel, boolean verboseLogs, @org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.model.ComponentLogSettings componentLogSettings) {
        return null;
    }
    
    @java.lang.Override
    public boolean equals(@org.jetbrains.annotations.Nullable
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override
    @org.jetbrains.annotations.NotNull
    public java.lang.String toString() {
        return null;
    }
}