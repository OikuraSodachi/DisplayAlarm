package com.todokanai.displayalarm.abstracts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

/** Todo: AlarmModel을 여기에 통합하기? **/
abstract class AlarmService(): BaseForegroundService() {

    override fun onCreate() {
        shouldStartAlarm.map{
            onStartAlarm(it)
        }.shareIn(
            scope = CoroutineScope(Dispatchers.Default),
            started = SharingStarted.Eagerly
        )
        super.onCreate()
    }

    open suspend fun onStartAlarm(isDisplayOn:Boolean){

    }
    abstract val shouldStartAlarm:Flow<Boolean>

}