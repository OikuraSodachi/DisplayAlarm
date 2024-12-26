package com.todokanai.displayalarm

import com.todokanai.displayalarm.objects.Constants.HOUR_MILLI
import com.todokanai.displayalarm.objects.Constants.MIN_MILLI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

class TimeChecker @Inject constructor(
    startHourFlow:Flow<Int?>,
    startMinFlow:Flow<Int?>,
    endHourFlow:Flow<Int?>,
    endMinFlow:Flow<Int?>
) {
    private val calendarInstance = Calendar.getInstance()

    private val startTime : Flow<Long> = combine(
        startHourFlow,
        startMinFlow
    ){ hour, min ->
        (hour ?:0) * HOUR_MILLI + (min ?:0)* MIN_MILLI
    }

    private val endTime : Flow<Long> = combine(
        endHourFlow,
        endMinFlow
    ){ hour, min ->
        (hour ?:0) * HOUR_MILLI + (min ?:0)* MIN_MILLI
    }

    val isInTime : Flow<Boolean> = combine(
        startTime,
        endTime
    ){ start,end ->
        val time = calendarInstance.time.toTimeMilli()
        if(start<=time && time<=end){
            true
        }else{
            false
        }
    }

    /** hour : minute 값을 millisecond 으로 변환**/
    private fun Date.toTimeMilli():Long{
        return (this.hours*HOUR_MILLI + this.minutes*MIN_MILLI)
    }
}