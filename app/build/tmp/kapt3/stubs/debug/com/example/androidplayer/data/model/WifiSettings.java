package com.example.androidplayer.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Модель настроек Wi-Fi
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u000f\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0086\b\u0018\u00002\u00020\u0001B-\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\bH\u00c6\u0003J;\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001a\u001a\u00020\bH\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u0003H\u00d6\u0001R\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000bR\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000b\u00a8\u0006\u001c"}, d2 = {"Lcom/example/androidplayer/data/model/WifiSettings;", "", "staSsid", "", "staPassword", "apSsid", "apPassword", "mode", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V", "getApPassword", "()Ljava/lang/String;", "getApSsid", "getMode", "()I", "getStaPassword", "getStaSsid", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "toString", "app_debug"})
public final class WifiSettings {
    @com.google.gson.annotations.SerializedName(value = "sta_ssid")
    @org.jetbrains.annotations.NotNull
    private final java.lang.String staSsid = null;
    @com.google.gson.annotations.SerializedName(value = "sta_password")
    @org.jetbrains.annotations.NotNull
    private final java.lang.String staPassword = null;
    @com.google.gson.annotations.SerializedName(value = "ap_ssid")
    @org.jetbrains.annotations.NotNull
    private final java.lang.String apSsid = null;
    @com.google.gson.annotations.SerializedName(value = "ap_password")
    @org.jetbrains.annotations.NotNull
    private final java.lang.String apPassword = null;
    @com.google.gson.annotations.SerializedName(value = "mode")
    private final int mode = 0;
    
    public WifiSettings(@org.jetbrains.annotations.NotNull
    java.lang.String staSsid, @org.jetbrains.annotations.NotNull
    java.lang.String staPassword, @org.jetbrains.annotations.NotNull
    java.lang.String apSsid, @org.jetbrains.annotations.NotNull
    java.lang.String apPassword, int mode) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getStaSsid() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getStaPassword() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getApSsid() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getApPassword() {
        return null;
    }
    
    public final int getMode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component3() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component4() {
        return null;
    }
    
    public final int component5() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.data.model.WifiSettings copy(@org.jetbrains.annotations.NotNull
    java.lang.String staSsid, @org.jetbrains.annotations.NotNull
    java.lang.String staPassword, @org.jetbrains.annotations.NotNull
    java.lang.String apSsid, @org.jetbrains.annotations.NotNull
    java.lang.String apPassword, int mode) {
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