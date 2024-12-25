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
    override val isInTime:Flow<Boolean>,
    val mediaPlayer: MediaPlayer,
    defaultDisplay: Display
):BaseAlarmModel(
    defaultDisplay,
    isInTime
) {

    override val shouldStartAlarm: Flow<Pair<Boolean, Uri?>>
        get() = combine(
            isInTime,
            isDisplayOn,
            fileUri
        ){ inTime,displayOn,uri->
            return@combine Pair(inTime&&displayOn,uri)
        }

    override suspend fun checkDisplayState() {
        super.checkDisplayState()
    }
}