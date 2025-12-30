package com.example.androidplayer.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Модель настроек приложения
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0015\b\u0086\b\u0018\u00002\u00020\u0001B7\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\bH\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0006H\u00c6\u0003J;\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0019\u001a\u00020\u00032\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001b\u001a\u00020\bH\u00d6\u0001J\t\u0010\u001c\u001a\u00020\u0006H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010\u00a8\u0006\u001d"}, d2 = {"Lcom/example/androidplayer/data/model/AppSettings;", "", "autoReconnect", "", "showNotifications", "themeMode", "", "defaultVolume", "", "notificationPriority", "(ZZLjava/lang/String;ILjava/lang/String;)V", "getAutoReconnect", "()Z", "getDefaultVolume", "()I", "getNotificationPriority", "()Ljava/lang/String;", "getShowNotifications", "getThemeMode", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class AppSettings {
    private final boolean autoReconnect = false;
    private final boolean showNotifications = false;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String themeMode = null;
    private final int defaultVolume = 0;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String notificationPriority = null;
    
    public AppSettings(boolean autoReconnect, boolean showNotifications, @org.jetbrains.annotations.NotNull
    java.lang.String themeMode, int defaultVolume, @org.jetbrains.annotations.NotNull
    java.lang.String notificationPriority) {
        super();
    }
    
    public final boolean getAutoReconnect() {
        return false;
    }
    
    public final boolean getShowNotifications() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getThemeMode() {
        return null;
    }
    
    public final int getDefaultVolume() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getNotificationPriority() {
        return null;
    }
    
    public AppSettings() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    public final boolean component2() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component3() {
        return null;
    }
    
    public final int component4() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.data.model.AppSettings copy(boolean autoReconnect, boolean showNotifications, @org.jetbrains.annotations.NotNull
    java.lang.String themeMode, int defaultVolume, @org.jetbrains.annotations.NotNull
    java.lang.String notificationPriority) {
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