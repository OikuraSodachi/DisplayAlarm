package com.todokanai.displayalarm

import android.media.AudioDeviceInfo
import android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.lifecycle.asLiveData
import com.todokanai.displayalarm.data.MyDataStore

class AlarmModel(val dataStore: MyDataStore,val audioManager: AudioManager) {

    val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

    fun speaker(audioDevices: Array<AudioDeviceInfo>) = audioDevices.filter { it.type == TYPE_BUILTIN_SPEAKER }.first()

    val mediaPlayer = MediaPlayer().apply {
        isLooping = true
    }

    /** 기기 본체 화면 켜져 있을 때의 Callback **/
    fun onDeviceScreenOn(){
        try {
            if (!mediaPlayer.isPlaying) {
                soundToSpeaker()
                mediaPlayer.run {
                    prepare()
                    start()
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun onDeviceScreenOff(){
        soundToDisplay()
        mediaPlayer.stop()
    }

    fun playFile(filePath: String){
        mediaPlayer.run {
            println("filePath: $filePath")
            setDataSource(filePath)
            prepare()
            start()
        }
    }

    fun observeSoundFile(){
        dataStore.filePath.asLiveData().observeForever { path ->
            path?.let {
                playFile(it)
            }
        }
    }

    fun soundToSpeaker() {
        mediaPlayer.preferredDevice = speaker(audioDevices)
    }

    fun soundToDisplay(){

    }
}