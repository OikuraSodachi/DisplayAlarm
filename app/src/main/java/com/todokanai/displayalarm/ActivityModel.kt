package com.todokanai.displayalarm

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.todokanai.displayalarm.components.service.DisplayAlarmService.Companion.fileToPlay
import com.todokanai.displayalarm.data.MyDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import kotlin.system.exitProcess

class ActivityModel(context:Context) {

    private val dataStore = MyDataStore(context)

    fun exit(activity:Activity,serviceIntent: Intent? = null){
        ActivityCompat.finishAffinity(activity)
        serviceIntent?.let{ activity.stopService(it) }     // 서비스 종료
        System.runFinalization()
        exitProcess(0)
    }

    fun isPermissionGranted():Boolean{
        return true
    }

    fun requestPermission(){

    }

    fun saveFilePath(filePath:String){
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveFilePath(filePath)
        }
    }
    val fileName = dataStore.filePath.map{ path ->
        path?.let {
            File(it).name
        }
    }

    fun startService(context: Context,serviceIntent:Intent){
        CoroutineScope(Dispatchers.IO).launch {
            fileToPlay = dataStore.getFilePath()
        }.invokeOnCompletion {
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}