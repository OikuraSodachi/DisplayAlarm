package com.todokanai.displayalarm.components.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.todokanai.displayalarm.components.service.DisplayAlarmService
import com.todokanai.displayalarm.databinding.ActivityMainBinding
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    private val serviceIntent by lazy  {Intent(applicationContext, DisplayAlarmService::class.java)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Todo: Permission 요청 코드 작성할 것
        if(!isPermissionGranted()){
            requestPermission()
        }

        ContextCompat.startForegroundService(this,serviceIntent)
        binding.exitBtn.setOnClickListener{
            exit_td(this,serviceIntent)
        }
        setContentView(binding.root)
    }

    /**
     * Application 종료 함수
     * Service가 실행중일 경우 서비스를 실행한 Intent도 입력할것
     */
    fun exit_td(activity: Activity, serviceIntent: Intent? = null){
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