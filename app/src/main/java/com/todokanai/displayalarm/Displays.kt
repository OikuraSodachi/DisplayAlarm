package com.todokanai.displayalarm

import android.hardware.display.DisplayManager
import android.view.Display

class Displays() {
    companion object{
        lateinit var defaultDisplay: Display
        lateinit var manager:DisplayManager
    }

    fun init(displayManager: DisplayManager){
        manager = displayManager
        defaultDisplay = manager.displays.first()
        println("defaultDisplay: ${defaultDisplay.name}")
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

    private fun isDefaultDisplayOn():Boolean {
        return translateState(defaultDisplay.state)
    }

    val isDefaultDisplayOn : Boolean
        get() = isDefaultDisplayOn()
}