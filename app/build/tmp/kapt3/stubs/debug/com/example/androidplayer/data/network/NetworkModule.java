package com.example.androidplayer.data.network;

import com.example.androidplayer.BuildConfig;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

/**
 * Сетевой клиент для взаимодействия с ESP32 плеером
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u0006\u0010\u0007\u001a\u00020\bJ\u000e\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\u000bJ\b\u0010\f\u001a\u00020\rH\u0002J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000bH\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0011"}, d2 = {"Lcom/example/androidplayer/data/network/NetworkModule;", "", "()V", "CONNECT_TIMEOUT", "", "READ_TIMEOUT", "WRITE_TIMEOUT", "createDefaultEsp32Api", "Lcom/example/androidplayer/data/network/Esp32PlayerApi;", "createEsp32Api", "ipAddress", "", "createOkHttpClient", "Lokhttp3/OkHttpClient;", "createRetrofitClient", "Lretrofit2/Retrofit;", "baseUrl", "app_debug"})
public final class NetworkModule {
    private static final long CONNECT_TIMEOUT = 10L;
    private static final long READ_TIMEOUT = 30L;
    private static final long WRITE_TIMEOUT = 30L;
    @org.jetbrains.annotations.NotNull
    public static final com.example.androidplayer.data.network.NetworkModule INSTANCE = null;
    
    private NetworkModule() {
        super();
    }
    
    /**
     * Создание настроенного OkHttpClient
     */
    private final okhttp3.OkHttpClient createOkHttpClient() {
        return null;
    }
    
    /**
     * Создание настроенного Retrofit клиента
     */
    private final retrofit2.Retrofit createRetrofitClient(java.lang.String baseUrl) {
        return null;
    }
    
    /**
     * Создание API сервиса для указанного IP адреса ESP32
     */
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.data.network.Esp32PlayerApi createEsp32Api(@org.jetbrains.annotations.NotNull
    java.lang.String ipAddress) {
        return null;
    }
    
    /**
     * Создание API сервиса с стандартным IP адресом ESP32 (192.168.4.1)
     */
    @org.jetbrains.annotations.NotNull
    public final com.example.androidplayer.data.network.Esp32PlayerApi createDefaultEsp32Api() {
        return null;
    }
}