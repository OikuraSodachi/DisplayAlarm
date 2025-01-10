package com.todokanai.displayalarm.abstracts

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

abstract class BaseAlarmService: BaseForegroundService() {

    open val updatePeriod = 1000L

    private val collector = FlowCollector<Boolean>{
        if(it){
            onStartAlarm()
        }else{
            onStopAlarm()
        }
    }

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch{
            shouldStartAlarm.collect(collector)
        }
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