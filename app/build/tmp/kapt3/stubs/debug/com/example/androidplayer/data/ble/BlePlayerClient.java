package com.example.androidplayer.data.ble;

import android.annotation.SuppressLint;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import com.example.androidplayer.data.model.CanStatistics;
import com.example.androidplayer.data.model.ComponentLogSettings;
import com.example.androidplayer.data.model.SleepSettings;
import com.example.androidplayer.data.model.SystemInfo;
import com.example.androidplayer.data.model.WifiSettings;
import kotlinx.coroutines.flow.StateFlow;
import org.json.JSONObject;
import androidx.core.content.ContextCompat;
import timber.log.Timber;
import java.util.UUID;
import java.util.ArrayDeque;

/**
 * BLE Player клієнт для управління ESP32 плеєром по Bluetooth LE
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00d6\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b \u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010I\u001a\u00020\u00122\u0006\u0010J\u001a\u00020\u0010H\u0007J\b\u0010K\u001a\u00020\u0012H\u0007J\b\u0010L\u001a\u00020MH\u0007J\u0011\u0010N\u001a\u00020OH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010PJ\u0011\u0010Q\u001a\u00020RH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010PJ\u0011\u0010S\u001a\u00020\u0015H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010PJ\u0017\u0010T\u001a\b\u0012\u0004\u0012\u00020\u00150\u000fH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010PJ\u0011\u0010U\u001a\u00020VH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010PJ\u0011\u0010W\u001a\u00020\u0019H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010PJ\u0011\u0010X\u001a\u00020YH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010PJ\u0011\u0010Z\u001a\u00020\u0015H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010PJ\u0011\u0010[\u001a\u00020\u0012H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010PJ\u0011\u0010\\\u001a\u00020]H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010PJ\u0011\u0010^\u001a\u00020_H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010PJ\u0006\u0010`\u001a\u00020\u0012J\u0006\u0010a\u001a\u00020\u0015J\u0006\u0010b\u001a\u00020\u0015J\u000e\u0010c\u001a\u00020\u00152\u0006\u0010d\u001a\u00020eJ\u0006\u0010f\u001a\u00020\u0015J\u0011\u0010g\u001a\u00020\u0015H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010PJ\u0006\u0010h\u001a\u00020\u0015J%\u0010i\u001a\u00020\u00152\u0006\u0010j\u001a\u00020\u00152\n\b\u0002\u0010k\u001a\u0004\u0018\u00010\u0015H\u0082@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010lJ\u000e\u0010m\u001a\u00020\u00152\u0006\u0010n\u001a\u00020eJ\u001a\u0010o\u001a\u00020\u00122\u0006\u0010p\u001a\u00020\u00152\b\b\u0002\u0010q\u001a\u00020\u0015H\u0007J\u0019\u0010r\u001a\u00020\u00152\u0006\u0010s\u001a\u00020RH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010tJ\u0019\u0010u\u001a\u00020\u00152\u0006\u0010v\u001a\u00020\u0015H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010wJ\u0019\u0010x\u001a\u00020\u00152\u0006\u0010s\u001a\u00020VH\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010yJ\u0019\u0010z\u001a\u00020\u00152\u0006\u0010{\u001a\u00020\u0012H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010|J\u0019\u0010}\u001a\u00020\u00152\u0006\u0010s\u001a\u00020_H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010~J\b\u0010\u007f\u001a\u00020MH\u0003J\t\u0010\u0080\u0001\u001a\u00020MH\u0007J\t\u0010\u0081\u0001\u001a\u00020MH\u0007J\u001a\u0010\u0082\u0001\u001a\u00020\u00152\u0006\u0010{\u001a\u00020\u0012H\u0086@\u00f8\u0001\u0000\u00a2\u0006\u0002\u0010|J\u0012\u0010\u0083\u0001\u001a\u00020M2\u0007\u0010\u0084\u0001\u001a\u00020\u001dH\u0003R\u0016\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\n\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\f\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0014\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u000f0\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0016\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00150\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00120\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0018\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00190\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u001dX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010 \u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010!\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\"\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010#\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000f0$\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010&R\u000e\u0010\'\u001a\u00020\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010(\u001a\u00020)X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00120$\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010&R\u0010\u0010+\u001a\u0004\u0018\u00010\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00120$\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010&R\u0014\u0010.\u001a\b\u0012\u0004\u0012\u0002000/X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u00101\u001a\b\u0012\u0004\u0012\u0002020/X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u00103\u001a\n\u0012\u0004\u0012\u00020\u0015\u0018\u000104X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u00105\u001a\u0004\u0018\u00010\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001d\u00106\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00150\u000f0$\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u0010&R\u0016\u00108\u001a\n \u0007*\u0004\u0018\u00010909X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010:\u001a\u00020;X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010<\u001a\u00020=X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010>\u001a\u00020?X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010@\u001a\u0004\u0018\u00010AX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010B\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00150$\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u0010&R\u0017\u0010D\u001a\b\u0012\u0004\u0012\u00020\u00120$\u00a2\u0006\b\n\u0000\u001a\u0004\bE\u0010&R\u0010\u0010F\u001a\u0004\u0018\u00010\u001fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0019\u0010G\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00190$\u00a2\u0006\b\n\u0000\u001a\u0004\bH\u0010&\u0082\u0002\u0004\n\u0002\b\u0019\u00a8\u0006\u0085\u0001"}, d2 = {"Lcom/example/androidplayer/data/ble/BlePlayerClient;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "CCCD_UUID", "Ljava/util/UUID;", "kotlin.jvm.PlatformType", "COMMAND_CHAR_UUID", "LOGS_CHAR_UUID", "PLAYER_SERVICE_UUID", "PLAYLIST_CHAR_UUID", "STATUS_CHAR_UUID", "_discoveredDevices", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Landroid/bluetooth/BluetoothDevice;", "_isConnected", "", "_notificationsReady", "_playlistData", "", "_serviceError", "_serviceReady", "_statusData", "Lcom/example/androidplayer/data/model/PlayerStatus;", "bluetoothAdapter", "Landroid/bluetooth/BluetoothAdapter;", "bluetoothGatt", "Landroid/bluetooth/BluetoothGatt;", "commandCharacteristic", "Landroid/bluetooth/BluetoothGattCharacteristic;", "commandWriteInProgress", "commandWriteLock", "connectedDevice", "discoveredDevices", "Lkotlinx/coroutines/flow/StateFlow;", "getDiscoveredDevices", "()Lkotlinx/coroutines/flow/StateFlow;", "explicitlyDisconnected", "gattCallback", "Landroid/bluetooth/BluetoothGattCallback;", "isConnected", "logsCharacteristic", "notificationsReady", "getNotificationsReady", "pendingCommandWrites", "Ljava/util/ArrayDeque;", "", "pendingNotificationDescriptors", "Landroid/bluetooth/BluetoothGattDescriptor;", "pendingRpcResponse", "Lkotlinx/coroutines/CompletableDeferred;", "playlistCharacteristic", "playlistData", "getPlaylistData", "preferences", "Landroid/content/SharedPreferences;", "reconnectHandler", "Landroid/os/Handler;", "rpcMutex", "Lkotlinx/coroutines/sync/Mutex;", "scanCallback", "Landroid/bluetooth/le/ScanCallback;", "scanner", "Landroid/bluetooth/le/BluetoothLeScanner;", "serviceError", "getServiceError", "serviceReady", "getServiceReady", "statusCharacteristic", "statusData", "getStatusData", "connect", "device", "connectRememberedDevice", "disconnect", "", "getCanStatistics", "Lcom/example/androidplayer/data/model/CanStatistics;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getComponentLogs", "Lcom/example/androidplayer/data/model/ComponentLogSettings;", "getLogLevel", "getPlaylist", "getSleepSettings", "Lcom/example/androidplayer/data/model/SleepSettings;", "getStatus", "getSystemInfo", "Lcom/example/androidplayer/data/model/SystemInfo;", "getSystemLogs", "getVerboseLogs", "getVersionInfo", "Lcom/example/androidplayer/data/model/VersionInfo;", "getWifiSettings", "Lcom/example/androidplayer/data/model/WifiSettings;", "hasRememberedDevice", "next", "pause", "play", "trackIndex", "", "previous", "restartDevice", "resume", "rpc", "operation", "payload", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "seek", "positionMs", "sendCommand", "command", "params", "setComponentLogs", "settings", "(Lcom/example/androidplayer/data/model/ComponentLogSettings;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setLogLevel", "level", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setSleepSettings", "(Lcom/example/androidplayer/data/model/SleepSettings;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setVerboseLogs", "enabled", "(ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setWifiSettings", "(Lcom/example/androidplayer/data/model/WifiSettings;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "startNextCommandWrite", "startScan", "stopScan", "toggleSniffer", "writeNextNotificationDescriptor", "gatt", "app_debug"})
public final class BlePlayerClient {
    @org.jetbrains.annotations.NotNull
    private final android.content.Context context = null;
    private final android.content.SharedPreferences preferences = null;
    @org.jetbrains.annotations.NotNull
    private final android.os.Handler reconnectHandler = null;
    @org.jetbrains.annotations.Nullable
    private final android.bluetooth.BluetoothAdapter bluetoothAdapter = null;
    @org.jetbrains.annotations.Nullable
    private final android.bluetooth.le.BluetoothLeScanner scanner = null;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.BluetoothGatt bluetoothGatt;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.BluetoothDevice connectedDevice;
    private final java.util.UUID PLAYER_SERVICE_UUID = null;
    private final java.util.UUID COMMAND_CHAR_UUID = null;
    private final java.util.UUID STATUS_CHAR_UUID = null;
    private final java.util.UUID PLAYLIST_CHAR_UUID = null;
    private final java.util.UUID LOGS_CHAR_UUID = null;
    private final java.util.UUID CCCD_UUID = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isConnected = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isConnected = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<android.bluetooth.BluetoothDevice>> _discoveredDevices = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<android.bluetooth.BluetoothDevice>> discoveredDevices = null;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.BluetoothGattCharacteristic commandCharacteristic;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.BluetoothGattCharacteristic statusCharacteristic;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.BluetoothGattCharacteristic playlistCharacteristic;
    @org.jetbrains.annotations.Nullable
    private android.bluetooth.BluetoothGattCharacteristic logsCharacteristic;
    @org.jetbrains.annotations.NotNull
    private final java.util.ArrayDeque<android.bluetooth.BluetoothGattDescriptor> pendingNotificationDescriptors = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.Object commandWriteLock = null;
    @org.jetbrains.annotations.NotNull
    private final java.util.ArrayDeque<byte[]> pendingCommandWrites = null;
    private boolean commandWriteInProgress = false;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.sync.Mutex rpcMutex = null;
    @kotlin.jvm.Volatile
    @org.jetbrains.annotations.Nullable
    private volatile kotlinx.coroutines.CompletableDeferred<java.lang.String> pendingRpcResponse;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<com.example.androidplayer.data.model.PlayerStatus> _statusData = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<com.example.androidplayer.data.model.PlayerStatus> statusData = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<java.lang.String>> _playlistData = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<java.lang.String>> playlistData = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _notificationsReady = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> notificationsReady = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _serviceReady = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> serviceReady = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.MutableStateFlow<java.lang.String> _serviceError = null;
    @org.jetbrains.annotations.NotNull
    private final kotlinx.coroutines.flow.StateFlow<java.lang.String> serviceError = null;
    @kotlin.jvm.Volatile
    private volatile boolean explicitlyDisconnected = false;
    @org.jetbrains.annotations.NotNull
    private final android.bluetooth.le.ScanCallback scanCallback = null;
    @org.jetbrains.annotations.NotNull
    private final android.bluetooth.BluetoothGattCallback gattCallback = null;
    
    public BlePlayerClient(@org.jetbrains.annotations.NotNull
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isConnected() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<android.bluetooth.BluetoothDevice>> getDiscoveredDevices() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<com.example.androidplayer.data.model.PlayerStatus> getStatusData() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<java.lang.String>> getPlaylistData() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getNotificationsReady() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> getServiceReady() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final kotlinx.coroutines.flow.StateFlow<java.lang.String> getServiceError() {
        return null;
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    private final void writeNextNotificationDescriptor(android.bluetooth.BluetoothGatt gatt) {
    }
    
    private final java.lang.Object rpc(java.lang.String operation, java.lang.String payload, kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getWifiSettings(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.WifiSettings> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object setWifiSettings(@org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.model.WifiSettings settings, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getSleepSettings(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.SleepSettings> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object setSleepSettings(@org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.model.SleepSettings settings, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getLogLevel(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object setLogLevel(@org.jetbrains.annotations.NotNull
    java.lang.String level, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getVerboseLogs(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object setVerboseLogs(boolean enabled, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getComponentLogs(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.ComponentLogSettings> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object setComponentLogs(@org.jetbrains.annotations.NotNull
    com.example.androidplayer.data.model.ComponentLogSettings settings, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getSystemLogs(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getSystemInfo(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.SystemInfo> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getCanStatistics(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.CanStatistics> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getVersionInfo(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.VersionInfo> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object restartDevice(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object toggleSniffer(boolean enabled, @org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    /**
     * Сканування BLE пристроїв
     */
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void startScan() {
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void stopScan() {
    }
    
    /**
     * Підключення до пристрою
     */
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final boolean connect(@org.jetbrains.annotations.NotNull
    android.bluetooth.BluetoothDevice device) {
        return false;
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final boolean connectRememberedDevice() {
        return false;
    }
    
    public final boolean hasRememberedDevice() {
        return false;
    }
    
    /**
     * Від'єднання
     */
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void disconnect() {
    }
    
    /**
     * Відправка команди
     */
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final boolean sendCommand(@org.jetbrains.annotations.NotNull
    java.lang.String command, @org.jetbrains.annotations.NotNull
    java.lang.String params) {
        return false;
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    private final void startNextCommandWrite() {
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String play(int trackIndex) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String pause() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String resume() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String next() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String previous() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String seek(int positionMs) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getStatus(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super com.example.androidplayer.data.model.PlayerStatus> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Object getPlaylist(@org.jetbrains.annotations.NotNull
    kotlin.coroutines.Continuation<? super java.util.List<java.lang.String>> $completion) {
        return null;
    }
}