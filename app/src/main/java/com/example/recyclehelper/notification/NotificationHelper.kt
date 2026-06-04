package com.example.recyclehelper.notification

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.recyclehelper.MainActivity
import java.util.Calendar

/**
 * 배출 알림 헬퍼.
 *
 *  - 알림 채널 생성
 *  - 즉시 알림 (테스트용)
 *  - 매일 반복 알림 예약 / 취소 (inexact — SCHEDULE_EXACT_ALARM 불필요)
 *  - 알림 클릭 → MainActivity + TODAY 탭
 */
object NotificationHelper {

    const val CHANNEL_ID   = "recycle_daily_reminder"
    const val EXTRA_TARGET_TAB = "extra_target_tab"
    const val TAB_TODAY    = "TODAY"
    const val TAB_CALENDAR = "CALENDAR"

    private const val CHANNEL_NAME      = "배출 알림"
    private const val NOTIFICATION_ID   = 1001
    private const val ALARM_REQUEST_CODE = 2001

    // ── 채널 ────────────────────────────────────────────────────
    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (mgr.getNotificationChannel(CHANNEL_ID) != null) return
            mgr.createNotificationChannel(
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
                    .apply { description = "오늘의 분리배출 안내 알림" }
            )
        }
    }

    // ── 권한 확인 ────────────────────────────────────────────────
    fun hasNotificationPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    // ── 즉시 알림 (테스트) ───────────────────────────────────────
    fun showNow(
        context: Context,
        title: String = "오늘의 배출",
        body: String  = "오늘 배출 가능한 항목을 확인해 보세요",
        targetTab: String = TAB_TODAY
    ) {
        ensureChannel(context)
        if (!hasNotificationPermission(context)) return
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_menu_recent_history)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setAutoCancel(true)
            .setContentIntent(buildTapIntent(context, targetTab))
            .build()
        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) { /* 권한 없음 — UI에서 안내 */ }
    }

    // ── 반복 알림 예약 ───────────────────────────────────────────
    /**
     * @param hour        알림 시(0-23)
     * @param minute      알림 분(0-59)
     * AlarmManager.setRepeating — inexact, 배터리 친화적.
     * SCHEDULE_EXACT_ALARM 권한은 불필요.
     */
    fun scheduleDaily(context: Context, hour: Int, minute: Int) {
        val triggerAt = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) add(Calendar.DAY_OF_YEAR, 1)
        }.timeInMillis

        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setRepeating(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            AlarmManager.INTERVAL_DAY,
            buildAlarmPendingIntent(context)
        )
    }

    fun cancelDaily(context: Context) {
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            .cancel(buildAlarmPendingIntent(context))
    }

    // ── 내부 헬퍼 ────────────────────────────────────────────────
    private fun buildTapIntent(context: Context, targetTab: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TARGET_TAB, targetTab)
        }
        return PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun buildAlarmPendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = AlarmReceiver.ACTION_DAILY_REMINDER
        }
        return PendingIntent.getBroadcast(
            context, ALARM_REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
