package com.todokanai.displayalarm

import android.media.AudioDeviceInfo
import android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
import android.media.AudioManager
import android.media.MediaPlayer

class AlarmModel() {

    private fun speaker(audioDevices: Array<AudioDeviceInfo>) = audioDevices.filter { it.type == TYPE_BUILTIN_SPEAKER }.first()

    val mediaPlayer = MediaPlayer().apply {
        isLooping = true
    }

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
}