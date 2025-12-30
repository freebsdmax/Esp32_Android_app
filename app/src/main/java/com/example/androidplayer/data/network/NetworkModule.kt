package com.example.androidplayer.data.network

import com.example.androidplayer.BuildConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Сетевой клиент для взаимодействия с ESP32 плеером
 */
object NetworkModule {
    
    private const val CONNECT_TIMEOUT = 10L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 30L
    
    /**
     * Создание настроенного OkHttpClient
     */
    private fun createOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
        
        // Добавляем логгирование только в debug режиме
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            builder.addInterceptor(loggingInterceptor)
        }
        
        return builder.build()
    }
    
    /**
     * Создание настроенного Retrofit клиента
     */
    private fun createRetrofitClient(baseUrl: String): Retrofit {
        val gson = GsonBuilder()
            .setLenient()
            .create()
            
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    /**
     * Создание API сервиса для указанного IP адреса ESP32
     */
    fun createEsp32Api(ipAddress: String): Esp32PlayerApi {
        val baseUrl = if (ipAddress.startsWith("http://")) {
            ipAddress
        } else {
            "http://$ipAddress"
        }
        
        // Добавляем завершающий слэш если его нет
        val finalBaseUrl = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        
        return createRetrofitClient(finalBaseUrl)
            .create(Esp32PlayerApi::class.java)
    }
    
    /**
     * Создание API сервиса с стандартным IP адресом ESP32 (192.168.4.1)
     */
    fun createDefaultEsp32Api(): Esp32PlayerApi {
        return createEsp32Api("http://192.168.4.1")
    }
}