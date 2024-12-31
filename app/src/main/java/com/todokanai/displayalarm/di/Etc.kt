package com.todokanai.displayalarm.di

import android.hardware.display.DisplayManager
import android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
import android.media.AudioManager
import android.media.AudioManager.GET_DEVICES_OUTPUTS
import android.media.MediaPlayer
import com.todokanai.displayalarm.DisplayAlarmServiceModel
import com.todokanai.displayalarm.TestModel
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
    fun provideTimeChecker(dataStoreRepository: DataStoreRepository):TimeChecker{
        return TimeChecker(
            dataStoreRepository.startHourFlow,
            dataStoreRepository.startMinFlow,
            dataStoreRepository.endHourFlow,
            dataStoreRepository.endMinFlow
        )
    }

    @Singleton
    @Provides
    fun provideTestModel():TestModel{
        return TestModel()
    }

    @Singleton
    @Provides
    fun provideDisplayAlarmServiceModel(
        testModel: TestModel,
        dataStoreRepository: DataStoreRepository,
        timeChecker: TimeChecker,
        mediaPlayer: MediaPlayer,
        displayManager: DisplayManager
    ):DisplayAlarmServiceModel{
        return DisplayAlarmServiceModel(
            testModel,
            dataStoreRepository,
            timeChecker,
            mediaPlayer,
            displayManager
        )
    }
}