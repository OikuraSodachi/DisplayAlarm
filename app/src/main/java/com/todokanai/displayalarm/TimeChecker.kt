package com.todokanai.displayalarm

import com.todokanai.displayalarm.objects.Constants.HOUR_MILLI
import com.todokanai.displayalarm.objects.Constants.MIN_MILLI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class TimeChecker @Inject constructor(
    startHourFlow:Flow<Int?>,
    startMinFlow:Flow<Int?>,
    endHourFlow:Flow<Int?>,
    endMinFlow:Flow<Int?>
) {
    private val calendarInstance = Calendar.getInstance()

    private val startTime = combine(
        startHourFlow,
        startMinFlow
    ){ hour,min ->
        convertToMilli(hour,min)
    }

    private val endTime = combine(
        endHourFlow,
        endMinFlow
    ){ hour,min ->
        convertToMilli(hour,min)
    }

    val isInTime : Flow<Boolean> = combine(
        startTime,
        endTime
    ){ start,end ->
        val temp = calendarInstance.time
        val time = convertToMilli(temp.hours,temp.minutes)

        if(start<=time && time<=end){
            true
        }else{
            false
        }
    }

    /** hour : minute 값을 millisecond 단위로 변환 **/
    private fun convertToMilli(hour:Int?, minute:Int?):Long{
        return (hour ?:0)* HOUR_MILLI + (minute ?:0)* MIN_MILLI
    }
}