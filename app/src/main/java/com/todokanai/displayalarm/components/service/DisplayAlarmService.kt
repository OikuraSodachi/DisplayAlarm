package com.todokanai.displayalarm.components.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.hardware.display.DisplayManager
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import com.todokanai.displayalarm.components.receiver.DisplayReceiver
import com.todokanai.displayalarm.notifications.Notifications
import com.todokanai.displayalarm.objects.Constants.channelID
import com.todokanai.displayalarm.objects.MyObjects.displays
import com.todokanai.displayalarm.objects.MyObjects.serviceChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DisplayAlarmService : Service() {
    //알림 권한 요청 추가할것

    private val displayManager by lazy{getSystemService(DISPLAY_SERVICE) as DisplayManager}
    private val notificationManager by lazy {NotificationManagerCompat.from(this)}
    private val receiver = DisplayReceiver()
    private val binder = Binder()
    private val notifications by lazy {
        Notifications(
            serviceChannel = serviceChannel,
            channelID = channelID,
            notificationManager = notificationManager
        )
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        displays.init(displayManager)
        notifications.createChannel(this)

        val screenStateFilter = IntentFilter()
        screenStateFilter.addAction(Intent.ACTION_SCREEN_ON)
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(receiver,screenStateFilter)

        /** 1초마다 display 상태 체크함.
         *
         * DeX 모드 켜면 Intent.ACTION_SCREEN_ON/OFF 가 Receiver에 수신되지 않는 현상 있음
         * **/
        CoroutineScope(Dispatchers.Default).launch {
            while(true) {
                displays.displayState({onDeviceScreenOn()})
                delay(1000)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notifications.postNotification(this)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    /** 기기 본체 화면 켜져 있을 때의 Callback **/
    private fun onDeviceScreenOn(){
        println("!!!!!!!!!!!!!!!!!!")
    }
}