package com.todokanai.displayalarm.abstracts

import android.net.Uri
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow

abstract class AlarmService(): BaseForegroundService() {

    override fun onCreate() {
        shouldStartAlarm.asLiveData().observeForever{
            onStartAlarm(it.first,it.second)
        }
        super.onCreate()
    }

    abstract fun onStartAlarm(isDisplayOn:Boolean, uri:Uri?)
    abstract val shouldStartAlarm:Flow<Pair<Boolean,Uri?>>
}