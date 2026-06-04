package com.example.recyclehelper.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.recyclehelper.data.prefs.PrefsManager

/**
 * AlarmManager 에서 호출되는 매일 배출 알림 수신부.
 *
 * - notifyEnabled = false 면 바로 종료
 * - notifyAdvanceDays:
 *     0 = 당일 알림  → "오늘 배출일입니다"
 *     1 = 전날 알림  → "내일 배출 준비를 해두세요"
 * - notifyWasteTypes 를 읽어 알림 본문에 배출 유형 목록을 포함
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != ACTION_DAILY_REMINDER) return

        val prefs = PrefsManager(context)
        if (!prefs.notifyEnabled) return

        val city     = prefs.selectedCity.orEmpty()
        val district = prefs.selectedDistrict.orEmpty()
        val types    = prefs.notifyWasteTypes.sorted().joinToString(", ")
        val advance  = prefs.notifyAdvanceDays

        val (title, body) = if (advance == 0) {
            "오늘의 배출 알림" to buildBody(
                prefix = "오늘",
                types  = types,
                city   = city,
                district = district
            )
        } else {
            "내일 배출 알림" to buildBody(
                prefix = "내일",
                types  = types,
                city   = city,
                district = district
            )
        }

        NotificationHelper.showNow(
            context   = context,
            title     = title,
            body      = body,
            targetTab = if (advance == 0) NotificationHelper.TAB_TODAY
                        else             NotificationHelper.TAB_CALENDAR
        )
    }

    private fun buildBody(
        prefix: String, types: String, city: String, district: String
    ): String {
        val region = if (city.isNotBlank() && district.isNotBlank()) "$city $district" else ""
        val typesPart = if (types.isNotBlank()) "[$types] " else ""
        return if (region.isNotBlank()) {
            "$region — ${typesPart}${prefix} 배출일입니다. 확인해 보세요."
        } else {
            "${typesPart}${prefix} 배출일입니다. 앱에서 확인해 보세요."
        }
    }

    companion object {
        const val ACTION_DAILY_REMINDER =
            "com.example.recyclehelper.action.DAILY_REMINDER"
    }
}
