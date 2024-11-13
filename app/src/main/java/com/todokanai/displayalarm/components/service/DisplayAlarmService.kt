package com.todokanai.displayalarm.components.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.hardware.display.DisplayManager
import android.media.AudioManager
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.asLiveData
import com.todokanai.displayalarm.AlarmModel
import com.todokanai.displayalarm.components.receiver.DisplayReceiver
import com.todokanai.displayalarm.data.MyDataStore
import com.todokanai.displayalarm.notifications.Notifications
import com.todokanai.displayalarm.objects.Constants.channelID
import com.todokanai.displayalarm.objects.MyObjects.displays
import com.todokanai.displayalarm.objects.MyObjects.serviceChannel

class DisplayAlarmService : Service() {
    //알림 권한 요청 추가할것
    companion object {
        var fileToPlay: String? = null
    }
    private val audioManager by lazy{getSystemService(AUDIO_SERVICE) as AudioManager}
    private val alarmModel by lazy {AlarmModel(MyDataStore(this),audioManager)}
    private val displayManager by lazy{getSystemService(DISPLAY_SERVICE) as DisplayManager}
    private val notificationManager by lazy {NotificationManagerCompat.from(this)}
    private val receiver = DisplayReceiver()
    private val binder = Binder()
    private val notifications by lazy {
        Notifications(
            serviceChannel = serviceChannel,
            channelID = channelID,
            notificationManager = notificationManager
        )
    }


    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        displays.init(displayManager)
        initReceiver(this)
        alarmModel.observeSoundFile()
        notifications.createChannel(this)

        displays.beginObserve()
        ///*
        displays.isScreenOn.asLiveData().observeForever {
            if(it){
                alarmModel.onDeviceScreenOn()
            }else{
                alarmModel.onDeviceScreenOff()
            }
        }

        // */
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notifications.postNotification(this)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    private fun initReceiver(service: Service){
        val screenStateFilter = IntentFilter()
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON)
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF)
        service.registerReceiver(receiver,screenStateFilter)
    }
}