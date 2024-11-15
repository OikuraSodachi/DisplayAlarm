package com.todokanai.displayalarm.components.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.todokanai.displayalarm.ActivityModel
import com.todokanai.displayalarm.components.service.DisplayAlarmService
import com.todokanai.displayalarm.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    private val serviceIntent by lazy  {Intent(applicationContext, DisplayAlarmService::class.java)}
    private val model by lazy {ActivityModel(this)}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model.requestPermission(this)
        model.startService(this,serviceIntent)

        binding.run {
            exitBtn.setOnClickListener {
                model.exit(this@MainActivity,serviceIntent)
            }
            testBtn.setOnClickListener {
                model.saveFilePath(editText.text.toString())
            }
        }
        model.fileName.asLiveData().observe(this){
            binding.soundFileName.text = it
        }
        setContentView(binding.root)
    }
}