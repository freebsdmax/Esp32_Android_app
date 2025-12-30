package com.example.androidplayer.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.androidplayer.data.model.*;
import com.example.androidplayer.data.repository.Esp32PlayerRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.ExperimentalCoroutinesApi;
import timber.log.Timber;
import java.io.File;
import javax.inject.Inject;

/**
 * UI состояние OTA
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b3\b\u0086\b\u0018\u00002\u00020\u0001B\u00b5\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u0012\b\b\u0002\u0010\b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\f\u001a\u00020\r\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\r\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\r\u0012\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\r\u0012\b\b\u0002\u0010\u0011\u001a\u00020\r\u0012\b\b\u0002\u0010\u0012\u001a\u00020\r\u0012\b\b\u0002\u0010\u0013\u001a\u00020\r\u0012\b\b\u0002\u0010\u0014\u001a\u00020\r\u0012\b\b\u0002\u0010\u0015\u001a\u00020\r\u0012\b\b\u0002\u0010\u0016\u001a\u00020\r\u00a2\u0006\u0002\u0010\u0017J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\rH\u00c6\u0003J\u000b\u0010,\u001a\u0004\u0018\u00010\rH\u00c6\u0003J\t\u0010-\u001a\u00020\rH\u00c6\u0003J\t\u0010.\u001a\u00020\rH\u00c6\u0003J\t\u0010/\u001a\u00020\rH\u00c6\u0003J\t\u00100\u001a\u00020\rH\u00c6\u0003J\t\u00101\u001a\u00020\rH\u00c6\u0003J\t\u00102\u001a\u00020\rH\u00c6\u0003J\t\u00103\u001a\u00020\u0003H\u00c6\u0003J\t\u00104\u001a\u00020\u0006H\u00c6\u0003J\t\u00105\u001a\u00020\u0006H\u00c6\u0003J\t\u00106\u001a\u00020\u0006H\u00c6\u0003J\t\u00107\u001a\u00020\nH\u00c6\u0003J\t\u00108\u001a\u00020\u0003H\u00c6\u0003J\t\u00109\u001a\u00020\rH\u00c6\u0003J\u000b\u0010:\u001a\u0004\u0018\u00010\rH\u00c6\u0003J\u00b9\u0001\u0010;\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\r2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\r2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\r2\b\b\u0002\u0010\u0011\u001a\u00020\r2\b\b\u0002\u0010\u0012\u001a\u00020\r2\b\b\u0002\u0010\u0013\u001a\u00020\r2\b\b\u0002\u0010\u0014\u001a\u00020\r2\b\b\u0002\u0010\u0015\u001a\u00020\r2\b\b\u0002\u0010\u0016\u001a\u00020\rH\u00c6\u0001J\u0013\u0010<\u001a\u00020\u00032\b\u0010=\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010>\u001a\u00020\u0006H\u00d6\u0001J\t\u0010?\u001a\u00020\rH\u00d6\u0001R\u0011\u0010\b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0013\u0010\u000f\u001a\u0004\u0018\u00010\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u001cR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u001cR\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\u001cR\u0011\u0010\u0011\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001bR\u0011\u0010\u0013\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001bR\u0011\u0010\u0012\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001bR\u0013\u0010\u0010\u001a\u0004\u0018\u00010\r\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u001bR\u0011\u0010\u0015\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001bR\u0011\u0010\u0014\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\u001bR\u0011\u0010\u0016\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001bR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\u0019R\u0013\u0010\u000e\u001a\u0004\u0018\u00010\r\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\u001bR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\'R\u0011\u0010\f\u001a\u00020\r\u00a2\u0006\b\n\u0000\u001a\u0004\b(\u0010\u001bR\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u0019\u00a8\u0006@"}, d2 = {"Lcom/example/androidplayer/ui/viewmodel/OtaUiState;", "", "isActive", "", "isUartSlave", "totalBytes", "", "writtenBytes", "currentBlock", "uploadProgress", "", "isUploading", "uploadTarget", "", "uploadMessage", "errorMessage", "restartMessage", "masterPlayerVersion", "masterWebVersion", "masterUartVersion", "slaveMainVersion", "slaveCrcVersion", "slaveUartVersion", "(ZZIIIFZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getCurrentBlock", "()I", "getErrorMessage", "()Ljava/lang/String;", "()Z", "getMasterPlayerVersion", "getMasterUartVersion", "getMasterWebVersion", "getRestartMessage", "getSlaveCrcVersion", "getSlaveMainVersion", "getSlaveUartVersion", "getTotalBytes", "getUploadMessage", "getUploadProgress", "()F", "getUploadTarget", "getWrittenBytes", "component1", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class OtaUiState {
    private final boolean isActive = false;
    private final boolean isUartSlave = false;
    private final int totalBytes = 0;
    private final int writtenBytes = 0;
    private final int currentBlock = 0;
    private final float uploadProgress = 0.0F;
    private final boolean isUploading = false;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String uploadTarget = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String uploadMessage = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String errorMessage = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.String restartMessage = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String masterPlayerVersion = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String masterWebVersion = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String masterUartVersion = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String slaveMainVersion = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String slaveCrcVersion = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String slaveUartVersion = null;
    
    public OtaUiState(boolean isActive, boolean isUartSlave, int totalBytes, int writtenBytes, int currentBlock, float uploadProgress, boolean isUploading, @org.jetbrains.annotations.NotNull
    java.lang.String uploadTarget, @org.jetbrains.annotations.Nullable
    java.lang.String uploadMessage, @org.jetbrains.annotations.Nullable
    java.lang.String errorMessage, @org.jetbrains.annotations.Nullable
    java.lang.String restartMessage, @org.jetbrains.annotations.NotNull
    java.lang.String masterPlayerVersion, @org.jetbrains.annotations.NotNull
    java.lang.String masterWebVersion, @org.jetbrains.annotations.NotNull
    java.lang.String masterUartVersion, @org.jetbrains.annotations.NotNull
    java.lang.String slaveMainVersion, @org.jetbrains.annotations.NotNull
    java.lang.String slaveCrcVersion, @org.jetbrains.annotations.NotNull
    java.lang.String slaveUartVersion) {
        super();
    }
    
    public final boolean isActive() {
        return false;
    }
    
    public final boolean isUartSlave() {
        return false;
    }
    
    public final int getTotalBytes() {
        return 0;
    }
    
    public final int getWrittenBytes() {
        return 0;
    }
    
    public final int getCurrentBlock() {
        return 0;
    }
    
    public final float getUploadProgress() {
        return 0.0F;
    }
    
    public final boolean isUploading() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getUploadTarget() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getUploadMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getErrorMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String getRestartMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getMasterPlayerVersion() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getMasterWebVersion() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getMasterUartVersion() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSlaveMainVersion() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSlaveCrcVersion() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getSlaveUartVersion() {
        return null;
    }
    
    public OtaUiState() {
        super();
    }
    
    public final boolean component1() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component10() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
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
    
    public final float component6() {
        return 0.0F;
    }
    
    public final boolean component7() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component8() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.String component9() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.ui.viewmodel.OtaUiState copy(boolean isActive, boolean isUartSlave, int totalBytes, int writtenBytes, int currentBlock, float uploadProgress, boolean isUploading, @org.jetbrains.annotations.NotNull
    java.lang.String uploadTarget, @org.jetbrains.annotations.Nullable
    java.lang.String uploadMessage, @org.jetbrains.annotations.Nullable
    java.lang.String errorMessage, @org.jetbrains.annotations.Nullable
    java.lang.String restartMessage, @org.jetbrains.annotations.NotNull
    java.lang.String masterPlayerVersion, @org.jetbrains.annotations.NotNull
    java.lang.String masterWebVersion, @org.jetbrains.annotations.NotNull
    java.lang.String masterUartVersion, @org.jetbrains.annotations.NotNull
    java.lang.String slaveMainVersion, @org.jetbrains.annotations.NotNull
    java.lang.String slaveCrcVersion, @org.jetbrains.annotations.NotNull
    java.lang.String slaveUartVersion) {
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