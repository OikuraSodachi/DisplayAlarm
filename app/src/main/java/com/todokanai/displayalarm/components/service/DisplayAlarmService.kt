package com.todokanai.displayalarm.components.service

import android.app.Service
import android.content.Intent
import android.hardware.display.DisplayManager
import android.media.AudioManager
import android.os.Binder
import android.os.IBinder
import androidx.core.net.toUri
import androidx.lifecycle.asLiveData
import com.todokanai.displayalarm.AlarmModel
import com.todokanai.displayalarm.notifications.Notifications
import com.todokanai.displayalarm.objects.Constants.channelID
import com.todokanai.displayalarm.objects.MyObjects.displays
import com.todokanai.displayalarm.objects.MyObjects.serviceChannel
import com.todokanai.displayalarm.repository.DataStoreRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@AndroidEntryPoint
class DisplayAlarmService : Service() {
    //알림 권한 요청 추가할것

    @Inject
    lateinit var dataStore:DataStoreRepository

    @Inject
    lateinit var audioManager: AudioManager

    @Inject
    lateinit var displayManager: DisplayManager

    private val alarmModel by lazy {AlarmModel()}
    private val binder = Binder()
    private val notifications by lazy {
        Notifications(
            service = this,
            serviceChannel = serviceChannel,
            channelID = channelID,
        )
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        displays.init(displayManager)
        notifications.createChannel(this)

        displays.isDisplayOn_setter()

        val test = combine(
            dataStore.fileUriStringFlow,
            displays.isScreenOn
        ){ path,screen ->
            return@combine Pair(path?.toUri(),screen)
        }

        test.asLiveData().observeForever { temp ->
            temp.first?.let {
                try {
                    //alarmModel.prepareFile(audioManager,it, temp.second)
                    alarmModel.prepareFileUri(this,audioManager,it,temp.second)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notifications.postNotification(this)
        return super.onStartCommand(intent, flags, startId)
    }
}