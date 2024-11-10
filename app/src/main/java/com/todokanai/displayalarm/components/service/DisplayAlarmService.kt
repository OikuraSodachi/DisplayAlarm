package com.todokanai.displayalarm.components.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.todokanai.displayalarm.notifications.Notifications
import com.todokanai.displayalarm.objects.Constants.channelID
import com.todokanai.displayalarm.objects.Constants.channelName

class DisplayAlarmService : Service() {

    private val serviceChannel by lazy {
        NotificationChannel(
            channelID,
            channelName,
            NotificationManager.IMPORTANCE_NONE             //  알림의 중요도
        )
    }

    private val notificationManager by lazy {NotificationManagerCompat.from(this)}
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
        println("service onCreate")

      //  val notification = notifications.notification(this)
       // startForeground(1,notification)

        notifications.createChannel(this)

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("service onStartCommand")
        //notificationManager.createNotificationChannel(serviceChannel)
        notifications.postNotification(this)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        println("service onDestroy")
    }
}