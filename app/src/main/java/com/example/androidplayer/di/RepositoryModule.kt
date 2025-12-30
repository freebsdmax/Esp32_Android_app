package com.example.androidplayer.di

import com.example.androidplayer.data.network.Esp32PlayerApi
import com.example.androidplayer.data.repository.Esp32PlayerRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {
    
    @Provides
    @Singleton
    fun provideEsp32PlayerRepository(
        api: Esp32PlayerApi
    ): Esp32PlayerRepository {
        return Esp32PlayerRepository(api)
    }
}