package com.todokanai.displayalarm.abstracts

import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow

abstract class AlarmService(): Service() {

    private val binder = Binder()
    override fun onBind(intent:Intent):IBinder{
        return binder
    }

    abstract fun onStartAlarm(isDisplayOn:Boolean, uri:Uri?)
    abstract val shouldStartAlarm:Flow<Pair<Boolean,Uri?>>

    override fun onCreate() {
        shouldStartAlarm.asLiveData().observeForever{
            onStartAlarm(it.first,it.second)
        }
        super.onCreate()
    }
}