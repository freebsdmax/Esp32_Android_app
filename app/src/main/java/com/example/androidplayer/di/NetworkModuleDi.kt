package com.example.androidplayer.di

import android.content.Context
import com.example.androidplayer.data.network.Esp32PlayerApi
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * DI модуль для сетевых зависимостей
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModuleDi {
    
    private const val CONNECT_TIMEOUT = 10L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L
    private const val DEFAULT_ESP32_IP = "http://192.168.4.1"
    private const val PREFS_NAME = "esp32_player_prefs"
    private const val KEY_ESP32_IP = "esp32_ip"

    /**
     * Предоставляет OkHttpClient
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    /**
     * Предоставляет Gson
     */
    @Provides
    @Singleton
    fun provideGson() = GsonBuilder().setLenient().create()

    /**
     * Предоставляет Retrofit с IP из настроек
     */
    @Provides
    @Singleton
    @Named("esp32_retrofit")
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        gson: com.google.gson.Gson,
        @ApplicationContext context: Context
    ): Retrofit {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val ipAddress = prefs.getString(KEY_ESP32_IP, DEFAULT_ESP32_IP) ?: DEFAULT_ESP32_IP
        
        val baseUrl = if (ipAddress.endsWith("/")) {
            ipAddress
        } else {
            "$ipAddress/"
        }

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Предоставляет Esp32PlayerApi
     */
    @Provides
    @Singleton
    fun provideEsp32PlayerApi(
        @Named("esp32_retrofit") retrofit: Retrofit
    ): Esp32PlayerApi {
        return retrofit.create(Esp32PlayerApi::class.java)
    }

    /**
     * Предоставляет IP адрес ESP32 из настроек или по умолчанию
     */
    @Provides
    @Singleton
    @Named("esp32_ip")
    fun provideEsp32Ip(@ApplicationContext context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_ESP32_IP, DEFAULT_ESP32_IP) ?: DEFAULT_ESP32_IP
    }
}