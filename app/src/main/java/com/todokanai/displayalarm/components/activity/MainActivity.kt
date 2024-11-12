package com.todokanai.displayalarm.components.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.asLiveData
import com.todokanai.displayalarm.ActivityModel
import com.todokanai.displayalarm.components.service.DisplayAlarmService
import com.todokanai.displayalarm.data.MyDataStore
import com.todokanai.displayalarm.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class MainActivity : AppCompatActivity() {

    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    private val serviceIntent by lazy  {Intent(applicationContext, DisplayAlarmService::class.java)}
    private val dataStore by lazy{MyDataStore(applicationContext)}
    private val model = ActivityModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Todo: Permission 요청 코드 작성할 것
        if(!model.isPermissionGranted()){
            model.requestPermission()
        }

        ContextCompat.startForegroundService(this,serviceIntent)
        binding.run {
            exitBtn.setOnClickListener {
                model.exit(this@MainActivity,serviceIntent)
            }
            testBtn.setOnClickListener {
                // 대충 파일 선택하기
             //   openDocumentLauncher.launch(arrayOf("audio/*"))
                CoroutineScope(Dispatchers.IO).launch {
                    dataStore.saveFilePath(
                        editText.text.toString()
                    )
                }
            }
        }
        dataStore.filePath.asLiveData().observe(this){
            it?.let {
                binding.soundFileName.text = File(it).name
            }
        }
        setContentView(binding.root)
    }
}