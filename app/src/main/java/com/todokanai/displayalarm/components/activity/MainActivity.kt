package com.todokanai.displayalarm.components.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.asLiveData
import com.todokanai.displayalarm.adapters.SpinnerListener
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

            val spinner_R = android.R.layout.simple_spinner_dropdown_item
            val dAdapter = ArrayAdapter(this@MainActivity,spinner_R,viewModel.startHour)
            startHour.adapter = dAdapter

            startHour.onItemSelectedListener = SpinnerListener({ position ->
                dAdapter.getItem(position)?.let { it ->
                    viewModel.setStartHour(
                        it
                    )
                }
            })



           // val temp = AdapterView.OnItemSelectedListener
            startMin.adapter = ArrayAdapter(this@MainActivity,spinner_R,viewModel.startMin)
            endHour.adapter = ArrayAdapter(this@MainActivity,spinner_R,viewModel.endHour)
            endMin.adapter = ArrayAdapter(this@MainActivity,spinner_R,viewModel.endMin)
        }
        viewModel.fileName.asLiveData().observe(this){
            binding.soundFileName.text = it
        }
        setContentView(binding.root)
    }
}