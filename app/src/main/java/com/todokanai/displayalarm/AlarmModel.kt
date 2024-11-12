package com.todokanai.displayalarm

import android.media.MediaPlayer

class AlarmModel() {

    val mediaPlayer = MediaPlayer().apply {
        isLooping = true
    }

    /** 기기 본체 화면 켜져 있을 때의 Callback **/
    fun onDeviceScreenOn(){
    //    println("!!!!!!!!!!!!!!!!!!")
        if(!mediaPlayer.isPlaying){
            mediaPlayer.run {
                prepare()
                start()
            }
        }
    }

    fun onDeviceScreenOff(){
        mediaPlayer.stop()
    }

    private fun setSoundFile(filePath: String){
        try {
            mediaPlayer.run {
                println("filePath: $filePath")
                setDataSource(filePath)
                prepare()
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    fun init(fileToPlay:String?){
        fileToPlay?.let {
            setSoundFile(it)
        }
    }
}