package com.todokanai.displayalarm.objects

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import com.todokanai.displayalarm.objects.Constants.channelID
import com.todokanai.displayalarm.objects.Constants.channelName

object MyObjects {

    /** 요청할 permission 목록. AndroidManifest에서 가져올 것 **/
    val permissions:Array<String> = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val serviceChannel =
        NotificationChannel(
            channelID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
}