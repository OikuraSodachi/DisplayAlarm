package com.todokanai.displayalarm

import android.media.AudioDeviceInfo
import android.media.AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.lifecycle.asLiveData
import com.todokanai.displayalarm.data.MyDataStore

class AlarmModel(val dataStore: MyDataStore,val audioManager: AudioManager) {

    var fileToPlay:String? = null

    val audioDevices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)

    fun speaker(audioDevices:List<AudioDeviceInfo>):AudioDeviceInfo?{
        audioDevices.forEach{
            println(it.productName)
        }
        val size = audioDevices.size
        var result : AudioDeviceInfo? = null
        for(i in 0..size-1){
            if(audioDevices[i].type == TYPE_BUILTIN_SPEAKER){
                result = audioDevices[i]
            }
        }
       // println("result: ${result?.productName}")
        return result
    }

    val mediaPlayer = MediaPlayer().apply {
        isLooping = true
       // preferredDevice = speaker(audioDevices.toList())
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
                fileToPlay = it
                playFile(it)
            }
        }
    }

    fun soundToSpeaker() {
        mediaPlayer.preferredDevice = speaker(audioDevices.toList())
    }

    fun soundToDisplay(){

    }
}