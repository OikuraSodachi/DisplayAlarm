package com.todokanai.displayalarm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel

/** 나중에 제거할 것 **/
class TestModel {

    lateinit var serviceCoroutineScope : CoroutineScope

    fun init(serviceScope: CoroutineScope){
        serviceCoroutineScope = serviceScope
    }

    fun test(){
        serviceCoroutineScope.cancel()
    }
}