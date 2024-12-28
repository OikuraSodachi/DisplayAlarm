package com.todokanai.displayalarm.components.service

import android.hardware.display.DisplayManager
import android.media.MediaPlayer
import android.view.Display
import com.todokanai.displayalarm.TestModel
import com.todokanai.displayalarm.TimeChecker
import com.todokanai.displayalarm.abstracts.AlarmService
import com.todokanai.displayalarm.notifications.Notifications
import com.todokanai.displayalarm.objects.Constants.CHANNEL_ID
import com.todokanai.displayalarm.objects.MyObjects.serviceChannel
import com.todokanai.displayalarm.repository.DataStoreRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DisplayAlarmService : AlarmService() {
    //알림 권한 요청 추가할것

    @Inject
    lateinit var testModel: TestModel

    @Inject
    lateinit var dsRepo:DataStoreRepository

    @Inject
    lateinit var timeChecker: TimeChecker

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var displayManager: DisplayManager

    override val defaultDisplay: Display
        get() = displayManager.displays.first()

    private val notifications by lazy {
        Notifications(
            service = this,
            serviceChannel = serviceChannel,
            channelID = CHANNEL_ID,
        )
    }

    override val shouldStartAlarm: Flow<Boolean>
        get() = combine(
            timeChecker.isInTime,
            isDisplayOn
        ){ inTime,displayOn ->
            return@combine inTime&&displayOn
        }

    override fun onCreate() {
        super.onCreate()
        testModel.init(serviceScope)        // 나중에 제거할 것
        CoroutineScope(Dispatchers.Default).launch {
            while(true){
                onCheckDisplayState()
                delay(1000)
            }
        }
    }

    override suspend fun onStartAlarm(isDisplayOn: Boolean) {
        val uri = dsRepo.getFileUri()
        mediaPlayer.run {
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