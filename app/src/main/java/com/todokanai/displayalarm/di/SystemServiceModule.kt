package com.todokanai.displayalarm.di

import android.app.Service.AUDIO_SERVICE
import android.app.Service.DISPLAY_SERVICE
import android.content.Context
import android.hardware.display.DisplayManager
import android.media.AudioManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class SystemServiceModule {

    @Singleton
    @Provides
    fun provideAudioManager(@ApplicationContext context:Context):AudioManager{
        return context.getSystemService(AUDIO_SERVICE) as AudioManager
    }

    @Singleton
    @Provides
    fun provideDisplayManager(@ApplicationContext context: Context):DisplayManager{
        return context.getSystemService(DISPLAY_SERVICE) as DisplayManager
    }
}