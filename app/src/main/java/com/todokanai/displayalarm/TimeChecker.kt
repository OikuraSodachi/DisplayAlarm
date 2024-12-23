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

    val startTime : Flow<Long> = combine(
        startHourFlow,
        startMinFlow
    ){ hour, min ->
        val h = hour ?:0
        val m = min ?:0
        h* HOUR_MILLI + m* MIN_MILLI
    }

    private val endTime : Flow<Long> = combine(
        endHourFlow,
        endMinFlow
    ){ hour, min ->
        val h = hour ?:0
        val m = min ?:0
        h* HOUR_MILLI + m* MIN_MILLI
    }

    /** return current time in Long format **/
    fun time():Long{
        val date = Calendar.getInstance().time
        return date.toTimeMilli()
    }

    val isInTime : Flow<Boolean> = combine(
        startTime,
        endTime
    ){ start,end ->
        val time = Calendar.getInstance().time.toTimeMilli()
        if(start<=time && time<=end){
            true
        }else{
            false
        }
    }

    /** hour : minute 값을 millisecond 으로 변환**/
    private fun Date.toTimeMilli():Long{
        val hour = this.hours
        val min = this.minutes
        return (hour*HOUR_MILLI + min*MIN_MILLI)
    }
}