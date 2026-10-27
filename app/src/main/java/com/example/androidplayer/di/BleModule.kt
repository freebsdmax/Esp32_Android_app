package com.example.androidplayer.di

import android.content.Context
import com.example.androidplayer.data.ble.BlePlayerClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BleModule {
    
    @Singleton
    @Provides
    fun provideBlePlayerClient(
        @ApplicationContext context: Context
    ): BlePlayerClient {
        return BlePlayerClient(context)
    }
}
