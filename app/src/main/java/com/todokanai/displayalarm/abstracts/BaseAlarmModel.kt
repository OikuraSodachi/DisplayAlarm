package com.todokanai.displayalarm.abstracts

import android.view.Display
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseAlarmModel(
    open val defaultDisplay: Display,
){
    /** display 상태 instance **/
    val isDisplayOn = MutableStateFlow(false)

    abstract fun getDisplayState():Boolean

    /** [isDisplayOn]에 display state 값 반영 **/
    open suspend fun onCheckDisplayState(){
        isDisplayOn.value = getDisplayState()
    }
}