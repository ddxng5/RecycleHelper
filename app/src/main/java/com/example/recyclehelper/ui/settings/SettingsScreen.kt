package com.example.recyclehelper.ui.settings

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.notification.NotificationHelper
import com.example.recyclehelper.ui.components.RegionPickerDialog
import com.example.recyclehelper.ui.search.SearchViewModel
import com.example.recyclehelper.ui.theme.CardBackground
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary

@Composable
fun SettingsScreen(searchViewModel: SearchViewModel) {
    val context = LocalContext.current
    val uiState by searchViewModel.uiState.collectAsState()
    var showRegionPicker by remember { mutableStateOf(false) }
    var pendingEnable by remember { mutableStateOf(false) }

    // Android 13+ 알림 권한 처리
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted && pendingEnable) {
            searchViewModel.setNotifyEnabled(true)
            NotificationHelper.scheduleDaily(context)
        }
        pendingEnable = false
    }

    if (showRegionPicker) {
        RegionPickerDialog(
            currentCity = uiState.selectedCity,
            currentDistrict = uiState.selectedDistrict,
            regions = uiState.availableRegions,
            onDismiss = { showRegionPicker = false },
            onConfirm = { city, district ->
                searchViewModel.updateRegion(city, district)
                showRegionPicker = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text("설정", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
        Spacer(Modifier.height(16.dp))

        // 1) 지역 설정
        SettingCard(onClick = { showRegionPicker = true }) {
            Icon(Icons.Filled.LocationOn, null, tint = GreenPrimary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("우리 동네", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    if (uiState.isRegionConfigured)
                        "${uiState.selectedCity} ${uiState.selectedDistrict}"
                    else
                        "기본값 (${uiState.selectedCity} ${uiState.selectedDistrict})",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
            Icon(Icons.Filled.ChevronRight, null, tint = TextSecondary)
        }

        Spacer(Modifier.height(8.dp))

        // 2) 알림 토글
        SettingCard(onClick = null) {
            Icon(Icons.Filled.Notifications, null, tint = GreenPrimary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("배출일 알림", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("매일 저녁 8시에 알려드려요", color = TextSecondary, fontSize = 13.sp)
            }
            Switch(
                checked = uiState.notifyEnabled,
                onCheckedChange = { enable ->
                    if (enable) {
                        // Android 13+ 권한 확인
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                            !NotificationHelper.hasNotificationPermission(context)
                        ) {
                            pendingEnable = true
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            searchViewModel.setNotifyEnabled(true)
                            NotificationHelper.scheduleDaily(context)
                        }
                    } else {
                        searchViewModel.setNotifyEnabled(false)
                        NotificationHelper.cancelDaily(context)
                    }
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        // 3) 테스트 알림 보내기
        SettingCard(onClick = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                !NotificationHelper.hasNotificationPermission(context)
            ) {
                pendingEnable = false
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                NotificationHelper.showNow(context)
            }
        }) {
            Icon(Icons.Filled.PlayArrow, null, tint = GreenPrimary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("테스트 알림 보내기", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("바로 알림이 표시됩니다", color = TextSecondary, fontSize = 13.sp)
            }
            Icon(Icons.Filled.ChevronRight, null, tint = TextSecondary)
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        // 4) 최근 검색어 관리
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.History, null, tint = GreenPrimary)
            Spacer(Modifier.width(8.dp))
            Text("최근 검색어", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            Spacer(Modifier.width(8.dp))
            Text("${uiState.recentQueries.size}개", color = TextSecondary, fontSize = 14.sp)
        }
        Spacer(Modifier.height(8.dp))

        if (uiState.recentQueries.isEmpty()) {
            Text("아직 검색 기록이 없어요", color = TextSecondary, fontSize = 13.sp)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                uiState.recentQueries.forEach { q ->
                    Text("• $q", color = TextSecondary, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { searchViewModel.clearRecentQueries() }
                    .padding(vertical = 4.dp)
            ) {
                Icon(Icons.Filled.DeleteSweep, null, tint = GreenPrimary)
                Spacer(Modifier.width(6.dp))
                Text("최근 검색어 전체 삭제", color = GreenPrimary, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun SettingCard(
    onClick: (() -> Unit)?,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val clickModifier = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(clickModifier)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}
