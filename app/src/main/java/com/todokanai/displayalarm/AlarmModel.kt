package com.todokanai.displayalarm

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import com.todokanai.displayalarm.abstracts.BaseAlarmModel
import kotlinx.coroutines.flow.Flow

class AlarmModel() {

    private fun speaker(audioDevices: Array<AudioDeviceInfo>) = audioDevices.filter { it.type == TYPE_BUILTIN_SPEAKER }.first()

    val mediaPlayer = MediaPlayer().apply {
        isLooping = true
    }
    /*
    fun prepareFile(audioManager: AudioManager,filePath: String, isScreenOn:Boolean){
        mediaPlayer.run {
            if(isScreenOn){
                setDataSource(filePath)
                mediaPlayer.preferredDevice = speaker(audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS))        // sound to speaker
                prepare()
                start()
            }else{
                reset()
            }
        }
    }

     */

    fun prepareFileUri(context: Context, audioManager: AudioManager, uri: Uri, shouldStartAlarm:Boolean){
        mediaPlayer.run {
            if(shouldStartAlarm){
                setDataSource(context,uri)
                //setDataSource(uri)
                mediaPlayer.preferredDevice = speaker(audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS))        // sound to speaker
                prepare()
                start()
            }else{
                reset()
            }
        }
    }
}