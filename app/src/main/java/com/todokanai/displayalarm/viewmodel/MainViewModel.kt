package com.todokanai.displayalarm.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.todokanai.displayalarm.objects.MyObjects.permissions
import com.todokanai.displayalarm.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class MainViewModel @Inject constructor(private val dataStore:DataStoreRepository):ViewModel() {

    val startHour = (0..23).toMutableList()
    val startMin = (0..59).toMutableList()
    val endHour = (0..23).toMutableList()
    val endMin = (0..59).toMutableList()

    fun setStartHour(value:Int){
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveStartHour(value)
        }
    }
    fun setStartMin(value:Int){
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveStartMin(value)
        }
    }
    fun setEndHour(value:Int){
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveEndHour(value)
        }
    }
    fun setEndMin(value:Int){
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveEndMin(value)
        }
    }

    fun exit(activity: Activity, serviceIntent: Intent? = null){
        ActivityCompat.finishAffinity(activity)
        serviceIntent?.let{ activity.stopService(it) }     // 서비스 종료
        System.runFinalization()
        exitProcess(0)
    }

    fun isPermissionGranted(activity: Activity):Boolean{
        val permission = permissions.first()

        return ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(activity: Activity, requestCode:Int = 1111){
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

    fun startService(context: Context, serviceIntent: Intent){
     //   CoroutineScope(Dispatchers.IO).launch {
            ///fileToPlay = dataStore.getFilePath()
            //   value 방식 대신 flow 활용하기
      //  }.invokeOnCompletion {
            ContextCompat.startForegroundService(context, serviceIntent)
     //   }
    }

    fun saveTime(startHour:Int,startMin:Int,endHour:Int,endMin:Int){
     //   println("save: $startHour:$startMin ~ $endHour:$endMin")

        CoroutineScope(Dispatchers.IO).launch {
            println("${dataStore.getStartHour()}:${dataStore.getStartMin()} ~ ${dataStore.getEndHour()}:${dataStore.getEndMin()}")
        }
    }
}