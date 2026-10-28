package com.example.androidplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import com.example.androidplayer.ui.notify.AppNotify

/**
 * Главный класс приложения Android для управления ESP32 Music Player
 */
@HiltAndroidApp
class AndroidPlayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        AppNotify.init(this)
        
        // Инициализация Timber для логирования
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        Timber.d("AndroidPlayerApplication initialized")
    }
}