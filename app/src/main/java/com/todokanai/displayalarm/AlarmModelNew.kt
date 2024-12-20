package com.todokanai.displayalarm

import android.content.Context
import android.hardware.display.DisplayManager
import android.media.AudioDeviceInfo
import android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.net.toUri
import com.todokanai.displayalarm.abstracts.BaseAlarmModel
import com.todokanai.displayalarm.repository.DataStoreRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class AlarmModelNew @Inject constructor(
    val dsRepo:DataStoreRepository,
    val manager:DisplayManager,
    val audioManager: AudioManager,
    val timeChecker: TimeChecker
):BaseAlarmModel() {
    val defaultDisplay = manager.displays.first()

    val timeFlowTemp = flowOf(true)

    override val isInTime: Flow<Boolean>
        get() = timeChecker.isInTime

    override val fileUri = dsRepo.fileUriStringFlow.map {
        it?.toUri()
    }

    private fun speaker(audioDevices: Array<AudioDeviceInfo>) = audioDevices.filter { it.type == TYPE_BUILTIN_SPEAKER }.first()

    val mediaPlayer = MediaPlayer().apply {
        isLooping = true
        preferredDevice =
            speaker(audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS))        // sound to speaker
    }

    val shouldStartAlarm = combine(
        isInTime,
        isDisplayOn,
        fileUri
    ){ inTime,displayOn,uri->
        return@combine Pair(inTime&&displayOn,uri)
    }

    /** == start Alarm **/
    fun prepareFileUri(context: Context, uri: Uri?, shouldStartAlarm:Boolean){
        mediaPlayer.run {
            if(shouldStartAlarm){
                uri?.let {
                    setDataSource(context, it)
                    prepare()
                    start()
                }
            }else{
                reset()
            }
        }
    }

    /** 1초마다 display 상태 체크함.
     *
     * DeX 모드 켜면 Intent.ACTION_SCREEN_ON/OFF 가 Receiver에 수신되지 않는 현상 있음
     * **/
    private fun getDisplayState():Boolean {
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
    fun startObserveDisplay(){
        CoroutineScope(Dispatchers.Default).launch {
            while(true) {
                if(getDisplayState()){
                    isDisplayOn.value = true
                }else{
                    isDisplayOn.value = false
                }
                timeChecker.timeTemp_Log()
                delay(1000)
            }
        }
    }
}