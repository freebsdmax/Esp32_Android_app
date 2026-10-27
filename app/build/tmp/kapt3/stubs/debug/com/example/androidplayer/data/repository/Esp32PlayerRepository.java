package com.example.androidplayer.data.repository;

import com.example.androidplayer.data.model.*;
import com.example.androidplayer.data.network.Esp32PlayerApi;
import com.example.androidplayer.data.ble.BlePlayerClient;
import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Репозиторий для работы с ESP32 плеером
 * Абстрагирует источники данных (HTTP/BLE) и предоставляет единый интерфейс
 */
@javax.inject.Singleton
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0094\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b)\n\u0002\u0010\u0012\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\f\u001a\u00020\rJ\u0006\u0010\u000e\u001a\u00020\u000fJ\"\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b\u0013\u0010\u0014J\"\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00160\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b\u0017\u0010\u0014J\"\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b\u0019\u0010\u0014J\"\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b\u001c\u0010\u0014J\"\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b\u001f\u0010\u0014J(\u0010 \u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000f0!0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b\"\u0010\u0014J\"\u0010#\u001a\b\u0012\u0004\u0012\u00020$0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b%\u0010\u0014J\"\u0010&\u001a\b\u0012\u0004\u0012\u00020\'0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b(\u0010\u0014J\"\u0010)\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b*\u0010\u0014J\"\u0010+\u001a\b\u0012\u0004\u0012\u00020\b0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b,\u0010\u0014J\"\u0010-\u001a\b\u0012\u0004\u0012\u00020.0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b/\u0010\u0014J\"\u00100\u001a\b\u0012\u0004\u0012\u0002010\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b2\u0010\u0014J\u0006\u00103\u001a\u00020\bJ\"\u00104\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b5\u0010\u0014J\"\u00106\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b7\u0010\u0014J*\u00108\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00112\u0006\u00109\u001a\u00020:H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b;\u0010<J\"\u0010=\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b>\u0010\u0014J\"\u0010?\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b@\u0010\u0014J\"\u0010A\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\bB\u0010\u0014JF\u0010C\u001a\b\u0012\u0004\u0012\u0002HD0\u0011\"\u0004\b\u0000\u0010D2\u001c\u0010E\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u0002HD0G\u0012\u0006\u0012\u0004\u0018\u00010\u00010FH\u0082@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\bH\u0010IJB\u0010J\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00112\u0006\u0010K\u001a\u00020\u000f2\n\b\u0002\u0010L\u001a\u0004\u0018\u00010:2\n\b\u0002\u0010M\u001a\u0004\u0018\u00010:H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\bN\u0010OJ\u000e\u0010P\u001a\u00020\r2\u0006\u0010Q\u001a\u00020\u0006J*\u0010R\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00112\u0006\u0010S\u001a\u00020\u0016H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\bT\u0010UJ*\u0010V\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00112\u0006\u0010W\u001a\u00020\u000fH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\bX\u0010YJ*\u0010Z\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00112\u0006\u0010[\u001a\u00020:H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b\\\u0010<J*\u0010]\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00112\u0006\u0010S\u001a\u00020$H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\b^\u0010_J*\u0010`\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00112\u0006\u0010a\u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\bb\u0010cJ*\u0010d\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00112\u0006\u0010S\u001a\u000201H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\be\u0010fJ\"\u0010g\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\bh\u0010\u0014J\u000e\u0010i\u001a\u00020\r2\u0006\u0010j\u001a\u00020\bJ\"\u0010k\u001a\b\u0012\u0004\u0012\u00020\b0\u0011H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\bl\u0010\u0014J*\u0010m\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00112\u0006\u0010a\u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\bn\u0010cJ*\u0010o\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00112\u0006\u0010p\u001a\u00020qH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\br\u0010sJ*\u0010t\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00112\u0006\u0010p\u001a\u00020qH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00f8\u0001\u0002\u00f8\u0001\u0002\u00a2\u0006\u0004\bu\u0010sR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001e\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\b@BX\u0086\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b\u0082\u0002\u000f\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\n\u0002\b\u0019\u00a8\u0006v"}, d2 = {"Lcom/example/androidplayer/data/repository/Esp32PlayerRepository;", "", "api", "Lcom/example/androidplayer/data/network/Esp32PlayerApi;", "(Lcom/example/androidplayer/data/network/Esp32PlayerApi;)V", "bleClient", "Lcom/example/androidplayer/data/ble/BlePlayerClient;", "<set-?>", "", "useBleTransport", "getUseBleTransport", "()Z", "disconnectBle", "", "getActiveTransport", "", "getCanStatistics", "Lkotlin/Result;", "Lcom/example/androidplayer/data/model/CanStatistics;", "getCanStatistics-IoAF18A", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getComponentLogs", "Lcom/example/androidplayer/data/model/ComponentLogSettings;", "getComponentLogs-IoAF18A", "getLogLevel", "getLogLevel-IoAF18A", "getOtaStatus", "Lcom/example/androidplayer/data/model/OtaStatus;", "getOtaStatus-IoAF18A", "getPlayerStatus", "Lcom/example/androidplayer/data/model/PlayerStatus;", "getPlayerStatus-IoAF18A", "getPlaylist", "", "getPlaylist-IoAF18A", "getSleepSettings", "Lcom/example/androidplayer/data/model/SleepSettings;", "getSleepSettings-IoAF18A", "getSystemInfo", "Lcom/example/androidplayer/data/model/SystemInfo;", "getSystemInfo-IoAF18A", "getSystemLogs", "getSystemLogs-IoAF18A", "getVerboseLogs", "getVerboseLogs-IoAF18A", "getVersionInfo", "Lcom/example/androidplayer/data/model/VersionInfo;", "getVersionInfo-IoAF18A", "getWifiSettings", "Lcom/example/androidplayer/data/model/WifiSettings;", "getWifiSettings-IoAF18A", "isBleTransport", "nextTrack", "nextTrack-IoAF18A", "pausePlayback", "pausePlayback-IoAF18A", "playTrack", "trackIndex", "", "playTrack-gIAlu-s", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "previousTrack", "previousTrack-IoAF18A", "restartDevice", "restartDevice-IoAF18A", "resumePlayback", "resumePlayback-IoAF18A", "safeApiCall", "T", "apiCall", "Lkotlin/Function1;", "Lkotlin/coroutines/Continuation;", "safeApiCall-gIAlu-s", "(Lkotlin/jvm/functions/Function1;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendPlayerCommand", "command", "track", "position", "sendPlayerCommand-BWLJW6A", "(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setBleClient", "client", "setComponentLogs", "settings", "setComponentLogs-gIAlu-s", "(Lcom/example/androidplayer/data/model/ComponentLogSettings;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setLogLevel", "level", "setLogLevel-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setPosition", "positionMs", "setPosition-gIAlu-s", "setSleepSettings", "setSleepSettings-gIAlu-s", "(Lcom/example/androidplayer/data/model/SleepSettings;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setVerboseLogs", "enabled", "setVerboseLogs-gIAlu-s", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setWifiSettings", "setWifiSettings-gIAlu-s", "(Lcom/example/androidplayer/data/model/WifiSettings;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "switchMode", "switchMode-IoAF18A", "switchTransport", "useBle", "testConnection", "testConnection-IoAF18A", "toggleSniffer", "toggleSniffer-gIAlu-s", "uploadMasterFirmware", "firmware", "", "uploadMasterFirmware-gIAlu-s", "([BLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadSlaveFirmware", "uploadSlaveFirmware-gIAlu-s", "app_debug"})
public final class Esp32PlayerRepository {
    @org.jetbrains.annotations.NotNull
    private final com.example.androidplayer.data.network.Esp32PlayerApi api = null;
    
    /**
     * Текущий источник подключения (true=BLE, false=HTTP)
     */
    private boolean useBleTransport = false;
    
    /**
     * BLE клієнт (опціональний, встановлюється пізніше)
     */
    @org.jetbrains.annotations.Nullable
    private com.example.androidplayer.data.ble.BlePlayerClient bleClient;
    
    @javax.inject.Inject
    public Esp32PlayerRepository(@org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.network.Esp32PlayerApi api) {
        super();
    }
    
    /**
     * Текущий источник подключения (true=BLE, false=HTTP)
     */
    public final boolean getUseBleTransport() {
        return false;
    }
    
    /**
     * Встановити BLE клієнт та переключити в BLE режим
     */
    public final void setBleClient(@org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.ble.BlePlayerClient client) {
    }
    
    /**
     * Відключити BLE та переключити в HTTP режим
     */
    public final void disconnectBle() {
    }
    
    /**
     * Повертає, чи активний BLE транспорт (true) або HTTP (false)
     */
    public final boolean isBleTransport() {
        return false;
    }
    
    /**
     * Переключение между BLE и HTTP транспортом
     */
    public final void switchTransport(boolean useBle) {
    }
    
    /**
     * Получить текущий активный транспорт
     */
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getActiveTransport() {
        return null;
    }
}