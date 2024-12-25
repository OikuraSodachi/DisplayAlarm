package com.todokanai.displayalarm.components.service

import android.net.Uri
import com.todokanai.displayalarm.AlarmModel
import com.todokanai.displayalarm.abstracts.AlarmService
import com.todokanai.displayalarm.notifications.Notifications
import com.todokanai.displayalarm.objects.Constants.CHANNEL_ID
import com.todokanai.displayalarm.objects.MyObjects.serviceChannel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DisplayAlarmService() : AlarmService() {
    //알림 권한 요청 추가할것

    @Inject
    lateinit var alarmModel:AlarmModel

    private val notifications by lazy {
        Notifications(
            service = this,
            serviceChannel = serviceChannel,
            channelID = CHANNEL_ID,
        )
    }

    override val shouldStartAlarm: Flow<Pair<Boolean, Uri?>>
        get() = alarmModel.shouldStartAlarm

    override fun onCreate() {
        super.onCreate()
        alarmModel.run {
            CoroutineScope(Dispatchers.Default).launch {
                while(true){
                    checkDisplayState()
                }
            }
        }
    }

    override fun onStartAlarm(isDisplayOn: Boolean, uri: Uri?) {
        alarmModel.mediaPlayer.run {
            if (isDisplayOn) {
                if (uri == null) {
                    println("DisplayAlarmService: file uri is null")
                } else {
                    setDataSource(this@DisplayAlarmService, uri)
                    prepare()
                    start()
                }
            } else {
                reset()
            }
        }
    }

    override fun onPostNotification() {
        notifications.postNotification(this)
    }

    override fun onCreateNotificationChannel() {
        notifications.createChannel(this)
    }
}