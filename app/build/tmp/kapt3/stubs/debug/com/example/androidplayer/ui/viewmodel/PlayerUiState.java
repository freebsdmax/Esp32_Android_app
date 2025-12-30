package com.example.androidplayer.ui.viewmodel;

import androidx.lifecycle.ViewModel;
import com.example.androidplayer.data.model.*;
import com.example.androidplayer.data.repository.Esp32PlayerRepository;
import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.StateFlow;
import timber.log.Timber;
import javax.inject.Inject;

/**
 * UI состояние плеера
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u001c\b\u0086\b\u0018\u00002\u00020\u0001B[\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u0012\b\b\u0002\u0010\f\u001a\u00020\u000b\u0012\b\b\u0002\u0010\r\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\u000eJ\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u000b0\nH\u00c6\u0003J\t\u0010 \u001a\u00020\u000bH\u00c6\u0003J\t\u0010!\u001a\u00020\u0007H\u00c6\u0003J_\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00032\u000e\b\u0002\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n2\b\b\u0002\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010#\u001a\u00020\u00072\b\u0010$\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010%\u001a\u00020\u0003H\u00d6\u0001J\t\u0010&\u001a\u00020\u000bH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0012R\u0011\u0010\f\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0010R\u0011\u0010\r\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0012\u00a8\u0006\'"}, d2 = {"Lcom/example/androidplayer/ui/viewmodel/PlayerUiState;", "", "currentTrack", "", "positionMs", "durationMs", "isPaused", "", "playlistSize", "playlist", "", "", "mode", "snifferEnabled", "(IIIZILjava/util/List;Ljava/lang/String;Z)V", "getCurrentTrack", "()I", "getDurationMs", "()Z", "getMode", "()Ljava/lang/String;", "getPlaylist", "()Ljava/util/List;", "getPlaylistSize", "getPositionMs", "getSnifferEnabled", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class PlayerUiState {
    private final int currentTrack = 0;
    private final int positionMs = 0;
    private final int durationMs = 0;
    private final boolean isPaused = false;
    private final int playlistSize = 0;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<java.lang.String> playlist = null;
    @org.jetbrains.annotations.NotNull
    private final java.lang.String mode = null;
    private final boolean snifferEnabled = false;
    
    public PlayerUiState(int currentTrack, int positionMs, int durationMs, boolean isPaused, int playlistSize, @org.jetbrains.annotations.NotNull
    java.util.List<java.lang.String> playlist, @org.jetbrains.annotations.NotNull
    java.lang.String mode, boolean snifferEnabled) {
        super();
    }
    
    public final int getCurrentTrack() {
        return 0;
    }
    
    public final int getPositionMs() {
        return 0;
    }
    
    public final int getDurationMs() {
        return 0;
    }
    
    public final boolean isPaused() {
        return false;
    }
    
    public final int getPlaylistSize() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<java.lang.String> getPlaylist() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String getMode() {
        return null;
    }
    
    public final boolean getSnifferEnabled() {
        return false;
    }
    
    public PlayerUiState() {
        super();
    }
    
    public final int component1() {
        return 0;
    }
    
    public final int component2() {
        return 0;
    }
    
    public final int component3() {
        return 0;
    }
    
    public final boolean component4() {
        return false;
    }
    
    public final int component5() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<java.lang.String> component6() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.lang.String component7() {
        return null;
    }
    
    public final boolean component8() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.ui.viewmodel.PlayerUiState copy(int currentTrack, int positionMs, int durationMs, boolean isPaused, int playlistSize, @org.jetbrains.annotations.NotNull
    java.util.List<java.lang.String> playlist, @org.jetbrains.annotations.NotNull
    java.lang.String mode, boolean snifferEnabled) {
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