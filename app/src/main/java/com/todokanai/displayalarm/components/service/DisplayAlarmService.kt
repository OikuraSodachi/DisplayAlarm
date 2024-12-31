package com.todokanai.displayalarm.components.service

import android.view.Display
import com.todokanai.displayalarm.DisplayAlarmServiceModel
import com.todokanai.displayalarm.abstracts.AlarmService
import com.todokanai.displayalarm.notifications.Notifications
import com.todokanai.displayalarm.objects.Constants.CHANNEL_ID
import com.todokanai.displayalarm.objects.Constants.HOUR_MILLI
import com.todokanai.displayalarm.objects.Constants.MIN_MILLI
import com.todokanai.displayalarm.objects.MyObjects.serviceChannel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DisplayAlarmService : AlarmService() {
    //알림 권한 요청 추가할것

    @Inject
    lateinit var model:DisplayAlarmServiceModel

    override val defaultDisplay: Display
        get() = model.displayManager.displays.first()

    private val notifications by lazy {
        Notifications(
            service = this,
            serviceChannel = serviceChannel,
            channelID = CHANNEL_ID,
            notificationManager = notificationManager
        )
    }

    private val mediaPlayer by lazy{model.mediaPlayer}

    override fun onCreate() {
        super.onCreate()
       // model.testModel.init(serviceScope)        // 나중에 제거할 것
    }

    override suspend fun onStartAlarm() {
        val uri = model.dsRepo.getFileUri()
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

    override suspend fun getStartTime(): Long {
        return convertToMilli(model.dsRepo.getStartHour(),model.dsRepo.getStartMin())
    }
    override suspend fun getEndTime(): Long {
        return convertToMilli(model.dsRepo.getEndHour(),model.dsRepo.getEndMin())
    }

    /** hour : minute 값을 millisecond 단위로 변환 **/
    private fun convertToMilli(hour:Int?, minute:Int?):Long{
        return (hour ?:0)* HOUR_MILLI + (minute ?:0)* MIN_MILLI
    }
}