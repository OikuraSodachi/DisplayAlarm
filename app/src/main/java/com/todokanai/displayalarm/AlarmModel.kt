package com.todokanai.displayalarm

import android.media.MediaPlayer
import android.net.Uri
import android.view.Display
import com.todokanai.displayalarm.abstracts.BaseAlarmModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class AlarmModel @Inject constructor(
    val fileUri:Flow<Uri?>,
    val isInTime:Flow<Boolean>,
    val mediaPlayer: MediaPlayer,
    defaultDisplay: Display
):BaseAlarmModel(
    defaultDisplay
) {

    override val shouldStartAlarm: Flow<Pair<Boolean, Uri?>>
        get() = combine(
            isInTime,
            isDisplayOn,
            fileUri
        ){ inTime,displayOn,uri->
            return@combine Pair(inTime&&displayOn,uri)
        }

    /** DeX 모드 켜면 Intent.ACTION_SCREEN_ON/OFF 가 Receiver에 수신되지 않는 현상 있음 **/
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

    override suspend fun checkDisplayState() {
        super.checkDisplayState()
    }
}