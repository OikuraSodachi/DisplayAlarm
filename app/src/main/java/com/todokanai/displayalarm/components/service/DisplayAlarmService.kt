package com.todokanai.displayalarm.components.service

import android.view.Display
import com.todokanai.displayalarm.DisplayAlarmServiceModel
import com.todokanai.displayalarm.abstracts.AlarmService
import com.todokanai.displayalarm.notifications.Notifications
import com.todokanai.displayalarm.objects.Constants.CHANNEL_ID
import com.todokanai.displayalarm.objects.MyObjects.serviceChannel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
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

    override val shouldStartAlarm: Flow<Boolean>
        get() = combine(
            model.timeChecker.isInTime,
            isDisplayOn
        ){ inTime,displayOn ->
            return@combine inTime&&displayOn
        }.shareIn(
            serviceScope,
            SharingStarted.Eagerly
        )

    override fun onCreate() {
        super.onCreate()
        model.testModel.init(serviceScope)        // 나중에 제거할 것
        CoroutineScope(Dispatchers.Default).launch {
            while(true){
                onCheckDisplayState()
                delay(1000)
            }
        }
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
}