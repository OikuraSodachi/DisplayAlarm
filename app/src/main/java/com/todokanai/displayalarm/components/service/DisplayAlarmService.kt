package com.todokanai.displayalarm.components.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.asLiveData
import com.todokanai.displayalarm.AlarmModel
import com.todokanai.displayalarm.notifications.Notifications
import com.todokanai.displayalarm.objects.Constants.CHANNEL_ID
import com.todokanai.displayalarm.objects.MyObjects.serviceChannel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DisplayAlarmService : Service() {
    //알림 권한 요청 추가할것

    @Inject
    lateinit var alarmModel:AlarmModel

    private val binder = Binder()
    private val notifications by lazy {
        Notifications(
            service = this,
            serviceChannel = serviceChannel,
            channelID = CHANNEL_ID,
        )
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        notifications.createChannel(this)
        alarmModel.startObserveDisplay()

        alarmModel.shouldStartAlarm.asLiveData().observeForever{
            alarmModel.prepareFileUri(
                context = this,
                uri =it.second,
                shouldStartAlarm = it.first
            )
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notifications.postNotification(this)
        return super.onStartCommand(intent, flags, startId)
    }
}