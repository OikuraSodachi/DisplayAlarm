package com.todokanai.displayalarm

import com.todokanai.displayalarm.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar

class TimeChecker(
    dsRepo:DataStoreRepository
) {
  //  /** 하루 = 86400초 **/
 //   val day : Long = 86400

    private val startTime : Flow<Long> = combine(
        dsRepo.startHourFlow,
        dsRepo.startMinFlow
    ){ hour, min ->
        val h = hour?:0
        val m = min?:0
        (h*3600000 + m*60000).toLong()
    }

    private val endTime : Flow<Long> = combine(
        dsRepo.endHourFlow,
        dsRepo.endMinFlow
    ){ hour, min ->
        val h = hour?:0
        val m = min?:0
        (h*3600000 + m*60000).toLong()
    }


    /** 하루 = 86400초 **/
    private val day : Long = 86400000

    /** return current time in Long format **/
    fun time():Long{
        val date = Calendar.getInstance().time
        val hour = date.hours
        val min = date.minutes
        println("hour: $hour, min: $min")
        println("test: ${date}")
        println("time: ${date.time%day}")

        val h = hour*3600000L
        val m = min*60000L
        val sum : Long = h+m
        println("sum: ${h+m}")

        return sum
    }

    fun timeTemp(){
        val date = Calendar.getInstance().time
        val hour = date.hours
        val min = date.minutes
        println("hour: $hour, min: $min")
        println("test: ${date}")
    //    println("time: ${date.time%day}")

        val h = hour*3600000
        val m = min*60000
        println("sum: ${h+m}")
    }

    val isInTime : Flow<Boolean> = combine(
        startTime,
        endTime
    ){ start,end ->
        if(start<=time() && time()<=end){
            true
        }else{
            false
        }
    }
}