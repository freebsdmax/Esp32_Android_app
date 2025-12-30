package com.example.androidplayer.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Модель информации о версиях
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\'\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001Be\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u0003\u0012\u0006\u0010\u000b\u001a\u00020\u0003\u0012\u0006\u0010\f\u001a\u00020\u0003\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\u0006\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\t\u0010$\u001a\u00020\u0003H\u00c6\u0003J\t\u0010%\u001a\u00020\u0003H\u00c6\u0003J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\'\u001a\u00020\u0003H\u00c6\u0003J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\u0081\u0001\u0010)\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010*\u001a\u00020+2\b\u0010,\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010-\u001a\u00020.H\u00d6\u0001J\t\u0010/\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0011R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0011R\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0011R\u0011\u0010\r\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0011R\u0011\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0011R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0011R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0011R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0011\u00a8\u00060"}, d2 = {"Lcom/example/androidplayer/data/model/VersionInfo;", "", "player_build_date", "", "player_build_time", "web_build_date", "web_build_time", "uart_build_date", "uart_build_time", "slave_main_build_date", "slave_main_build_time", "slave_crc_build_date", "slave_crc_build_time", "slave_uart_build_date", "slave_uart_build_time", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getPlayer_build_date", "()Ljava/lang/String;", "getPlayer_build_time", "getSlave_crc_build_date", "getSlave_crc_build_time", "getSlave_main_build_date", "getSlave_main_build_time", "getSlave_uart_build_date", "getSlave_uart_build_time", "getUart_build_date", "getUart_build_time", "getWeb_build_date", "getWeb_build_time", "component1", "component10", "component11", "component12", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "", "toString", "app_debug"})
public final class VersionInfo {
    @org.jetbrains.annotations.NotNull
    private final java.lang.String player_build_date = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String player_build_time = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String web_build_date = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String web_build_time = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String uart_build_date = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String uart_build_time = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String slave_main_build_date = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String slave_main_build_time = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String slave_crc_build_date = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String slave_crc_build_time = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String slave_uart_build_date = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String slave_uart_build_time = null;
    
    public VersionInfo(@org.jetbrains.annotations.NotNull
    java.lang.String player_build_date, @org.jetbrains.annotations.NotNull
    java.lang.String player_build_time, @org.jetbrains.annotations.NotNull
    java.lang.String web_build_date, @org.jetbrains.annotations.NotNull
    java.lang.String web_build_time, @org.jetbrains.annotations.NotNull
    java.lang.String uart_build_date, @org.jetbrains.annotations.NotNull
    java.lang.String uart_build_time, @org.jetbrains.annotations.NotNull
    java.lang.String slave_main_build_date, @org.jetbrains.annotations.NotNull
    java.lang.String slave_main_build_time, @org.jetbrains.annotations.NotNull
    java.lang.String slave_crc_build_date, @org.jetbrains.annotations.NotNull
    java.lang.String slave_crc_build_time, @org.jetbrains.annotations.NotNull
    java.lang.String slave_uart_build_date, @org.jetbrains.annotations.NotNull
    java.lang.String slave_uart_build_time) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getPlayer_build_date() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getPlayer_build_time() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getWeb_build_date() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getWeb_build_time() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getUart_build_date() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getUart_build_time() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSlave_main_build_date() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSlave_main_build_time() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSlave_crc_build_date() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSlave_crc_build_time() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSlave_uart_build_date() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSlave_uart_build_time() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component11() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component12() {
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
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component5() {
        return null;
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
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.data.model.VersionInfo copy(@org.jetbrains.annotations.NotNull
    java.lang.String player_build_date, @org.jetbrains.annotations.NotNull
    java.lang.String player_build_time, @org.jetbrains.annotations.NotNull
    java.lang.String web_build_date, @org.jetbrains.annotations.NotNull
    java.lang.String web_build_time, @org.jetbrains.annotations.NotNull
    java.lang.String uart_build_date, @org.jetbrains.annotations.NotNull
    java.lang.String uart_build_time, @org.jetbrains.annotations.NotNull
    java.lang.String slave_main_build_date, @org.jetbrains.annotations.NotNull
    java.lang.String slave_main_build_time, @org.jetbrains.annotations.NotNull
    java.lang.String slave_crc_build_date, @org.jetbrains.annotations.NotNull
    java.lang.String slave_crc_build_time, @org.jetbrains.annotations.NotNull
    java.lang.String slave_uart_build_date, @org.jetbrains.annotations.NotNull
    java.lang.String slave_uart_build_time) {
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