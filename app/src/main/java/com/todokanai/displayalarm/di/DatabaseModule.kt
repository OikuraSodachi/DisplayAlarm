package com.todokanai.displayalarm.di

import android.content.Context
import android.media.MediaPlayer
import android.view.Display
import androidx.core.net.toUri
import com.todokanai.displayalarm.AlarmModel
import com.todokanai.displayalarm.TimeChecker
import com.todokanai.displayalarm.data.room.MyDatabase
import com.todokanai.displayalarm.repository.DataStoreRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideMyDatabase(@ApplicationContext context: Context): MyDatabase {
        return MyDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun provideDataStoreRepository(@ApplicationContext context: Context): DataStoreRepository {
        return DataStoreRepository(context)
    }

    @Singleton
    @Provides
    fun provideAlarmModel(dataStoreRepository:DataStoreRepository,timeChecker: TimeChecker,mediaPlayer: MediaPlayer,defaultDisplay: Display):AlarmModel{
        return AlarmModel(
            dataStoreRepository.fileUriStringFlow.map{it?.toUri()},
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