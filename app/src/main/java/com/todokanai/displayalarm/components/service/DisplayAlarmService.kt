package com.todokanai.displayalarm.components.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import com.todokanai.displayalarm.components.receiver.DisplayReceiver
import com.todokanai.displayalarm.notifications.Notifications
import com.todokanai.displayalarm.objects.Constants.channelID
import com.todokanai.displayalarm.objects.MyObjects.serviceChannel

class DisplayAlarmService : Service() {

    companion object{
        val isDisplayOn = MutableLiveData<Boolean>(false)
    }
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
        notifications.createChannel(this)

        val screenStateFilter = IntentFilter()
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON)
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(receiver,screenStateFilter)

        isDisplayOn.observeForever{
            println("isDisplayOn: $it")
        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notifications.postNotification(this)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}