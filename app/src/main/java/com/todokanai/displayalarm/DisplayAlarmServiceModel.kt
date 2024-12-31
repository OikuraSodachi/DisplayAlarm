package com.todokanai.displayalarm

import android.hardware.display.DisplayManager
import android.media.MediaPlayer
import com.todokanai.displayalarm.objects.Constants.HOUR_MILLI
import com.todokanai.displayalarm.objects.Constants.MIN_MILLI
import com.todokanai.displayalarm.repository.DataStoreRepository
import java.util.Calendar
import javax.inject.Inject

class DisplayAlarmServiceModel @Inject constructor(
    val dsRepo:DataStoreRepository,
    val mediaPlayer: MediaPlayer,
    val displayManager: DisplayManager
) {

    suspend fun getStartTime():Long{
        return convertToMilli(dsRepo.getStartHour(),dsRepo.getStartMin())
    }

    suspend fun getEndTime():Long{
        return convertToMilli(dsRepo.getEndHour(),dsRepo.getEndMin())
    }

    fun getCurrentTime():Long{
        val temp = Calendar.getInstance().time
        return convertToMilli(temp.hours,temp.minutes)
    }

    /** hour : minute 값을 millisecond 단위로 변환 **/
    private fun convertToMilli(hour:Int?, minute:Int?):Long{
        return (hour ?:0)* HOUR_MILLI + (minute ?:0)* MIN_MILLI
    }
}