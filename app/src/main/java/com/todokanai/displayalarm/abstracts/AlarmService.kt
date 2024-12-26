package com.todokanai.displayalarm.abstracts

import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

abstract class AlarmService(): BaseForegroundService() {

    override fun onCreate() {
        shouldStartAlarm.map{
            onStartAlarm(it.first,it.second)
        }.shareIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Eagerly
        )
        super.onCreate()
    }

    abstract fun onStartAlarm(isDisplayOn:Boolean, uri:Uri?)
    abstract val shouldStartAlarm:Flow<Pair<Boolean,Uri?>>
}