package com.todokanai.displayalarm.abstracts

import android.net.Uri
import android.view.Display
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

abstract class BaseAlarmModel(){

    companion object{
        lateinit var defaultDisplay: Display
    }

    abstract val isInTime: Flow<Boolean>
    abstract val fileUri:Flow<Uri?>

    val isDisplayOn = MutableStateFlow(false)

}