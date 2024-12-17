package com.todokanai.displayalarm.components.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
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

            spinnerCase1()      // spinner item 선택 즉시 반영 방식
            spinnerCase2(binding.saveTimeBtn)   // 별도의 save 버튼으로 반영 방식
        }
        viewModel.fileName.asLiveData().observe(this){
            binding.soundFileName.text = it
        }
        setContentView(binding.root)
    }


    /** spinner item 선택 즉시 반영 방식 **/
    fun spinnerCase1(){
        val spinner_R = android.R.layout.simple_spinner_dropdown_item

        binding.run {
            startHour.run {
                val tempAdapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.startHour)
                adapter = tempAdapter
                onItemSelectedListener = SpinnerListener({ position ->
                    tempAdapter.getItem(position)?.let {
                        viewModel.setStartHour(it)
                    }
                })
            }

            startMin.run {
                val tempAdapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.startMin)
                adapter = tempAdapter
                onItemSelectedListener = SpinnerListener({ position ->
                    tempAdapter.getItem(position)?.let {
                        viewModel.setStartMin(it)
                    }
                })
            }

            endHour.run {
                val tempAdapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.endHour)
                adapter = tempAdapter
                onItemSelectedListener = SpinnerListener({ position ->
                    tempAdapter.getItem(position)?.let {
                        viewModel.setEndHour(it)
                    }
                })
            }

            endMin.run {
                val tempAdapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.endMin)
                adapter = tempAdapter
                onItemSelectedListener = SpinnerListener({ position ->
                    tempAdapter.getItem(position)?.let {
                        viewModel.setEndMin(it)
                    }
                })
            }
        }
    }

    /** 별도의 save 버튼으로 반영 방식 **/
    fun spinnerCase2(saveButton: View){
        val spinner_R = android.R.layout.simple_spinner_dropdown_item
        binding.run {
            startHour.adapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.startHour)
            startMin.adapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.startMin)
            endHour.adapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.endHour)
            endMin.adapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.endMin)

            saveButton.setOnClickListener {
                viewModel.saveTime(
                    startHour = startHour.selectedItem as Int,
                    startMin = startMin.selectedItem as Int,
                    endHour = endHour.selectedItem as Int,
                    endMin = endMin.selectedItem as Int
                )
            }
        }
    }
}