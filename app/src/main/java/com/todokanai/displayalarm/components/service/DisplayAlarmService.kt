package com.todokanai.displayalarm.components.service

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
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
        notifications.createChannel(this)
        alarmModel.run {
            CoroutineScope(Dispatchers.Default).launch {
                while(true){
                    checkDisplayState()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notifications.postNotification(this)
        return super.onStartCommand(intent, flags, startId)
    }
    /** == start Alarm **/
    private fun prepareFileUri(mediaPlayer : MediaPlayer, context: Context, uri: Uri?, shouldStartAlarm:Boolean) {
        mediaPlayer.run {
            if (shouldStartAlarm) {
                if (uri == null) {
                    println("DisplayAlarmService: file uri is null")
                } else {
                    setDataSource(context, uri)
                    prepare()
                    start()
                }
            } else {
                reset()
            }
        }
    }

    override fun onStartAlarm(isDisplayOn: Boolean, uri: Uri?) {
        prepareFileUri(
            alarmModel.mediaPlayer,
            this@DisplayAlarmService,
            uri,
            isDisplayOn
        )
    }
}