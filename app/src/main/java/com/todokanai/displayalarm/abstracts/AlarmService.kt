package com.todokanai.displayalarm.abstracts

import android.hardware.display.DisplayManager
import android.view.Display
import com.todokanai.displayalarm.objects.Constants.HOUR_MILLI
import com.todokanai.displayalarm.objects.Constants.MIN_MILLI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

abstract class AlarmService: BaseForegroundService() {

    /** display 상태 instance **/
    private val isDisplayOn = MutableStateFlow(false)
    private val currentTimeFlow = MutableStateFlow(-1L) // 값을 -1로 지정하여, shouldStartAlarm 초기 값 false 만듬
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



    abstract val startTimeFlow: Flow<Long>
    abstract val endTimeFlow: Flow<Long>
    abstract val displayManager:DisplayManager
    abstract val defaultDisplay: Display

    abstract suspend fun onStartAlarm()
    abstract suspend fun onStopAlarm()

    val shouldStartAlarm by lazy{
        combine(
            startTimeFlow,
            endTimeFlow,
            currentTimeFlow,
            isDisplayOn
        ){ start,end,current,display ->
            return@combine isInTime(start, current, end) && display
        }.stateIn(
            scope = serviceScope,
            started = SharingStarted.WhileSubscribed(5),
            initialValue = false
        )
    }

    /** update values every second **/
    suspend fun update() {
        when (defaultDisplay.state) {
            1 -> {
                isDisplayOn.value = false
            }
            2 -> {
                isDisplayOn.value = true
            }
            else -> {
                isDisplayOn.value = false
            }
        }       // [isDisplayOn]에 display state 값 반영
        currentTimeFlow.value = getCurrentTime()      // currentTimeFlow 업데이트
    }

    /** hour : minute 값을 millisecond 단위로 변환 **/
    private fun convertToMilli(hour:Int?, minute:Int?):Long{
        return (hour ?:0)* HOUR_MILLI + (minute ?:0)* MIN_MILLI
    }

    private fun getCurrentTime():Long{
        val temp = Calendar.getInstance().time
        return convertToMilli(temp.hours,temp.minutes)
    }

    /** 시간 조건 **/
    private fun isInTime(start:Long,current:Long,end:Long):Boolean{
        return start<=current && current<=end
    }
}