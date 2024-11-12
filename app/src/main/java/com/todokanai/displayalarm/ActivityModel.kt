package com.todokanai.displayalarm

import android.app.Activity
import android.content.Intent
import androidx.core.app.ActivityCompat
import kotlin.system.exitProcess

class ActivityModel {
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
}