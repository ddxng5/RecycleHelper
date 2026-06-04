package com.example.recyclehelper.ui.notification

import android.Manifest
import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.data.prefs.PrefsManager
import com.example.recyclehelper.notification.NotificationHelper
import com.example.recyclehelper.ui.search.SearchViewModel
import com.example.recyclehelper.ui.theme.CardBackground
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.ScreenBackground
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary

@Composable
fun NotificationSettingsScreen(viewModel: SearchViewModel, onBack: () -> Unit) {

    BackHandler { onBack() }

    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    var permissionDenied by remember { mutableStateOf(false) }

    // Android 13+ 알림 권한 런처
    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionDenied = !granted
        if (granted) {
            viewModel.setNotifyEnabled(true)
            NotificationHelper.scheduleDaily(context, state.notifyHour, state.notifyMinute)
        }
    }

    fun requestEnableNotify() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !NotificationHelper.hasNotificationPermission(context)) {
            permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            viewModel.setNotifyEnabled(true)
            NotificationHelper.scheduleDaily(context, state.notifyHour, state.notifyMinute)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White)
            }
            Text("알림 설정", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // ── 알림 ON/OFF ─────────────────────────────────────
            SectionCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.NotificationsActive, null, tint = GreenPrimary)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("배출 알림", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                        Text(
                            if (state.notifyEnabled) "알림이 활성화되어 있습니다"
                            else "알림이 꺼져 있습니다",
                            color = TextSecondary, fontSize = 13.sp
                        )
                    }
                    Switch(
                        checked = state.notifyEnabled,
                        onCheckedChange = { enable ->
                            if (enable) requestEnableNotify()
                            else {
                                viewModel.setNotifyEnabled(false)
                                NotificationHelper.cancelDaily(context)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor   = Color.White,
                            checkedTrackColor   = GreenPrimary,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFBBBBBB),
                            uncheckedBorderColor = Color(0xFFBBBBBB)
                        )
                    )
                }

                // 권한 거부 안내
                if (permissionDenied) {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "알림 권한이 거부되었습니다. 시스템 설정에서 권한을 허용해 주세요.",
                        color = Color(0xFFE53935),
                        fontSize = 12.sp
                    )
                }
            }

            // ── 알림 시간 ────────────────────────────────────────
            SectionCard(enabled = state.notifyEnabled) {
                Text(
                    "알림 시간",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (state.notifyEnabled) TextPrimary else TextSecondary
                )
                Spacer(Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 시 선택
                    NumberSelector(
                        value = state.notifyHour,
                        label = "시",
                        range = 0..23,
                        enabled = state.notifyEnabled,
                        onDecrease = {
                            val newH = (state.notifyHour - 1 + 24) % 24
                            viewModel.setNotifyHour(newH)
                            if (state.notifyEnabled)
                                NotificationHelper.scheduleDaily(context, newH, state.notifyMinute)
                        },
                        onIncrease = {
                            val newH = (state.notifyHour + 1) % 24
                            viewModel.setNotifyHour(newH)
                            if (state.notifyEnabled)
                                NotificationHelper.scheduleDaily(context, newH, state.notifyMinute)
                        }
                    )

                    Text(
                        " : ",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (state.notifyEnabled) TextPrimary else TextSecondary
                    )

                    // 분 선택 (0, 15, 30, 45)
                    MinuteSelector(
                        value = state.notifyMinute,
                        enabled = state.notifyEnabled,
                        onSelect = { m ->
                            viewModel.setNotifyMinute(m)
                            if (state.notifyEnabled)
                                NotificationHelper.scheduleDaily(context, state.notifyHour, m)
                        }
                    )
                }
            }

            // ── 알림 시점 ────────────────────────────────────────
            SectionCard(enabled = state.notifyEnabled) {
                Text(
                    "알림 시점",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (state.notifyEnabled) TextPrimary else TextSecondary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "배출 당일 또는 전날 저녁에 알림을 받습니다",
                    fontSize = 12.sp, color = TextSecondary
                )
                Spacer(Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(0 to "당일 알림", 1 to "전날 알림").forEach { (days, label) ->
                        val selected = state.notifyAdvanceDays == days
                        Text(
                            label,
                            fontSize = 14.sp,
                            color = if (selected) Color.White else
                                if (state.notifyEnabled) GreenPrimary else TextSecondary,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    if (selected) GreenPrimary
                                    else if (state.notifyEnabled) GreenPrimary.copy(alpha = 0.08f)
                                    else Color(0xFFEEEEEE)
                                )
                                .border(
                                    1.dp,
                                    if (selected) GreenPrimary else GreenPrimary.copy(alpha = 0.3f),
                                    RoundedCornerShape(20.dp)
                                )
                                .clickable(enabled = state.notifyEnabled) {
                                    viewModel.setNotifyAdvanceDays(days)
                                }
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }
            }

            // ── 알림 받을 배출 유형 ───────────────────────────────
            SectionCard(enabled = state.notifyEnabled) {
                Text(
                    "알림 받을 배출 유형",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (state.notifyEnabled) TextPrimary else TextSecondary
                )
                Spacer(Modifier.height(8.dp))

                PrefsManager.DEFAULT_NOTIFY_TYPES.forEach { type ->
                    val checked = state.notifyWasteTypes.contains(type)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = state.notifyEnabled) {
                                viewModel.toggleNotifyWasteType(type)
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        Checkbox(
                            checked = checked,
                            onCheckedChange = {
                                if (state.notifyEnabled) viewModel.toggleNotifyWasteType(type)
                            },
                            enabled = state.notifyEnabled,
                            colors = CheckboxDefaults.colors(checkedColor = GreenPrimary)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            type,
                            fontSize = 14.sp,
                            color = if (state.notifyEnabled) TextPrimary else TextSecondary
                        )
                    }
                }
            }

            HorizontalDivider()

            // ── 테스트 알림 ───────────────────────────────────────
            SectionCard {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                !NotificationHelper.hasNotificationPermission(context)) {
                                permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else {
                                val types = state.notifyWasteTypes.sorted().joinToString(", ")
                                NotificationHelper.showNow(
                                    context   = context,
                                    title     = "🔔 테스트 알림",
                                    body      = if (types.isNotBlank()) "[$types] 배출일입니다. 앱에서 확인하세요."
                                               else "오늘 배출 일정을 앱에서 확인하세요.",
                                    targetTab = NotificationHelper.TAB_TODAY
                                )
                            }
                        }
                ) {
                    Icon(Icons.Filled.Send, null, tint = GreenPrimary)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("테스트 알림 보내기", fontWeight = FontWeight.Bold, color = TextPrimary)
                        Text("지금 바로 알림이 표시됩니다", color = TextSecondary, fontSize = 13.sp)
                    }
                }
            }

            // 현재 예약 정보 표시
            if (state.notifyEnabled) {
                val timeStr = "%02d:%02d".format(state.notifyHour, state.notifyMinute)
                val advanceStr = if (state.notifyAdvanceDays == 0) "당일" else "전날"
                Text(
                    "현재 설정: 매일 $advanceStr $timeStr 에 알림 예약됨",
                    fontSize = 12.sp,
                    color = GreenPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

// ─────────────────────────────────────────
//  공통 섹션 카드
// ─────────────────────────────────────────
@Composable
private fun SectionCard(enabled: Boolean = true, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) CardBackground else Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) { content() }
    }
}

// ─────────────────────────────────────────
//  시 선택 (− N +)
// ─────────────────────────────────────────
@Composable
private fun NumberSelector(
    value: Int,
    label: String,
    range: IntRange,
    enabled: Boolean,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(onClick = onIncrease, enabled = enabled) {
            Icon(Icons.Filled.Add, null,
                tint = if (enabled) GreenPrimary else Color.LightGray,
                modifier = Modifier.size(20.dp))
        }
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (enabled) GreenPrimary.copy(alpha = 0.1f) else Color(0xFFEEEEEE)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "%02d".format(value),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (enabled) TextPrimary else TextSecondary
            )
        }
        Text(label, fontSize = 11.sp, color = TextSecondary)
        IconButton(onClick = onDecrease, enabled = enabled) {
            Icon(Icons.Filled.Remove, null,
                tint = if (enabled) GreenPrimary else Color.LightGray,
                modifier = Modifier.size(20.dp))
        }
    }
}

// ─────────────────────────────────────────
//  분 선택 칩 (0, 15, 30, 45)
// ─────────────────────────────────────────
@Composable
private fun MinuteSelector(value: Int, enabled: Boolean, onSelect: (Int) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("분", fontSize = 11.sp, color = TextSecondary,
            modifier = Modifier.padding(bottom = 4.dp))
        listOf(0, 15, 30, 45).forEach { m ->
            val selected = value == m
            Text(
                "%02d".format(m),
                fontSize = 14.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color.White
                        else if (enabled) GreenPrimary else TextSecondary,
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (selected) GreenPrimary
                        else if (enabled) GreenPrimary.copy(alpha = 0.08f)
                        else Color(0xFFEEEEEE)
                    )
                    .clickable(enabled = enabled) { onSelect(m) }
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }
    }
}
