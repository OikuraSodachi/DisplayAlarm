package com.todokanai.displayalarm

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.asLiveData
import com.todokanai.displayalarm.data.MyDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File

class AlarmModel(val context: Context) {

    val mediaPlayer = MediaPlayer().apply {
        isLooping = true
    }

    /** 기기 본체 화면 켜져 있을 때의 Callback **/
    fun onDeviceScreenOn(){
        println("!!!!!!!!!!!!!!!!!!")
    }

    fun setSoundFile(filePath: String){
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

    fun stopAlarm(){
        mediaPlayer.stop()
    }

    fun startAlarm(){
        mediaPlayer.start()
    }
    fun init(dataStore:MyDataStore){
        CoroutineScope(Dispatchers.IO).launch {
            val temp = dataStore.getFilePath()
            temp?.let {
             //   println("temp_to_file: ${it.toFile()}")
                setSoundFile(it)
                startAlarm()
            }
        }
    }
}