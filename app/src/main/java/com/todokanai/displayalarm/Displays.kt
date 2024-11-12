package com.todokanai.displayalarm

import android.hardware.display.DisplayManager
import android.view.Display
import com.todokanai.displayalarm.objects.MyObjects.displays
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class Displays() {
    companion object{
        lateinit var defaultDisplay: Display
        lateinit var manager:DisplayManager
    }

    val shouldPlay = MutableStateFlow<Boolean>(false)


    fun init(displayManager: DisplayManager){
        manager = displayManager
        defaultDisplay = manager.displays.first()
     //   println("defaultDisplay: ${defaultDisplay.name}")
    }

    private fun translateState(state:Int):Boolean{
        when(state){
            1 ->{
                return false
            }
            2->{
                return true
            }
            else ->{
                println("exception")
                return false
            }
        }
    }

    private fun isDefaultDisplayOn_td():Boolean {
        return translateState(defaultDisplay.state)
    }

    private val isDefaultDisplayOn : Boolean
        get() = isDefaultDisplayOn_td()

    /** 1초마다 display 상태 체크함.
     *
     * DeX 모드 켜면 Intent.ACTION_SCREEN_ON/OFF 가 Receiver에 수신되지 않는 현상 있음
     * **/
    fun beginObserve(){
        CoroutineScope(Dispatchers.Default).launch {
            while(true) {
                if(displays.isDefaultDisplayOn){
                    //onDeviceScreenOn()
                    shouldPlay.value = true
                }else{
                    shouldPlay.value = false
                        //onDeviceScreenOff()
                }
                /*
                while (displays.isDefaultDisplayOn) {
                    onDeviceScreenOn()
                    delay(1000)
                }
                 */
                delay(1000)
            }
        }
    }
}