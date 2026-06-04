package com.example.recyclehelper.ui.stats

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.data.local.WasteItemStore
import com.example.recyclehelper.data.model.DisposalRecord
import com.example.recyclehelper.ui.search.SearchViewModel
import com.example.recyclehelper.ui.theme.CardBackground
import com.example.recyclehelper.ui.theme.GreenLight
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.ScreenBackground
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary
import java.time.YearMonth

@Composable
fun StatsScreen(viewModel: SearchViewModel, onBack: () -> Unit) {

    BackHandler { onBack() }

    val state by viewModel.uiState.collectAsState()
    val now = remember { YearMonth.now() }
    val nowStr = now.toString()   // "2025-05"

    // ─── 이번 달 배출 기록 ───────────────────────────────────────
    val thisMonthRecords = remember(state.disposalRecords, nowStr) {
        state.disposalRecords.filter { it.date.startsWith(nowStr) }
    }

    // 카테고리별 집계
    val byType = remember(thisMonthRecords) {
        thisMonthRecords.groupBy { it.wasteType }
    }

    // ─── 품목 통계 ───────────────────────────────────────────────
    val allItems   = WasteItemStore.items
    val totalItems = allItems.size
    val byWasteGroup = remember(allItems) {
        allItems.groupBy { it.wasteGroup }.mapValues { it.value.size }
            .entries.sortedByDescending { it.value }
    }

    // ─── 환경 실천 점수 (참고용) ─────────────────────────────────
    val score = remember(thisMonthRecords, state.favorites, state.monthlySearchCount) {
        val disposalScore = thisMonthRecords.size * 10          // 완료 1회 = 10점
        val favoriteScore = minOf(state.favorites.size * 2, 20) // 즐겨찾기 최대 20점
        val searchScore   = minOf(state.monthlySearchCount, 10) // 검색 최대 10점
        minOf(disposalScore + favoriteScore + searchScore, 100)
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
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로",
                    tint = Color.White
                )
            }
            Text("환경 통계", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {

            // ── 환경 실천 점수 ──────────────────────────────────
            ScoreCard(score = score)
            Spacer(Modifier.height(16.dp))

            // ── 이번 달 배출 기록 ───────────────────────────────
            SectionCard(
                title = "이번 달 배출 완료",
                icon = Icons.Filled.CheckCircle,
                iconTint = GreenPrimary
            ) {
                if (thisMonthRecords.isEmpty()) {
                    Text(
                        "아직 기록이 없습니다.",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                } else {
                    SummaryRow("전체 완료 횟수", "${thisMonthRecords.size}회")
                    Spacer(Modifier.height(10.dp))
                    // 카테고리별
                    listOf("재활용품", "생활쓰레기", "음식물쓰레기").forEach { type ->
                        val count = byType[type]?.size ?: 0
                        if (count > 0) {
                            TypeRow(
                                label = type,
                                count = count,
                                color = typeColor(type)
                            )
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            // ── 앱 사용 현황 ─────────────────────────────────────
            SectionCard(
                title = "앱 사용 현황",
                icon = Icons.Filled.Search,
                iconTint = Color(0xFF2196F3)
            ) {
                SummaryRow("이번 달 검색 횟수", "${state.monthlySearchCount}회")
                Spacer(Modifier.height(4.dp))
                SummaryRow("즐겨찾기 품목", "${state.favorites.size}개")
            }
            Spacer(Modifier.height(12.dp))

            // ── 품목 DB 현황 ─────────────────────────────────────
            SectionCard(
                title = "품목 사전",
                icon = Icons.Filled.Inventory2,
                iconTint = Color(0xFFFF9800)
            ) {
                SummaryRow("전체 등록 품목", "${totalItems}개")
                Spacer(Modifier.height(8.dp))
                byWasteGroup.take(6).forEach { (group, cnt) ->
                    TypeRow(label = group, count = cnt, color = Color(0xFF78909C))
                    Spacer(Modifier.height(3.dp))
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─────────────────────────────────────────
//  환경 실천 점수 카드
// ─────────────────────────────────────────
@Composable
private fun ScoreCard(score: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GreenPrimary),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.EmojiEvents,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(36.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "환경 실천 점수",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 14.sp
            )
            Text(
                "$score 점",
                color = Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = Color(0xFFFFC107),
                trackColor = Color.White.copy(alpha = 0.3f)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "※ 배출 완료 기록·즐겨찾기·검색 활동을 합산한 참고용 점수입니다.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

// ─────────────────────────────────────────
//  공통 섹션 카드
// ─────────────────────────────────────────
@Composable
private fun SectionCard(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(icon, null, tint = iconTint)
                Text(title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
            }
            Spacer(Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = TextSecondary)
        Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

@Composable
private fun TypeRow(label: String, count: Int, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(label, fontSize = 13.sp, color = TextPrimary, modifier = Modifier.weight(1f))
        Text("${count}회/개", fontSize = 13.sp, color = TextSecondary)
    }
}

private fun typeColor(wasteType: String) = when (wasteType) {
    "재활용품"    -> Color(0xFF4CAF50)
    "음식물쓰레기" -> Color(0xFFFF9800)
    else          -> Color(0xFF9E9E9E)   // 생활쓰레기
}
