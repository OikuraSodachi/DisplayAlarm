package com.todokanai.displayalarm.components.service

import android.hardware.display.DisplayManager
import android.media.MediaPlayer
import com.todokanai.displayalarm.abstracts.AlarmService
import com.todokanai.displayalarm.notifications.Notifications
import com.todokanai.displayalarm.objects.Constants.CHANNEL_ID
import com.todokanai.displayalarm.objects.MyObjects.serviceChannel
import com.todokanai.displayalarm.repository.DataStoreRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@AndroidEntryPoint
class DisplayAlarmService : AlarmService() {
    //알림 권한 요청 추가할것

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var dsRepo:DataStoreRepository

    @Inject
    override lateinit var displayManager: DisplayManager

    private val notifications by lazy {
        Notifications(
            service = this,
            serviceChannel = serviceChannel,
            channelID = CHANNEL_ID,
            notificationManager = notificationManager
        )
    }

    override fun onCreate() {
        super.onCreate()
       // model.testModel.init(serviceScope)        // 나중에 제거할 것
    }

    override suspend fun onStartAlarm() {
        val uri = dsRepo.getFileUri()
        mediaPlayer.run{
            if (uri == null) {
                println("DisplayAlarmService: file uri is null")
            } else {
                reset()      //  attachNewPlayer called in state 16 이슈 해결
                setDataSource(this@DisplayAlarmService, uri)
                prepare()
                start()
            }
        }
    }

    override suspend fun onStopAlarm() {
        mediaPlayer.reset()
    }

    override fun onPostNotification() {
        notifications.postNotification(this)
    }

    override fun onCreateNotificationChannel() {
        notifications.createChannel(this)
    }

    override val startTimeFlow: Flow<Long>
        get() = dsRepo.startTimeFlow

    override val endTimeFlow: Flow<Long>
        get() = dsRepo.endTimeFlow
}