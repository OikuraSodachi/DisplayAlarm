package com.todokanai.displayalarm.di

import android.hardware.display.DisplayManager
import android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
import android.media.AudioManager
import android.media.AudioManager.GET_DEVICES_OUTPUTS
import android.media.MediaPlayer
import com.todokanai.displayalarm.DisplayAlarmServiceModel
import com.todokanai.displayalarm.repository.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class Etc {

    @Singleton
    @Provides
    fun provideMediaPlayer(audioManager: AudioManager):MediaPlayer{
        return MediaPlayer().apply {
            isLooping = true
            preferredDevice = audioManager.getDevices(GET_DEVICES_OUTPUTS).filter{ it.type == TYPE_BUILTIN_SPEAKER}.first()  // sound to speaker
        }
    }

    @Singleton
    @Provides
    fun provideDisplayAlarmServiceModel(
        dataStoreRepository: DataStoreRepository,
        mediaPlayer: MediaPlayer,
        displayManager: DisplayManager
    ):DisplayAlarmServiceModel{
        return DisplayAlarmServiceModel(
            dataStoreRepository,
            mediaPlayer,
            displayManager
        )
    }
}