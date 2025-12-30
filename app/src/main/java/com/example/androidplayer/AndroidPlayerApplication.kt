package com.example.androidplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * Главный класс приложения Android для управления ESP32 Music Player
 */
@HiltAndroidApp
class AndroidPlayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Инициализация Timber для логирования
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.d("AndroidPlayerApplication initialized")
    }
}