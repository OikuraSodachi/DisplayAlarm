package com.todokanai.displayalarm.abstracts

import android.app.Notification
import android.app.NotificationChannel
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/** Foreground Service with single Notification Channel ([android.app.NotificationChannel]) **/
abstract class BaseForegroundService: Service() {

    private val binder = Binder()
    val notificationManager by lazy{ NotificationManagerCompat.from(this)}
    abstract val notificationChannel:NotificationChannel

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
        serviceScope.cancel()
    }

    abstract fun generateNotification(context:Context):Notification

    open fun onPostNotification(notificationManager:NotificationManagerCompat = this@BaseForegroundService.notificationManager){
        notificationManager.notify(1,generateNotification(this@BaseForegroundService))
    }

    open fun onCreateNotificationChannel(notificationManager:NotificationManagerCompat = this@BaseForegroundService.notificationManager){
        notificationManager.createNotificationChannel(notificationChannel)
        val notification = generateNotification(this@BaseForegroundService)
        val type =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
            } else {
                0
            }
        ServiceCompat.startForeground(this@BaseForegroundService, 1, notification, type)
    }
}