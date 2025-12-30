package com.example.androidplayer.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Модель системной информации
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u001b\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001BM\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\n\u0012\u0006\u0010\f\u001a\u00020\n\u00a2\u0006\u0002\u0010\rJ\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\u001d\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0015J\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0015J\t\u0010\u001f\u001a\u00020\bH\u00c6\u0003J\t\u0010 \u001a\u00020\nH\u00c6\u0003J\t\u0010!\u001a\u00020\nH\u00c6\u0003J\t\u0010\"\u001a\u00020\nH\u00c6\u0003Jb\u0010#\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\nH\u00c6\u0001\u00a2\u0006\u0002\u0010$J\u0013\u0010%\u001a\u00020&2\b\u0010\'\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010(\u001a\u00020\u0003H\u00d6\u0001J\t\u0010)\u001a\u00020\nH\u00d6\u0001R\u0016\u0010\u000b\u001a\u00020\n8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0016\u0010\f\u001a\u00020\n8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0016\u0010\t\u001a\u00020\n8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u001a\u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u0016\u001a\u0004\b\u0014\u0010\u0015R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0013R\u001a\u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004\u00a2\u0006\n\n\u0002\u0010\u0016\u001a\u0004\b\u0018\u0010\u0015R\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006*"}, d2 = {"Lcom/example/androidplayer/data/model/SystemInfo;", "", "freeHeap", "", "totalHeap", "freePsram", "totalPsram", "uptimeMs", "", "firmwareVersion", "", "buildDate", "buildTime", "(IILjava/lang/Integer;Ljava/lang/Integer;JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getBuildDate", "()Ljava/lang/String;", "getBuildTime", "getFirmwareVersion", "getFreeHeap", "()I", "getFreePsram", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getTotalHeap", "getTotalPsram", "getUptimeMs", "()J", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "(IILjava/lang/Integer;Ljava/lang/Integer;JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;)Lcom/example/androidplayer/data/model/SystemInfo;", "equals", "", "other", "hashCode", "toString", "app_debug"})
public final class SystemInfo {
    @com.google.gson.annotations.SerializedName(value = "free_heap")
    private final int freeHeap = 0;
    @com.google.gson.annotations.SerializedName(value = "total_heap")
    private final int totalHeap = 0;
    @com.google.gson.annotations.SerializedName(value = "free_psram")
    @org.jetbrains.annotations.Nullable
    private final java.lang.Integer freePsram = null;
    @com.google.gson.annotations.SerializedName(value = "total_psram")
    @org.jetbrains.annotations.Nullable
    private final java.lang.Integer totalPsram = null;
    @com.google.gson.annotations.SerializedName(value = "uptime_ms")
    private final long uptimeMs = 0L;
    @com.google.gson.annotations.SerializedName(value = "firmware_version")
    @org.jetbrains.annotations.NotNull
    private final java.lang.String firmwareVersion = null;
    @com.google.gson.annotations.SerializedName(value = "build_date")
    @org.jetbrains.annotations.NotNull
    private final java.lang.String buildDate = null;
    @com.google.gson.annotations.SerializedName(value = "build_time")
    @org.jetbrains.annotations.NotNull
    private final java.lang.String buildTime = null;
    
    public SystemInfo(int freeHeap, int totalHeap, @org.jetbrains.annotations.Nullable
    java.lang.Integer freePsram, @org.jetbrains.annotations.Nullable
    java.lang.Integer totalPsram, long uptimeMs, @org.jetbrains.annotations.NotNull
    java.lang.String firmwareVersion, @org.jetbrains.annotations.NotNull
    java.lang.String buildDate, @org.jetbrains.annotations.NotNull
    java.lang.String buildTime) {
        super();
    }
    
    public final int getFreeHeap() {
        return 0;
    }
    
    public final int getTotalHeap() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Integer getFreePsram() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Integer getTotalPsram() {
        return null;
    }
    
    public final long getUptimeMs() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getFirmwareVersion() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getBuildDate() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getBuildTime() {
        return null;
    }
    
    public final int component1() {
        return 0;
    }
    
    public final int component2() {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Integer component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Integer component4() {
        return null;
    }
    
    public final long component5() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component7() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.data.model.SystemInfo copy(int freeHeap, int totalHeap, @org.jetbrains.annotations.Nullable
    java.lang.Integer freePsram, @org.jetbrains.annotations.Nullable
    java.lang.Integer totalPsram, long uptimeMs, @org.jetbrains.annotations.NotNull
    java.lang.String firmwareVersion, @org.jetbrains.annotations.NotNull
    java.lang.String buildDate, @org.jetbrains.annotations.NotNull
    java.lang.String buildTime) {
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