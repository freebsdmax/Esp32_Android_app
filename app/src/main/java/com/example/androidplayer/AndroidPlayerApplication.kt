package com.example.androidplayer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import com.example.androidplayer.data.update.UpdateChecker
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
        
        val checkerEntry = EntryPointAccessors.fromApplication(this, UpdateCheckerEntryPoint::class.java)
        checkerEntry.updateChecker().start()

        Timber.d("AndroidPlayerApplication initialized")
    }
}

@EntryPoint
@InstallIn(SingletonComponent::class)
interface UpdateCheckerEntryPoint {
    fun updateChecker(): UpdateChecker
}