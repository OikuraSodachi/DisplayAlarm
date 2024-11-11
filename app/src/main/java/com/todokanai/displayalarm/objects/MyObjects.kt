package com.todokanai.displayalarm.objects

import android.app.NotificationChannel
import android.app.NotificationManager
import com.todokanai.displayalarm.objects.Constants.channelID
import com.todokanai.displayalarm.objects.Constants.channelName

object MyObjects {
    val serviceChannel =
        NotificationChannel(
            channelID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
}