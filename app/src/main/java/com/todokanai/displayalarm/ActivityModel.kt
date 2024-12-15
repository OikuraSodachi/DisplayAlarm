package com.todokanai.displayalarm

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.todokanai.displayalarm.data.MyDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import kotlin.system.exitProcess

class ActivityModel(context:Context) {

    private val dataStore = MyDataStore(context)
    private val permissions:Array<String> = arrayOf(Manifest.permission.POST_NOTIFICATIONS,Manifest.permission.READ_MEDIA_AUDIO,Manifest.permission.READ_EXTERNAL_STORAGE)

    fun exit(activity:Activity,serviceIntent: Intent? = null){
        ActivityCompat.finishAffinity(activity)
        serviceIntent?.let{ activity.stopService(it) }     // 서비스 종료
        System.runFinalization()
        exitProcess(0)
    }

    fun isPermissionGranted(activity: Activity):Boolean{
        val permission = permissions.first()

        return ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity,requestCode:Int = 1111){
        ActivityCompat.requestPermissions(
            activity,
            permissions,
            requestCode
        )
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
            ///fileToPlay = dataStore.getFilePath()
         //   value 방식 대신 flow 활용하기
        }.invokeOnCompletion {
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}