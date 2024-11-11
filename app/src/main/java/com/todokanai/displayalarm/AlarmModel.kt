package com.todokanai.displayalarm

import android.media.MediaPlayer
import java.io.File

class AlarmModel {

    val mediaPlayer = MediaPlayer().apply {
        isLooping = true
    }

    /** 기기 본체 화면 켜져 있을 때의 Callback **/
    fun onDeviceScreenOn(){
        println("!!!!!!!!!!!!!!!!!!")
    }

    fun setSoundFile(file: File){
        try {
            mediaPlayer.run {
                setDataSource(file.absolutePath)
                prepare()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun stopAlarm(){
        mediaPlayer.stop()
    }

    fun startAlarm(){
        mediaPlayer.start()
    }

}