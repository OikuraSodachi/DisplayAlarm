package com.todokanai.displayalarm.di

import android.hardware.display.DisplayManager
import android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
import android.media.AudioManager
import android.media.AudioManager.GET_DEVICES_OUTPUTS
import android.media.MediaPlayer
import android.view.Display
import com.todokanai.displayalarm.AlarmModel
import com.todokanai.displayalarm.TimeChecker
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
    fun provideDefaultDisplay(displayManager: DisplayManager):Display{
        return displayManager.displays.first()
    }

    @Singleton
    @Provides
    fun provideAlarmModel(timeChecker: TimeChecker, mediaPlayer: MediaPlayer, defaultDisplay: Display): AlarmModel {
        return AlarmModel(
            timeChecker.isInTime,
            mediaPlayer,
            defaultDisplay
        )
    }

    @Singleton
    @Provides
    fun provideTimeChecker(dataStoreRepository: DataStoreRepository):TimeChecker{
        return TimeChecker(
            dataStoreRepository.startHourFlow,
            dataStoreRepository.startMinFlow,
            dataStoreRepository.endHourFlow,
            dataStoreRepository.endMinFlow
        )
    }
}