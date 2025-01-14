package com.todokanai.displayalarm.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.todokanai.displayalarm.di.MyApplication.Companion.appContext
import com.todokanai.displayalarm.objects.MyObjects.permissions
import com.todokanai.displayalarm.repository.DataStoreRepository
import com.todokanai.displayalarm.tools.independent.getPathFromUri_td
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okio.Path.Companion.toPath
import javax.inject.Inject
import kotlin.system.exitProcess

@HiltViewModel
class MainViewModel @Inject constructor(private val dataStore:DataStoreRepository):ViewModel() {

    val startHourList = (0..23).toMutableList()
    val startMinList = (0..59).toMutableList()
    val endHourList = (0..23).toMutableList()
    val endMinList = (0..59).toMutableList()

    val startHourFlow = dataStore.startHourFlow
    val startMinFlow = dataStore.startMinFlow
    val endHourFlow = dataStore.endHourFlow
    val endMinFlow = dataStore.endMinFlow

    val enableSound = dataStore.enableSoundFlow

    /** uri에서 File 이름 추출
     *
     * 현재 비정상 작동중 **/
    val fileName = dataStore.fileUriFlow.map{
        it?.let { uri ->
            fileNameConverter(appContext, uri)
        }
    }

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

    /*
    fun isPermissionGranted(activity: Activity):Boolean{
        val permission = permissions.first()
        return ContextCompat.checkSelfPermission(activity,permission) == PackageManager.PERMISSION_GRANTED
    }
     */

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

    fun startService(context: Context, serviceIntent: Intent){
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    fun saveTime(startHour:Int,startMin:Int,endHour:Int,endMin:Int){
        println("save: $startHour:$startMin ~ $endHour:$endMin")
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.run{
                saveStartHour(startHour)
                saveStartMin(startMin)
                saveEndHour(endHour)
                saveEndMin(endMin)
            }
        }
    }

    fun saveUriString(uri: Uri){
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveFileUri(uri)
        }
    }

    /** uri에서 File 이름 추출 **/
    private fun fileNameConverter(context: Context,uri:Uri):String?{
        val out= getPathFromUri_td(context,uri)?.toPath()
        return out?.name
    }

    fun soundSwitch(value:Boolean){
        println("enable sound save: $value")
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.saveEnableSound(value)
        }
    }

    fun testBtn(context: Context){
    }
}