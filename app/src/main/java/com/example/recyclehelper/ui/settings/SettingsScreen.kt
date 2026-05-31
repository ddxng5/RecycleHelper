package com.example.recyclehelper.ui.settings

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.recyclehelper.data.auth.UserSessionManager
import com.example.recyclehelper.ui.auth.RegisterActivity
import com.example.recyclehelper.ui.components.RegionPickerDialog
import com.example.recyclehelper.ui.search.SearchViewModel
import com.example.recyclehelper.ui.theme.CardBackground
import com.example.recyclehelper.ui.theme.GreenLight
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    searchViewModel: SearchViewModel,
    onShowStats: () -> Unit = {},
    onShowNotifySettings: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val session = remember { UserSessionManager(context) }
    val uiState by searchViewModel.uiState.collectAsState()
    var showRegionPicker by remember { mutableStateOf(false) }

    if (showRegionPicker) {
        RegionPickerDialog(
            currentCity = uiState.selectedCity,
            currentDistrict = uiState.selectedDistrict,
            regions = uiState.availableRegions,
            isLoading = uiState.isRegionLoading,
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

        // 0) 사용자 정보 카드
        UserInfoCard(session = session, onLogout = onLogout, context = context)
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
                    else "기본값 (${uiState.selectedCity} ${uiState.selectedDistrict})",
                    color = TextSecondary, fontSize = 13.sp
                )
            }
            Icon(Icons.Filled.ChevronRight, null, tint = TextSecondary)
        }

        Spacer(Modifier.height(8.dp))

        // 2) 알림 설정 (상세 화면으로 이동)
        SettingCard(onClick = onShowNotifySettings) {
            Icon(Icons.Filled.NotificationsActive, null, tint = GreenPrimary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("배출 알림 설정", fontWeight = FontWeight.Bold, color = TextPrimary)
                val timeStr = "%02d:%02d".format(uiState.notifyHour, uiState.notifyMinute)
                val advStr  = if (uiState.notifyAdvanceDays == 0) "당일" else "전날"
                Text(
                    if (uiState.notifyEnabled) "켜짐 — $advStr $timeStr"
                    else "꺼짐",
                    color = if (uiState.notifyEnabled) GreenPrimary else TextSecondary,
                    fontSize = 13.sp
                )
            }
            Icon(Icons.Filled.ChevronRight, null, tint = TextSecondary)
        }

        Spacer(Modifier.height(8.dp))

        // 3) 환경 통계
        SettingCard(onClick = onShowStats) {
            Icon(Icons.Filled.BarChart, null, tint = GreenPrimary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("환경 통계", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("배출 기록·즐겨찾기·실천 점수 보기", color = TextSecondary, fontSize = 13.sp)
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

// ─────────────────────────────────────────
//  사용자 정보 카드
// ─────────────────────────────────────────
@Composable
private fun UserInfoCard(
    session: UserSessionManager,
    onLogout: () -> Unit,
    context: android.content.Context
) {
    val isGuest  = session.isGuest()
    val userId   = session.getCurrentUserId()
    val nickname = session.getCurrentNickname()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (isGuest) Color(0xFFFFF8E1) else GreenLight),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isGuest) Color(0xFFFFCC02) else GreenPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        nickname,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                    Text(
                        if (isGuest) "게스트로 이용 중" else "@$userId",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
                // 로그아웃 버튼
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, Color(0xFFDDDDDD), RoundedCornerShape(20.dp))
                        .clickable { onLogout() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Filled.Logout,
                        contentDescription = "로그아웃",
                        tint = Color(0xFFE53935),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text("로그아웃", color = Color(0xFFE53935), fontSize = 12.sp)
                }
            }

            // 게스트 전용 안내
            if (isGuest) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(Modifier.height(10.dp))
                Text(
                    "회원가입 후 지역 설정·즐겨찾기·배출 기록을 저장할 수 있습니다.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(GreenPrimary.copy(alpha = 0.1f))
                        .border(1.dp, GreenPrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable {
                            context.startActivity(
                                android.content.Intent(context, RegisterActivity::class.java)
                            )
                        }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("회원가입 하러 가기", color = GreenPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
private fun SettingCard(
    onClick: (() -> Unit)?,
    content: @Composable androidx.compose.foundation.layout.RowScope.() -> Unit
) {
    val clickMod = if (onClick != null) Modifier.clickable { onClick() } else Modifier
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(clickMod)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}
