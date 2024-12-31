package com.todokanai.displayalarm.abstracts

import android.view.Display
import com.todokanai.displayalarm.objects.Constants.HOUR_MILLI
import com.todokanai.displayalarm.objects.Constants.MIN_MILLI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.util.Calendar

abstract class AlarmService: BaseForegroundService() {

    abstract val defaultDisplay: Display

    /** display 상태 instance **/
    val isDisplayOn = MutableStateFlow(false)
    val isInTime = MutableStateFlow(false)

    /** [isInTime] 업데이트 **/
    private suspend fun updateTime(){
        val startTime = getStartTime()
        val endTime = getEndTime()
        val temp = Calendar.getInstance().time
        val time = convertToMilli(temp.hours,temp.minutes)
        if(startTime<=time && time<=endTime){
            isInTime.value = true
        }else{
            isInTime.value = false
        }
    }

    /** [isDisplayOn]에 display state 값 반영 **/
    private fun onCheckDisplayState(){
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
                onCheckDisplayState()
                updateTime()
                delay(1000)
            }
        }
    }

    abstract suspend fun onStartAlarm()
    abstract suspend fun onStopAlarm()

    abstract suspend fun getStartTime():Long
    abstract suspend fun getEndTime():Long

    val shouldStartAlarm by lazy {
        combine(
            isInTime,
            isDisplayOn
        ) { inTime, displayOn ->
            return@combine inTime && displayOn
        }
    }

    /** hour : minute 값을 millisecond 단위로 변환 **/
    private fun convertToMilli(hour:Int?, minute:Int?):Long{
        return (hour ?:0)* HOUR_MILLI + (minute ?:0)* MIN_MILLI
    }
}