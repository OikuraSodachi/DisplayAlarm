package com.todokanai.displayalarm.abstracts

import android.net.Uri
import android.view.Display
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseAlarmModel(
    open val defaultDisplay: Display,
){
    val isDisplayOn = MutableStateFlow(false)

    abstract fun getDisplayState():Boolean

    abstract val shouldStartAlarm:Flow<Pair<Boolean,Uri?>>

    /** [isDisplayOn]에 display state 값 반영 **/
    open suspend fun onCheckDisplayState(){
        isDisplayOn.value = getDisplayState()
    }
}