package com.todokanai.displayalarm.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.Service
import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat.startForeground
import com.todokanai.displayalarm.R

class Notifications(
    val serviceChannel:NotificationChannel,
    val channelID:String,
    val notificationManager: NotificationManagerCompat
) {

    fun createChannel(service: Service){
        notificationManager.createNotificationChannel(serviceChannel)
        val notification = notification(service)
        startForeground(service,1,notification,FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED)
    }

    fun notification(context: Context): Notification {
        return NotificationCompat.Builder(context, channelID)       // 알림바에 띄울 알림을 만듬
            .setContentTitle(context.getString(R.string.notification_title)) // 알림의 제목
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentText(context.getString(R.string.notification_content_text))
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .build()
    }

    fun postNotification(context: Context){
        notificationManager.notify(1,notification(context))
    }
}