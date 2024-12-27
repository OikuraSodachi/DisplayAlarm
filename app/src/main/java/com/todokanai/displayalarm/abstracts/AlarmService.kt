package com.todokanai.displayalarm.abstracts

import android.view.Display
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

abstract class AlarmService: BaseForegroundService() {

    abstract val defaultDisplay: Display

    /** display 상태 instance **/
    val isDisplayOn = MutableStateFlow(false)

    /** @return whether if [defaultDisplay] is turned on**/
    fun getDisplayState(): Boolean {
        when (defaultDisplay.state) {
            1 -> {
                return false
            }
            2 -> {
                return true
            }
            else -> {
                println("exception: state = ${defaultDisplay.state}")
                return false
            }
        }
    }

    /** [isDisplayOn]에 display state 값 반영 **/
    fun onCheckDisplayState(){
        isDisplayOn.value = getDisplayState()
    }

    override fun onCreate() {
        super.onCreate()
        shouldStartAlarm.map{
            onStartAlarm(it)
        }.shareIn(
            scope = serviceScope,
            started = SharingStarted.Eagerly
        )
    }

    abstract suspend fun onStartAlarm(isDisplayOn:Boolean)

    abstract val shouldStartAlarm:Flow<Boolean>
}