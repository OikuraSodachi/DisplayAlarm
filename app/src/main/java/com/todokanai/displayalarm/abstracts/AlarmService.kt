package com.todokanai.displayalarm.abstracts

import android.view.Display
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** Alarm 작동에 관한 로직 관리 layer **/
abstract class AlarmService: BaseForegroundService() {

    open val updatePeriod = 1000L
    abstract val shouldStartAlarm: StateFlow<Boolean>
    abstract val defaultDisplay: Display

    override fun onCreate() {
        super.onCreate()
        serviceScope.launch{
            shouldStartAlarm.collect{
                if(it){
                    onStartAlarm()
                }else{
                    onStopAlarm()
                }
            }
        }
        CoroutineScope(Dispatchers.Default).launch {
            while(true){
                update(defaultDisplay)
                delay(updatePeriod)
            }
        }
    }

    abstract suspend fun onStartAlarm()
    abstract suspend fun onStopAlarm()

    /** update values every second **/
    abstract fun update(defaultDisplay: Display)
}