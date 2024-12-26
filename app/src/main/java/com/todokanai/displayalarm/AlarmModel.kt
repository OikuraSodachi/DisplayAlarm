package com.todokanai.displayalarm

import android.media.MediaPlayer
import android.view.Display
import com.todokanai.displayalarm.abstracts.BaseAlarmModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class AlarmModel @Inject constructor(
    private val isInTime:Flow<Boolean>,
    val mediaPlayer: MediaPlayer,
    defaultDisplay: Display
):BaseAlarmModel(
    defaultDisplay
) {

    override val shouldStartAlarm:Flow<Boolean>
        get() = combine(
            isInTime,
            isDisplayOn
        ){ inTime,displayOn ->
            return@combine inTime&&displayOn
        }

    /** @return whether if [defaultDisplay] is turned on**/
    override fun getDisplayState(): Boolean {
        when (defaultDisplay.state) {
            1 -> {
                return false
            }
            2 -> {
                return true
            }
            else -> {
                println("exception")
                return false
            }
        }
    }
}