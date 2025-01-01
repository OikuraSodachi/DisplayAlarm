package com.todokanai.displayalarm.abstracts

import android.hardware.display.DisplayManager
import com.todokanai.displayalarm.objects.Constants.HOUR_MILLI
import com.todokanai.displayalarm.objects.Constants.MIN_MILLI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import java.util.Calendar

abstract class AlarmService: BaseForegroundService() {

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
                currentTimeFlow.value = getCurrentTime()      // currentTimeFlow 업데이트
                delay(1000)
            }
        }
    }

    abstract val startTimeFlow: Flow<Long>
    abstract val endTimeFlow: Flow<Long>
    private val currentTimeFlow = MutableStateFlow(-1L) // 값을 -1로 지정하여, shouldStartAlarm 초기 값 false 만듬
    abstract val displayManager:DisplayManager

    /** display 상태 instance **/
    private val isDisplayOn = MutableStateFlow(false)
    private val isInTime by lazy {
        combine(
            startTimeFlow,
            endTimeFlow,
            currentTimeFlow
        ) { start,end,current ->
            return@combine start<=current && current<=end
        }
    }
    private val defaultDisplay by lazy{
        displayManager.displays.first()
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

    abstract suspend fun onStartAlarm()
    abstract suspend fun onStopAlarm()

    /** hour : minute 값을 millisecond 단위로 변환 **/
    private fun convertToMilli(hour:Int?, minute:Int?):Long{
        return (hour ?:0)* HOUR_MILLI + (minute ?:0)* MIN_MILLI
    }

    private fun getCurrentTime():Long{
        val temp = Calendar.getInstance().time
        return convertToMilli(temp.hours,temp.minutes)
    }

    private val shouldStartAlarm by lazy {
        combine(
            isInTime,
            isDisplayOn
        ) { inTime, displayOn ->
            return@combine inTime && displayOn
        }
    }
}