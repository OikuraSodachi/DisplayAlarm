package com.todokanai.displayalarm.components.service

import android.app.Service
import android.content.Intent
import android.hardware.display.DisplayManager
import android.media.AudioManager
import android.os.Binder
import android.os.IBinder
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

    private val audioManager by lazy{getSystemService(AUDIO_SERVICE) as AudioManager}
    private val alarmModel by lazy {AlarmModel(dataStore,audioManager)}
    private val displayManager by lazy{getSystemService(DISPLAY_SERVICE) as DisplayManager}
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

        val soundFilePath = alarmModel.dataStore.filePath
        val isScreenOn = displays.isScreenOn

        val test = combine(
            soundFilePath,
            isScreenOn
        ){ path,screen ->
            return@combine Pair(path,screen)
        }

        test.asLiveData().observeForever { temp ->
            temp.first?.let {
                try {
                    alarmModel.prepareFile(it, temp.second)
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