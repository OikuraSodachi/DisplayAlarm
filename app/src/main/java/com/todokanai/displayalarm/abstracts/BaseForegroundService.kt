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
    private val notificationManager by lazy{NotificationManagerCompat.from(this)}
    private val serviceCoroutineContext: CoroutineContext = Dispatchers.Default
    private val id : Int = 1

    /** [BaseForegroundService]가 존재하는 동안 유지되는 CoroutineScope
     *
     *  -> viewModelScope 에 대응(?)
     * **/
    val serviceScope = CoroutineScope(serviceCoroutineContext)

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        val type =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MANIFEST
            } else {
                0
            }

        notificationManager.createNotificationChannel(getNotificationChannel())
        ServiceCompat.startForeground(this, id, foregroundNotification(this), type)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notificationManager.notify(id,foregroundNotification())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    /** notification for the foregroundservice **/
    abstract fun foregroundNotification(
        context: Context = this,
        channelId:String = getNotificationChannel().id
    ): Notification


    abstract fun getNotificationChannel():NotificationChannel
}