package com.example.androidplayer.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * Модель CAN сообщения
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\t\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006H\u00c6\u0003J\u0010\u0010\u0015\u001a\u0004\u0018\u00010\bH\u00c6\u0003\u00a2\u0006\u0002\u0010\u0010J>\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\u000e\b\u0002\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\bH\u00c6\u0001\u00a2\u0006\u0002\u0010\u0017J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001c\u001a\u00020\u001dH\u00d6\u0001R\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0015\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\n\n\u0002\u0010\u0011\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006\u001e"}, d2 = {"Lcom/example/androidplayer/data/model/CanMessage;", "", "id", "", "length", "data", "", "timestamp", "", "(IILjava/util/List;Ljava/lang/Long;)V", "getData", "()Ljava/util/List;", "getId", "()I", "getLength", "getTimestamp", "()Ljava/lang/Long;", "Ljava/lang/Long;", "component1", "component2", "component3", "component4", "copy", "(IILjava/util/List;Ljava/lang/Long;)Lcom/example/androidplayer/data/model/CanMessage;", "equals", "", "other", "hashCode", "toString", "", "app_debug"})
public final class CanMessage {
    private final int id = 0;
    private final int length = 0;
    @org.jetbrains.annotations.NotNull
    private final java.util.List<java.lang.Integer> data = null;
    @org.jetbrains.annotations.Nullable
    private final java.lang.Long timestamp = null;
    
    public CanMessage(int id, int length, @org.jetbrains.annotations.NotNull
    java.util.List<java.lang.Integer> data, @org.jetbrains.annotations.Nullable
    java.lang.Long timestamp) {
        super();
    }
    
    public final int getId() {
        return 0;
    }
    
    public final int getLength() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<java.lang.Integer> getData() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long getTimestamp() {
        return null;
    }
    
    public final int component1() {
        return 0;
    }
    
    public final int component2() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull
    public final java.util.List<java.lang.Integer> component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable
    public final java.lang.Long component4() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.data.model.CanMessage copy(int id, int length, @org.jetbrains.annotations.NotNull
    java.util.List<java.lang.Integer> data, @org.jetbrains.annotations.Nullable
    java.lang.Long timestamp) {
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