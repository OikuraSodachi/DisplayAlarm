package com.todokanai.displayalarm.components.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.todokanai.displayalarm.components.service.DisplayAlarmService
import com.todokanai.displayalarm.databinding.ActivityMainBinding
import com.todokanai.displayalarm.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy{ActivityMainBinding.inflate(layoutInflater)}
    private val serviceIntent by lazy  {Intent(applicationContext, DisplayAlarmService::class.java)}
    private val viewModel:MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.requestPermission(this)
        viewModel.startService(this,serviceIntent)

        binding.run {
            exitBtn.setOnClickListener {
                viewModel.exit(this@MainActivity,serviceIntent)
            }
            selectFileBtn.setOnClickListener {
                viewModel.saveFilePath(editText.text.toString())
            }

            testBtn.setOnClickListener {
                viewModel.testBtn()
            }

            val temp = ArrayAdapter<Int>(this@MainActivity,android.R.layout.simple_spinner_dropdown_item,viewModel.items)
                .apply {

                }
            startHour.adapter = temp
        }
        viewModel.fileName.asLiveData().observe(this){
            binding.soundFileName.text = it
        }
        setContentView(binding.root)
    }
}