package com.todokanai.displayalarm.abstracts

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/** Foreground Service with single Notification Channel ([android.app.NotificationChannel]) **/
abstract class BaseForegroundService: Service() {

    private val binder = Binder()
    val notificationManager by lazy{ NotificationManagerCompat.from(this)}

    open val serviceContext:CoroutineContext = Dispatchers.Default

    private val _serviceContext
        get() = serviceContext
    /** [BaseForegroundService]가 존재하는 동안 유지되는 CoroutineScope **/
    val serviceScope = CoroutineScope(_serviceContext)

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
        serviceContext.cancel()
    }

    abstract fun onPostNotification()
    abstract fun onCreateNotificationChannel()
}