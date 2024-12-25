package com.todokanai.displayalarm.abstracts

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat

/** Foreground Service with single Notification Channel ([android.app.NotificationChannel]) **/
abstract class BaseForegroundService: Service() {

    private val binder = Binder()
    val notificationManager by lazy{ NotificationManagerCompat.from(this)}

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        onCreateNotificationChannel()
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        onPostNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    abstract fun onPostNotification()
    abstract fun onCreateNotificationChannel()
}