package com.todokanai.displayalarm.abstracts

import android.net.Uri
import android.view.Display
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseAlarmModel(
    open val defaultDisplay: Display,
    open val isInTime:Flow<Boolean>
){

    val isDisplayOn = MutableStateFlow(false)

    /** 1초마다 display 상태 체크함.
     *
     * DeX 모드 켜면 Intent.ACTION_SCREEN_ON/OFF 가 Receiver에 수신되지 않는 현상 있음
     * **/
    private fun getDisplayState():Boolean {
        when (defaultDisplay.state) {
            1 -> {
                return false
            }
            2 -> {
                return true
            }
            else -> {
                println("exception")
                return false
            }
        }
    }

    abstract val shouldStartAlarm:Flow<Pair<Boolean,Uri?>>

    /** [isDisplayOn]에 display state 값 반영 **/
    open suspend fun checkDisplayState(){
        isDisplayOn.value = getDisplayState()
    }
}