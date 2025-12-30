package com.example.androidplayer.di;

import android.content.Context;
import com.example.androidplayer.data.network.Esp32PlayerApi;
import com.google.gson.GsonBuilder;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * DI модуль для сетевых зависимостей
 */
@dagger.Module
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0012\u0010\u000b\u001a\u00020\u00062\b\b\u0001\u0010\f\u001a\u00020\rH\u0007J\u0012\u0010\u000e\u001a\u00020\u000f2\b\b\u0001\u0010\u0010\u001a\u00020\u0011H\u0007J\u0010\u0010\u0012\u001a\n \u0014*\u0004\u0018\u00010\u00130\u0013H\u0007J\b\u0010\u0015\u001a\u00020\u0016H\u0007J\"\u0010\u0017\u001a\u00020\u00112\u0006\u0010\u0018\u001a\u00020\u00162\u0006\u0010\u0019\u001a\u00020\u00132\b\b\u0001\u0010\f\u001a\u00020\rH\u0007R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001a"}, d2 = {"Lcom/example/androidplayer/di/NetworkModuleDi;", "", "()V", "CONNECT_TIMEOUT", "", "DEFAULT_ESP32_IP", "", "KEY_ESP32_IP", "PREFS_NAME", "READ_TIMEOUT", "WRITE_TIMEOUT", "provideEsp32Ip", "context", "Landroid/content/Context;", "provideEsp32PlayerApi", "Lcom/example/androidplayer/data/network/Esp32PlayerApi;", "retrofit", "Lretrofit2/Retrofit;", "provideGson", "Lcom/google/gson/Gson;", "kotlin.jvm.PlatformType", "provideOkHttpClient", "Lokhttp3/OkHttpClient;", "provideRetrofit", "okHttpClient", "gson", "app_debug"})
@dagger.hilt.InstallIn(value = {dagger.hilt.components.SingletonComponent.class})
public final class NetworkModuleDi {
    private static final long CONNECT_TIMEOUT = 10L;
    private static final long READ_TIMEOUT = 30L;
    private static final long WRITE_TIMEOUT = 30L;
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String DEFAULT_ESP32_IP = "http://192.168.4.1";
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String PREFS_NAME = "esp32_player_prefs";
    @org.jetbrains.annotations.NotNull
    private static final java.lang.String KEY_ESP32_IP = "esp32_ip";
    @org.jetbrains.annotations.NotNull
    public static final com.example.androidplayer.di.NetworkModuleDi INSTANCE = null;
    
    private NetworkModuleDi() {
        super();
    }
    
    /**
     * Предоставляет OkHttpClient
     */
    @dagger.Provides
    @javax.inject.Singleton
    @org.jetbrains.annotations.NotNull
    public final okhttp3.OkHttpClient provideOkHttpClient() {
        return null;
    }
    
    /**
     * Предоставляет Gson
     */
    @dagger.Provides
    @javax.inject.Singleton
    public final com.google.gson.Gson provideGson() {
        return null;
    }
    
    /**
     * Предоставляет Retrofit с IP из настроек
     */
    @dagger.Provides
    @javax.inject.Singleton
    @javax.inject.Named(value = "esp32_retrofit")
    @org.jetbrains.annotations.NotNull
    public final retrofit2.Retrofit provideRetrofit(@org.jetbrains.annotations.NotNull
    okhttp3.OkHttpClient okHttpClient, @org.jetbrains.annotations.NotNull
    com.google.gson.Gson gson, @dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        return null;
    }
    
    /**
     * Предоставляет Esp32PlayerApi
     */
    @dagger.Provides
    @javax.inject.Singleton
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.data.network.Esp32PlayerApi provideEsp32PlayerApi(@javax.inject.Named(value = "esp32_retrofit")
    @org.jetbrains.annotations.NotNull
    retrofit2.Retrofit retrofit) {
        return null;
    }
    
    /**
     * Предоставляет IP адрес ESP32 из настроек или по умолчанию
     */
    @dagger.Provides
    @javax.inject.Singleton
    @javax.inject.Named(value = "esp32_ip")
    @org.jetbrains.annotations.NotNull
    public final java.lang.String provideEsp32Ip(@dagger.hilt.android.qualifiers.ApplicationContext
    @org.jetbrains.annotations.NotNull
    android.content.Context context) {
        return null;
    }
}