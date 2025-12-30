package com.example.androidplayer.ui.screens;

import android.content.Intent;
import android.net.Uri;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.foundation.layout.*;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.*;
import androidx.compose.material3.*;
import androidx.compose.runtime.*;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.text.font.FontWeight;
import com.example.androidplayer.ui.viewmodel.OtaUiState;
import com.example.androidplayer.ui.viewmodel.OtaViewModel;
import java.io.File;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u001a\u0016\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a\u001e\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a\u0010\u0010\b\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0006H\u0003\u001a$\u0010\t\u001a\u00020\u00012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a\u0010\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000eH\u0003\u001a\u0012\u0010\u000f\u001a\u00020\u00012\b\b\u0002\u0010\u0010\u001a\u00020\u0011H\u0007\u001a$\u0010\u0012\u001a\u00020\u00012\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u00032\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0003H\u0003\u001a\u0010\u0010\u0013\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0006H\u0003\u001a\u0010\u0010\u0014\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000eH\u0003\u001a\u0018\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0017\u001a\u00020\u0006H\u0003\u00a8\u0006\u0018"}, d2 = {"DeviceControlSection", "", "onRestart", "Lkotlin/Function0;", "ErrorCard", "message", "", "onDismiss", "InfoCard", "MasterFirmwareSection", "onSelectFile", "onUpload", "OtaProgressSection", "uiState", "Lcom/example/androidplayer/ui/viewmodel/OtaUiState;", "OtaScreen", "viewModel", "Lcom/example/androidplayer/ui/viewmodel/OtaViewModel;", "SlaveFirmwareSection", "SuccessCard", "VersionInfoSection", "VersionItem", "label", "version", "app_debug"})
public final class OtaScreenKt {
    
    /**
     * Экран OTA обновлений
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable
    public static final void OtaScreen(@org.jetbrains.annotations.NotNull
    com.example.androidplayer.ui.viewmodel.OtaViewModel viewModel) {
    }
    
    /**
     * Секция информации о версиях
     */
    @androidx.compose.runtime.Composable
    private static final void VersionInfoSection(com.example.androidplayer.ui.viewmodel.OtaUiState uiState) {
    }
    
    /**
     * Элемент версии
     */
    @androidx.compose.runtime.Composable
    private static final void VersionItem(java.lang.String label, java.lang.String version) {
    }
    
    /**
     * Секция прогресса OTA
     */
    @androidx.compose.runtime.Composable
    private static final void OtaProgressSection(com.example.androidplayer.ui.viewmodel.OtaUiState uiState) {
    }
    
    /**
     * Секция загрузки прошивки Master
     */
    @androidx.compose.runtime.Composable
    private static final void MasterFirmwareSection(kotlin.jvm.functions.Function0<kotlin.Unit> onSelectFile, kotlin.jvm.functions.Function0<kotlin.Unit> onUpload) {
    }
    
    /**
     * Секция загрузки прошивки Slave
     */
    @androidx.compose.runtime.Composable
    private static final void SlaveFirmwareSection(kotlin.jvm.functions.Function0<kotlin.Unit> onSelectFile, kotlin.jvm.functions.Function0<kotlin.Unit> onUpload) {
    }
    
    /**
     * Секция управления устройством
     */
    @androidx.compose.runtime.Composable
    private static final void DeviceControlSection(kotlin.jvm.functions.Function0<kotlin.Unit> onRestart) {
    }
    
    /**
     * Карточка успеха
     */
    @androidx.compose.runtime.Composable
    private static final void SuccessCard(java.lang.String message) {
    }
    
    /**
     * Карточка информации
     */
    @androidx.compose.runtime.Composable
    private static final void InfoCard(java.lang.String message) {
    }
    
    /**
     * Карточка ошибки
     */
    @androidx.compose.runtime.Composable
    private static final void ErrorCard(java.lang.String message, kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
}