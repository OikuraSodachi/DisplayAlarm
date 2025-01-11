package com.todokanai.displayalarm.components.activity

import android.app.Activity
import android.app.PendingIntent
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
import com.todokanai.displayalarm.di.MyApplication.Companion.appContext
import com.todokanai.displayalarm.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

/** Todo: DeX 모드 켜면 Intent.ACTION_SCREEN_ON/OFF 가 Receiver에 수신되지 않는 현상 있음 **/

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CODE_OPEN_DOCUMENT = 1
        private val mainOpenIntent = Intent(appContext, MainActivity::class.java)
        /** intent to open [MainActivity] **/
        val mainIntent = PendingIntent.getActivity(appContext,0, Intent(mainOpenIntent), PendingIntent.FLAG_IMMUTABLE)
    }

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
                //viewModel.saveFilePath(editText.text.toString())
                openFilePicker()
            }

            testBtn.setOnClickListener {
                viewModel.testBtn(this@MainActivity)
            }

            viewModel.run {
                startHourFlow.asLiveData().observe(this@MainActivity) {
                    it?.let { t ->
                        binding.startHour.setSelection(t)
                    }
                }
                startMinFlow.asLiveData().observe(this@MainActivity){
                    it?.let { t ->
                        binding.startMin.setSelection(t)
                    }
                }
                endHourFlow.asLiveData().observe(this@MainActivity){
                    it?.let { t ->
                        binding.endHour.setSelection(t)
                    }
                }
                endMinFlow.asLiveData().observe(this@MainActivity){
                    it?.let{ t ->
                        binding.endMin.setSelection(t)
                    }
                }
            }       // Todo :이거 어떻게 단순화할수 없나?

           // spinnerCase1()      // spinner item 선택 즉시 반영 방식
            spinnerCase2(binding.saveTimeBtn,android.R.layout.simple_spinner_dropdown_item)   // 별도의 save 버튼으로 반영 방식
        }
        viewModel.fileName.asLiveData().observe(this){
            binding.soundFileName.text = it
        }
        /*
        viewModel.file.asLiveData().observe(this@MainActivity){
            println("test: $it")
        }

         */
        setContentView(binding.root)
    }

    /** spinner item 선택 즉시 반영 방식 **/
    fun spinnerCase1(spinner_R: Int = android.R.layout.simple_spinner_dropdown_item){
        binding.run {
            startHour.run {
                val tempAdapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.startHourList)
                adapter = tempAdapter
                onItemSelectedListener = SpinnerListener({ position ->
                    tempAdapter.getItem(position)?.let {
                        viewModel.setStartHour(it)
                    }
                })
            }

            startMin.run {
                val tempAdapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.startMinList)
                adapter = tempAdapter
                onItemSelectedListener = SpinnerListener({ position ->
                    tempAdapter.getItem(position)?.let {
                        viewModel.setStartMin(it)
                    }
                })
            }

            endHour.run {
                val tempAdapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.endHourList)
                adapter = tempAdapter
                onItemSelectedListener = SpinnerListener({ position ->
                    tempAdapter.getItem(position)?.let {
                        viewModel.setEndHour(it)
                    }
                })
            }

            endMin.run {
                val tempAdapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.endMinList)
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
    fun spinnerCase2(
        saveButton: View,
        spinner_R:Int
    ){
        binding.run {
            startHour.adapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.startHourList)
            startMin.adapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.startMinList)
            endHour.adapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.endHourList)
            endMin.adapter = ArrayAdapter(this@MainActivity, spinner_R, viewModel.endMinList)

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

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, REQUEST_CODE_OPEN_DOCUMENT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                contentResolver.openInputStream(uri)?.use { inputStream ->
                    val fileContent = inputStream.readBytes()
                    println("파일 내용: ${fileContent.size} bytes")

                    viewModel.saveUriString(uri)
                }
            }
        }
    }
}