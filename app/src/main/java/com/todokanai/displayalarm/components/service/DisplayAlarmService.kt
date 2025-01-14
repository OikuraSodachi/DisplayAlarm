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
import com.todokanai.displayalarm.objects.Constants.HOUR_MILLI
import com.todokanai.displayalarm.objects.Constants.MIN_MILLI
import com.todokanai.displayalarm.objects.MyObjects.serviceChannel
import com.todokanai.displayalarm.repository.DataStoreRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@AndroidEntryPoint
class DisplayAlarmService : AlarmService() {
    //알림 권한 요청 추가할것

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    @Inject
    lateinit var dsRepo:DataStoreRepository

    @Inject
    lateinit var displayManager: DisplayManager

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch {
            dsRepo.enableSoundFlow.collect{
                if(it == false){
                    mute()
                }else{
                    unMute()
                }
            }
        }
       // model.testModel.init(serviceScope)        // 나중에 제거할
    }

    override suspend fun onStartAlarm() {
        val uri = dsRepo.getFileUri()

        mediaPlayer.run {
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

    override fun foregroundNotification(context: Context,channelId:String): Notification {
        return NotificationCompat.Builder(context, channelId)       // 알림바에 띄울 알림을 만듬
            .setContentTitle(context.getString(R.string.notification_title)) // 알림의 제목
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentText(context.getString(R.string.notification_content_text))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(mainIntent)
            .setOngoing(true)
            .build()
    }

    override fun getNotificationChannel(): NotificationChannel {
        return serviceChannel
    }

    override val defaultDisplay: Display
        get() = displayManager.displays.first()

    /** display 상태 instance **/
    private val isDisplayOn = MutableStateFlow(false)
    private val currentTimeFlow = MutableStateFlow(-1L) // 값을 -1로 지정하여, shouldStartAlarm 초기 값 false 만듬

    override val shouldStartAlarm by lazy {
        combine(
            dsRepo.startTimeFlow,
            dsRepo.endTimeFlow,
            currentTimeFlow,
            isDisplayOn
        ) { start, end, current, display ->
            return@combine (start <= current && current <= end) && display
        }.stateIn(
            scope = serviceScope,
            started = SharingStarted.WhileSubscribed(5),
            initialValue = false
        )
    }

    override val enableSound by lazy{ dsRepo.enableSoundFlow }

    override fun update(defaultDisplay: Display) {
        when (defaultDisplay.state) {
            1 -> {
                isDisplayOn.value = false
            }
            2 -> {
                isDisplayOn.value = true
            }
            else -> {
                isDisplayOn.value = false
            }
        }       // [isDisplayOn]에 display state 값 반영
        currentTimeFlow.value = getCurrentTime(Calendar.getInstance().time)      // currentTimeFlow 업데이트
    }

    private fun getCurrentTime(date: Date):Long{
        return date.hours * HOUR_MILLI + date.minutes * MIN_MILLI
    }

    override fun mute(){
        mediaPlayer.setVolume(0f,0f)
    }

    override fun unMute(){
        mediaPlayer.setVolume(1f,1f)
    }
}