package com.example.androidplayer.data.network;

import com.example.androidplayer.data.model.*;
import retrofit2.http.*;

/**
 * API интерфейс для взаимодействия с ESP32 плеером
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0012\n\u0002\u0010\u0012\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\u0011\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u0005\u001a\u00020\u0006H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u0007\u001a\u00020\bH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\t\u001a\u00020\nH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u000b\u001a\u00020\fH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\b0\u000eH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u000f\u001a\u00020\u0010H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u0011\u001a\u00020\u0012H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u0013\u001a\u00020\bH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u0014\u001a\u00020\bH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u0015\u001a\u00020\u0016H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u0017\u001a\u00020\u0018H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J\u0011\u0010\u0019\u001a\u00020\bH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u0004J3\u0010\u001a\u001a\u00020\b2\b\b\u0001\u0010\u001b\u001a\u00020\b2\n\b\u0003\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\n\b\u0003\u0010\u001e\u001a\u0004\u0018\u00010\u001dH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\u001fJ\u001b\u0010 \u001a\u00020\b2\b\b\u0001\u0010!\u001a\u00020\u0006H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\"J\u001b\u0010#\u001a\u00020\b2\b\b\u0001\u0010$\u001a\u00020\bH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010%J\u001b\u0010&\u001a\u00020\b2\b\b\u0001\u0010!\u001a\u00020\u0010H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010\'J\u001b\u0010(\u001a\u00020\b2\b\b\u0001\u0010)\u001a\u00020\bH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010%J\u001b\u0010*\u001a\u00020\b2\b\b\u0001\u0010!\u001a\u00020\u0018H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010+J%\u0010,\u001a\u00020\b2\b\b\u0003\u0010\u001b\u001a\u00020\b2\b\b\u0001\u0010)\u001a\u00020\u001dH\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010-J\u001b\u0010.\u001a\u00020\b2\b\b\u0001\u0010/\u001a\u000200H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u00101J\u001b\u00102\u001a\u00020\b2\b\b\u0001\u0010/\u001a\u000200H\u00a7@\u00f8\u0001\u0000\u00a2\u0006\u0002\u00101\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u00063"}, d2 = {"Lcom/example/androidplayer/data/network/Esp32PlayerApi;", "", "getCanStatistics", "Lcom/example/androidplayer/data/model/CanStatistics;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getComponentLogs", "Lcom/example/androidplayer/data/model/ComponentLogSettings;", "getLogLevel", "", "getOtaStatus", "Lcom/example/androidplayer/data/model/OtaStatus;", "getPlayerStatus", "Lcom/example/androidplayer/data/model/PlayerStatus;", "getPlaylist", "", "getSleepSettings", "Lcom/example/androidplayer/data/model/SleepSettings;", "getSystemInfo", "Lcom/example/androidplayer/data/model/SystemInfo;", "getSystemLogs", "getVerboseLogs", "getVersionInfo", "Lcom/example/androidplayer/data/model/VersionInfo;", "getWifiSettings", "Lcom/example/androidplayer/data/model/WifiSettings;", "restartDevice", "sendPlayerCommand", "command", "track", "", "position", "(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setComponentLogs", "settings", "(Lcom/example/androidplayer/data/model/ComponentLogSettings;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setLogLevel", "level", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setSleepSettings", "(Lcom/example/androidplayer/data/model/SleepSettings;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setVerboseLogs", "enabled", "setWifiSettings", "(Lcom/example/androidplayer/data/model/WifiSettings;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "toggleSniffer", "(Ljava/lang/String;ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadMasterFirmware", "firmware", "", "([BLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadSlaveFirmware", "app_debug"})
public abstract interface Esp32PlayerApi {
    
    @retrofit2.http.GET(value = "/player_status")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getPlayerStatus(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.PlayerStatus> $completion);
    
    @retrofit2.http.GET(value = "/playlist")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getPlaylist(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion);
    
    @retrofit2.http.GET(value = "/player_cmd")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object sendPlayerCommand(@retrofit2.http.Query(value = "cmd")
    @org.jetbrains.annotations.NotNull
    java.lang.String command, @retrofit2.http.Query(value = "track")
    @org.jetbrains.annotations.Nullable
    java.lang.Integer track, @retrofit2.http.Query(value = "pos")
    @org.jetbrains.annotations.Nullable
    java.lang.Integer position, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.GET(value = "/wifi_settings")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getWifiSettings(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.WifiSettings> $completion);
    
    @retrofit2.http.POST(value = "/wifi_settings")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object setWifiSettings(@retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.model.WifiSettings settings, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.GET(value = "/player_cmd")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object toggleSniffer(@retrofit2.http.Query(value = "cmd")
    @org.jetbrains.annotations.NotNull
    java.lang.String command, @retrofit2.http.Query(value = "track")
    int enabled, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.GET(value = "/sleep_settings")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getSleepSettings(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.SleepSettings> $completion);
    
    @retrofit2.http.POST(value = "/sleep_settings")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object setSleepSettings(@retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.model.SleepSettings settings, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.GET(value = "/log_level")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getLogLevel(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.POST(value = "/log_level")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object setLogLevel(@retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    java.lang.String level, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.GET(value = "/verbose_logs")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getVerboseLogs(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.POST(value = "/verbose_logs")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object setVerboseLogs(@retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    java.lang.String enabled, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.GET(value = "/component_logs")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getComponentLogs(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.ComponentLogSettings> $completion);
    
    @retrofit2.http.POST(value = "/component_logs")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object setComponentLogs(@retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.model.ComponentLogSettings settings, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.GET(value = "/ota_status")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getOtaStatus(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.OtaStatus> $completion);
    
    @retrofit2.http.POST(value = "/upload")
    @retrofit2.http.Headers(value = {"Content-Type: application/octet-stream"})
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object uploadMasterFirmware(@retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    byte[] firmware, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.POST(value = "/upload_slave")
    @retrofit2.http.Headers(value = {"Content-Type: application/octet-stream"})
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object uploadSlaveFirmware(@retrofit2.http.Body
    @org.jetbrains.annotations.NotNull
    byte[] firmware, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.GET(value = "/logs")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getSystemLogs(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.GET(value = "/version")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getVersionInfo(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.VersionInfo> $completion);
    
    @retrofit2.http.POST(value = "/restart")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object restartDevice(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion);
    
    @retrofit2.http.GET(value = "/can_statistics")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getCanStatistics(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.CanStatistics> $completion);
    
    @retrofit2.http.GET(value = "/system_info")
    @org.jetbrains.annotations.Nullable
    public abstract java.lang.Object getSystemInfo(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.SystemInfo> $completion);
    
    /**
     * API интерфейс для взаимодействия с ESP32 плеером
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}