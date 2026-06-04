package com.example.recyclehelper.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.recyclehelper.data.prefs.PrefsManager

/**
 * 기기 재부팅 후 AlarmManager 알람을 재예약한다.
 * (재부팅하면 setRepeating 알람이 사라지므로 재등록 필요)
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        if (action != Intent.ACTION_BOOT_COMPLETED &&
            action != "android.intent.action.QUICKBOOT_POWERON") return   // 일부 Samsung 기기

        val prefs = PrefsManager(context)
        if (!prefs.notifyEnabled) return

        NotificationHelper.ensureChannel(context)
        NotificationHelper.scheduleDaily(
            context = context,
            hour    = prefs.notifyHour,
            minute  = prefs.notifyMinute
        )
    }
}
