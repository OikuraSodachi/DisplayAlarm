package com.todokanai.displayalarm.components.service

import android.app.Notification
import android.app.NotificationChannel
import android.content.Context
import android.hardware.display.DisplayManager
import android.media.MediaPlayer
import android.view.Display
import androidx.core.app.NotificationCompat
import com.todokanai.displayalarm.R
import com.todokanai.displayalarm.abstracts.AlarmService
import com.todokanai.displayalarm.components.activity.MainActivity.Companion.mainIntent
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

    override fun generateNotification(context: Context): Notification {
        return NotificationCompat.Builder(context, notificationChannel.id)       // 알림바에 띄울 알림을 만듬
            .setContentTitle(context.getString(R.string.notification_title)) // 알림의 제목
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentText(context.getString(R.string.notification_content_text))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(mainIntent)
            .setOngoing(true)
            .build()
    }

    override val notificationChannel: NotificationChannel
        get() = serviceChannel

    override val startTimeFlow: Flow<Long>
        get() = dsRepo.startTimeFlow

    override val endTimeFlow: Flow<Long>
        get() = dsRepo.endTimeFlow

    override val defaultDisplay: Display
        get() = displayManager.displays.first()
}