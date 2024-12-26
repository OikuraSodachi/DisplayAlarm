package com.todokanai.displayalarm.abstracts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

abstract class AlarmService(): BaseForegroundService() {

    override fun onCreate() {
        super.onCreate()
        shouldStartAlarm.map{
            onStartAlarm(it)
        }.shareIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Eagerly
        )
    }

    abstract suspend fun onStartAlarm(isDisplayOn:Boolean)

    abstract val shouldStartAlarm:Flow<Boolean>

}