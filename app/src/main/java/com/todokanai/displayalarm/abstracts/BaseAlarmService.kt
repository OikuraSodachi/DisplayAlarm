package com.todokanai.displayalarm.abstracts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

abstract class BaseAlarmService:BaseForegroundService() {

    open val updatePeriod = 1000L

    override fun onCreate() {
        super.onCreate()
        shouldStartAlarm.map{
            if(it){
                onStartAlarm()
            }else{
                onStopAlarm()
            }
        }.stateIn(
            scope = serviceScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )
        CoroutineScope(Dispatchers.Default).launch {
            while(true){
                update()
                delay(updatePeriod)
            }
        }
    }

    abstract suspend fun onStartAlarm()
    abstract suspend fun onStopAlarm()

    abstract val shouldStartAlarm: Flow<Boolean>
    abstract suspend fun update()
}