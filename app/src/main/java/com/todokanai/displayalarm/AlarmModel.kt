package com.todokanai.displayalarm

import android.media.AudioDeviceInfo
import android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
import android.media.AudioManager
import android.media.MediaPlayer

class AlarmModel(audioManager: AudioManager) {

    val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

    fun speaker(audioDevices: Array<AudioDeviceInfo>) = audioDevices.filter { it.type == TYPE_BUILTIN_SPEAKER }.first()

    val mediaPlayer = MediaPlayer().apply {
        isLooping = true
    }

    fun prepareFile(filePath: String, isScreenOn:Boolean){
        mediaPlayer.run {
            if(isScreenOn){
                setDataSource(filePath)
                soundToSpeaker()
                prepare()
                start()
            }else{
                reset()
            }
        }
    }

    fun soundToSpeaker() {
        mediaPlayer.preferredDevice = speaker(audioDevices)
    }
}