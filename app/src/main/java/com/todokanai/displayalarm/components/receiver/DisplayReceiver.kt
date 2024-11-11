package com.todokanai.displayalarm.components.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DisplayReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        when(intent.action){
            Intent.ACTION_SCREEN_ON -> {
                println("screen on")
                // 화면이 켜졌을 때 필요한 작업 수행
            }
            Intent.ACTION_SCREEN_OFF -> {
                println("screen off")
                // 화면이 꺼졌을 때 필요한 작업 수행

            }
            Intent.ACTION_USER_PRESENT -> {
                println("user present")
                // 화면이 켜져 있고, 사용자 잠금 해제 후
            }
        }
    }
}