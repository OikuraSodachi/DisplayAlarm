package com.todokanai.displayalarm.abstracts

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/** Foreground Service with single Notification Channel ([android.app.NotificationChannel]) **/
abstract class BaseForegroundService: Service() {

    private val binder = Binder()
    val notificationManager by lazy{ NotificationManagerCompat.from(this)}

    private val serviceJob = Job()

    /** [BaseForegroundService]가 존재하는 동안 유지되는 CoroutineScope **/
    val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        onCreateNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        onPostNotification()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceJob.cancel()
    }

    abstract fun onPostNotification()
    abstract fun onCreateNotificationChannel()
}