package com.todokanai.displayalarm.abstracts

import android.view.Display
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

abstract class AlarmService: BaseForegroundService() {

    abstract val defaultDisplay: Display

    /** display 상태 instance **/
    private val isDisplayOn = MutableStateFlow(false)
    private val isInTime = MutableStateFlow(false)

    /** [isInTime] 업데이트 **/
    private fun updateTime(startTime:Long,endTime:Long,currentTime:Long){
        if(startTime<=currentTime && currentTime<=endTime){
            isInTime.value = true
        }else{
            isInTime.value = false
        }
    }

    /** [isDisplayOn]에 display state 값 반영 **/
    private fun updateDisplayState(){
        when (defaultDisplay.state) {
            1 -> {
                isDisplayOn.value = false
            }
            2 -> {
                isDisplayOn.value = true
            }
            else -> {
                println("exception: state = ${defaultDisplay.state}")
                isDisplayOn.value = false
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        shouldStartAlarm.map{
            if(it){
                onStartAlarm()
            }else{
                onStopAlarm()
            }
        }.shareIn(
            scope = serviceScope,
            started = SharingStarted.Eagerly
        )
        CoroutineScope(Dispatchers.Default).launch {
            while(true){
                updateDisplayState()
                updateTime(getStartTime(),getEndTime(),getCurrentTime())
                delay(1000)
            }
        }
    }

    abstract suspend fun onStartAlarm()
    abstract suspend fun onStopAlarm()

    abstract suspend fun getStartTime():Long
    abstract suspend fun getEndTime():Long
    abstract fun getCurrentTime():Long

    private val shouldStartAlarm by lazy {
        combine(
            isInTime,
            isDisplayOn
        ) { inTime, displayOn ->
            return@combine inTime && displayOn
        }
    }
}