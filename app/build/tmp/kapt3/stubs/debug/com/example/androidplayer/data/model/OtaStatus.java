package com.example.androidplayer.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Модель статуса OTA
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b6\b\u0086\b\u0018\u00002\u00020\u0001B\u00af\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u0012\b\b\u0002\u0010\b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n\u0012\b\b\u0002\u0010\f\u001a\u00020\n\u0012\b\b\u0002\u0010\r\u001a\u00020\n\u0012\b\b\u0002\u0010\u000e\u001a\u00020\n\u0012\b\b\u0002\u0010\u000f\u001a\u00020\n\u0012\b\b\u0002\u0010\u0010\u001a\u00020\n\u0012\b\b\u0002\u0010\u0011\u001a\u00020\n\u0012\b\b\u0002\u0010\u0012\u001a\u00020\n\u0012\b\b\u0002\u0010\u0013\u001a\u00020\n\u0012\b\b\u0002\u0010\u0014\u001a\u00020\n\u0012\b\b\u0002\u0010\u0015\u001a\u00020\n\u00a2\u0006\u0002\u0010\u0016J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\nH\u00c6\u0003J\t\u0010,\u001a\u00020\nH\u00c6\u0003J\t\u0010-\u001a\u00020\nH\u00c6\u0003J\t\u0010.\u001a\u00020\nH\u00c6\u0003J\t\u0010/\u001a\u00020\nH\u00c6\u0003J\t\u00100\u001a\u00020\nH\u00c6\u0003J\t\u00101\u001a\u00020\nH\u00c6\u0003J\t\u00102\u001a\u00020\nH\u00c6\u0003J\t\u00103\u001a\u00020\u0003H\u00c6\u0003J\t\u00104\u001a\u00020\u0006H\u00c6\u0003J\t\u00105\u001a\u00020\u0006H\u00c6\u0003J\t\u00106\u001a\u00020\u0006H\u00c6\u0003J\t\u00107\u001a\u00020\nH\u00c6\u0003J\t\u00108\u001a\u00020\nH\u00c6\u0003J\t\u00109\u001a\u00020\nH\u00c6\u0003J\t\u0010:\u001a\u00020\nH\u00c6\u0003J\u00b3\u0001\u0010;\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\f\u001a\u00020\n2\b\b\u0002\u0010\r\u001a\u00020\n2\b\b\u0002\u0010\u000e\u001a\u00020\n2\b\b\u0002\u0010\u000f\u001a\u00020\n2\b\b\u0002\u0010\u0010\u001a\u00020\n2\b\b\u0002\u0010\u0011\u001a\u00020\n2\b\b\u0002\u0010\u0012\u001a\u00020\n2\b\b\u0002\u0010\u0013\u001a\u00020\n2\b\b\u0002\u0010\u0014\u001a\u00020\n2\b\b\u0002\u0010\u0015\u001a\u00020\nH\u00c6\u0001J\u0013\u0010<\u001a\u00020\u00032\b\u0010=\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010>\u001a\u00020\u0006H\u00d6\u0001J\t\u0010?\u001a\u00020\nH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0018R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u000b\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001cR\u0011\u0010\u0012\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001cR\u0011\u0010\u0013\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001cR\u0011\u0010\u0010\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001cR\u0011\u0010\u0011\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001cR\u0011\u0010\u0014\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001cR\u0011\u0010\u0015\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001cR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u001aR\u0011\u0010\u000e\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001cR\u0011\u0010\u000f\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u001cR\u0011\u0010\f\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\u001cR\u0011\u0010\r\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u001cR\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u001a\u00a8\u0006@"}, d2 = {"Lcom/example/androidplayer/data/model/OtaStatus;", "", "active", "", "is_uart_slave", "total", "", "written", "block", "player_build_date", "", "player_build_time", "web_build_date", "web_build_time", "uart_build_date", "uart_build_time", "slave_main_build_date", "slave_main_build_time", "slave_crc_build_date", "slave_crc_build_time", "slave_uart_build_date", "slave_uart_build_time", "(ZZIIILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getActive", "()Z", "getBlock", "()I", "getPlayer_build_date", "()Ljava/lang/String;", "getPlayer_build_time", "getSlave_crc_build_date", "getSlave_crc_build_time", "getSlave_main_build_date", "getSlave_main_build_time", "getSlave_uart_build_date", "getSlave_uart_build_time", "getTotal", "getUart_build_date", "getUart_build_time", "getWeb_build_date", "getWeb_build_time", "getWritten", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class OtaStatus {
    private final boolean active = false;
    private final boolean is_uart_slave = false;
    private final int total = 0;
    private final int written = 0;
    private final int block = 0;
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
    
    public OtaStatus(boolean active, boolean is_uart_slave, int total, int written, int block, @org.jetbrains.annotations.NotNull
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
    
    public final boolean getActive() {
        return false;
    }
    
    public final boolean is_uart_slave() {
        return false;
    }
    
    public final int getTotal() {
        return 0;
    }
    
    public final int getWritten() {
        return 0;
    }
    
    public final int getBlock() {
        return 0;
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
    
    public OtaStatus() {
        super();
    }
    
    public final boolean component1() {
        return false;
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
    public final java.lang.String component13() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component14() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component15() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component16() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component17() {
        return null;
    }
    
    public final boolean component2() {
        return false;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final int component4() {
        return 0;
    }
    
    public final int component5() {
        return 0;
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
    public final com.example.androidplayer.data.model.OtaStatus copy(boolean active, boolean is_uart_slave, int total, int written, int block, @org.jetbrains.annotations.NotNull
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