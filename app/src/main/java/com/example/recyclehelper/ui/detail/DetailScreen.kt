package com.example.recyclehelper.ui.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.data.model.RecycleItem
import com.example.recyclehelper.data.model.ZoneInfo
import com.example.recyclehelper.ui.components.CategoryChip
import com.example.recyclehelper.ui.search.SearchViewModel
import com.example.recyclehelper.ui.theme.CardBackground
import com.example.recyclehelper.ui.theme.GreenLight
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.ScreenBackground
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary
import com.example.recyclehelper.util.formatDays

@Composable
fun DetailScreen(
    item: RecycleItem,
    viewModel: SearchViewModel,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    val state by viewModel.uiState.collectAsState()
    val isFavorite = state.favorites.contains(item.id)
    val zone = state.zones.getOrNull(state.selectedZoneIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
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
            Text(
                text = item.name,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { viewModel.toggleFavorite(item) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                    contentDescription = "즐겨찾기",
                    tint = if (isFavorite) Color(0xFFFFC107) else Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    item.name,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.width(10.dp))
                CategoryChip(item.category)
            }
            if (item.wasteGroup.isNotBlank() || item.subCategory.isNotBlank() || item.scheduleType.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                MetaTable(item)
            }
            Spacer(Modifier.height(20.dp))

            if (item.isSpecialWaste) {
                SpecialWasteBanner(item)
                Spacer(Modifier.height(12.dp))
            }

            SectionCard(title = "분리배출 방법") {
                Text(item.disposalMethod, fontSize = 15.sp, color = TextPrimary)
            }
            Spacer(Modifier.height(12.dp))

            if (item.disposalLocation.isNotBlank()) {
                SectionCard(title = "배출 장소") {
                    Text(item.disposalLocation, fontSize = 15.sp, color = TextPrimary)
                }
                Spacer(Modifier.height(12.dp))
            }

            if (item.cautions.isNotEmpty()) {
                SectionCard(title = "주의사항") {
                    item.cautions.forEach { caution ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text("• ", color = GreenPrimary, fontWeight = FontWeight.Bold)
                            Text(caution, fontSize = 14.sp, color = TextSecondary)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // 우리 동네 배출 정보
            RegionInfoCard(
                city = state.selectedCity,
                district = state.selectedDistrict,
                isConfigured = state.isRegionConfigured,
                scheduleType = item.scheduleType,
                zone = zone
            )
        }
    }
}

@Composable
private fun MetaTable(item: RecycleItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (item.wasteGroup.isNotBlank()) MetaRow("대분류", item.wasteGroup)
            if (item.subCategory.isNotBlank()) MetaRow("분류", item.subCategory)
            if (item.scheduleType.isNotBlank()) MetaRow("배출 유형", item.scheduleType)
        }
    }
}

@Composable
private fun MetaRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            "$label  ",
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.width(72.dp)
        )
        Text(value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SpecialWasteBanner(item: RecycleItem) {
    val message = when (item.scheduleType) {
        "특수폐기물" -> "일반 재활용품으로 배출하지 마세요. 전용 수거함 또는 지정 장소에 배출하세요."
        "대형폐기물" -> "대형폐기물은 지자체 신고 후 배출해야 합니다."
        else -> "전용 수거함 또는 지정 장소에 배출해야 합니다."
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3F3), RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFFE53935).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Icon(Icons.Filled.Warning, null, tint = Color(0xFFE53935))
        Spacer(Modifier.width(8.dp))
        Column {
            Text(
                "특수 배출 안내",
                color = Color(0xFFE53935),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(message, color = TextPrimary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun RegionInfoCard(
    city: String,
    district: String,
    isConfigured: Boolean,
    scheduleType: String,
    zone: ZoneInfo?
) {
    val scheduleInfo = remember(zone, scheduleType) {
        if (zone == null) return@remember null
        when (scheduleType) {
            "재활용품" -> zone.recyclable
            "생활쓰레기" -> zone.general
            "음식물쓰레기" -> zone.food
            else -> null
        }
    }
    val bulkInfo = zone?.bulk?.takeIf { scheduleType == "대형폐기물" }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GreenLight),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Filled.LocationOn, null, tint = GreenPrimary)
                Text(
                    text = if (isConfigured) "현재 지역: $city $district" else "지역이 아직 설정되지 않았어요",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary
                )
            }
            Spacer(Modifier.height(8.dp))

            when {
                scheduleInfo != null -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Filled.CalendarMonth, null, tint = GreenPrimary)
                        Text("${scheduleType} 배출일", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Medium)
                    }
                    Text(
                        "요일: ${formatDays(scheduleInfo.dow)}",
                        fontSize = 13.sp, color = TextSecondary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    Text(
                        "시간: ${scheduleInfo.timeRange}",
                        fontSize = 13.sp, color = TextSecondary
                    )
                }
                bulkInfo != null -> {
                    Text(
                        "대형폐기물은 지자체 신고 후 배출해야 합니다.",
                        fontSize = 13.sp, color = TextPrimary
                    )
                    if (bulkInfo.timeRange != "시간 미정") {
                        Text("배출 시간: ${bulkInfo.timeRange}", fontSize = 13.sp, color = TextSecondary)
                    }
                    if (bulkInfo.place.isNotBlank()) {
                        Text("배출 장소: ${bulkInfo.place}", fontSize = 13.sp, color = TextSecondary)
                    }
                }
                else -> {
                    Text(
                        if (isConfigured) "이 품목은 지역 배출일과 별개로 전용 수거함을 이용하세요."
                        else "지역 설정 탭에서 우리 동네를 등록해 보세요.",
                        fontSize = 13.sp, color = TextSecondary
                    )
                }
            }
        }
    }
    Box(Modifier.fillMaxWidth().padding(bottom = 8.dp))
}

@Composable
private fun <T> remember(key1: Any?, key2: Any?, calculation: () -> T): T =
    androidx.compose.runtime.remember(key1, key2) { calculation() }
