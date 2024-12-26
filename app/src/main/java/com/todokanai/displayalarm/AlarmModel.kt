package com.todokanai.displayalarm

import android.media.MediaPlayer
import android.view.Display
import com.todokanai.displayalarm.abstracts.BaseAlarmModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AlarmModel @Inject constructor(
    val isInTime:Flow<Boolean>,
    val mediaPlayer: MediaPlayer,
    defaultDisplay: Display
):BaseAlarmModel(
    defaultDisplay
) {

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