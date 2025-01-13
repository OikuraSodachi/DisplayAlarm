package com.todokanai.displayalarm.abstracts

import android.hardware.display.DisplayManager
import android.view.Display
import com.todokanai.displayalarm.objects.Constants.HOUR_MILLI
import com.todokanai.displayalarm.objects.Constants.MIN_MILLI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

/** Alarm 작동에 관한 로직 관리 layer **/
abstract class AlarmService: BaseForegroundService() {

    /** display 상태 instance **/
    private val isDisplayOn = MutableStateFlow(false)
    private val currentTimeFlow = MutableStateFlow(-1L) // 값을 -1로 지정하여, shouldStartAlarm 초기 값 false 만듬
    open val updatePeriod = 1000L

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
            return@combine (start<=current && current<=end) && display
        }.stateIn(
            scope = serviceScope,
            started = SharingStarted.WhileSubscribed(5),
            initialValue = false
        )
    }

    /** update values every second **/
    fun update(defaultDisplay:Display) {
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
        currentTimeFlow.value = getCurrentTime(Calendar.getInstance().time)      // currentTimeFlow 업데이트
    }

    private fun getCurrentTime(date:Date):Long{
        return date.hours * HOUR_MILLI + date.minutes * MIN_MILLI
    }
}