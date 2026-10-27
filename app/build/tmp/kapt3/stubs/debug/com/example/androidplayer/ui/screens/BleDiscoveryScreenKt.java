package com.example.androidplayer.ui.screens;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import androidx.core.content.ContextCompat;
import com.example.androidplayer.data.ble.BlePlayerClient;
import com.example.androidplayer.ui.viewmodel.PlayerViewModel;
import timber.log.Timber;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a4\u0010\u0000\u001a\u00020\u00012\u0012\u0010\u0002\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u0007\u001a$\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u00a8\u0006\r"}, d2 = {"BleDiscoveryScreen", "", "onDeviceSelected", "Lkotlin/Function1;", "", "onNavigateBack", "Lkotlin/Function0;", "playerViewModel", "Lcom/example/androidplayer/ui/viewmodel/PlayerViewModel;", "DeviceCard", "device", "Landroid/bluetooth/BluetoothDevice;", "onConnect", "app_debug"})
public final class BleDiscoveryScreenKt {
    
    /**
     * Екран для пошуку та підключення до BLE пристрою
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable
    public static final void BleDiscoveryScreen(@org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onDeviceSelected, @org.jetbrains.annotations.NotNull
    kotlin.jvm.functions.Function0<kotlin.Unit> onNavigateBack, @org.jetbrains.annotations.NotNull
    com.example.androidplayer.ui.viewmodel.PlayerViewModel playerViewModel) {
    }
    
    @androidx.compose.runtime.Composable
    private static final void DeviceCard(android.bluetooth.BluetoothDevice device, kotlin.jvm.functions.Function1<? super android.bluetooth.BluetoothDevice, kotlin.Unit> onConnect) {
    }
}