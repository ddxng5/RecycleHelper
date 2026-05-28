package com.example.recyclehelper.ui.today

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.data.model.WasteTypeInfo
import com.example.recyclehelper.data.model.ZoneInfo
import com.example.recyclehelper.ui.components.RegionPickerDialog
import com.example.recyclehelper.ui.search.SearchViewModel
import com.example.recyclehelper.ui.theme.CardBackground
import com.example.recyclehelper.ui.theme.GreenLight
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun TodayScreen(viewModel: SearchViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showRegionPicker by remember { mutableStateOf(false) }
    val today = LocalDate.now().dayOfWeek
    val todayLabel = today.getDisplayName(TextStyle.FULL, Locale.KOREAN)
    val zone = uiState.zones.getOrNull(uiState.selectedZoneIndex)
    var detailTitle by remember { mutableStateOf<String?>(null) }
    var detailBody by remember { mutableStateOf("") }

    if (showRegionPicker) {
        RegionPickerDialog(
            currentCity = uiState.selectedCity,
            currentDistrict = uiState.selectedDistrict,
            regions = uiState.availableRegions,
            onDismiss = { showRegionPicker = false },
            onConfirm = { city, district ->
                viewModel.updateRegion(city, district)
                showRegionPicker = false
            }
        )
    }

    detailTitle?.let { title ->
        DetailDialog(
            title = title,
            body = detailBody,
            onDismiss = { detailTitle = null }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        RegionHeader(
            city = uiState.selectedCity,
            district = uiState.selectedDistrict,
            onClick = { showRegionPicker = true }
        )
        Spacer(Modifier.height(12.dp))

        if (uiState.isLoading) {
            CircularProgressIndicator(
                color = GreenPrimary,
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.CenterHorizontally)
            )
            return@Column
        }

        if (zone == null) {
            Text(
                uiState.errorMessage ?: "데이터가 없습니다",
                color = TextSecondary,
                modifier = Modifier.padding(top = 16.dp)
            )
            return@Column
        }

        ZoneSummary(zone)
        ZoneTabs(
            zones = uiState.zones,
            selectedIndex = uiState.selectedZoneIndex,
            onSelect = viewModel::selectZone
        )
        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GreenLight),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarMonth, null, tint = GreenPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("오늘 배출 가능한 쓰레기", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = TextPrimary)
                }
                Text(todayLabel, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))
                Spacer(Modifier.height(16.dp))

                val todayItems = listOfNotNull(zone.general, zone.food, zone.recyclable)
                    .filter { it.isToday(today) }

                if (todayItems.isEmpty()) {
                    Text("오늘은 배출 가능한 항목이 없어요", color = TextSecondary)
                } else {
                    todayItems.forEach { info ->
                        TodayWasteRow(info)
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        MethodCard(
            zone = zone,
            onDetail = { title, body ->
                detailTitle = title
                detailBody = body
            }
        )
        Spacer(Modifier.height(12.dp))
        BulkCard(
            zone = zone,
            onDetail = { title, body ->
                detailTitle = title
                detailBody = body
            }
        )
        Spacer(Modifier.height(12.dp))
        ContactCard(zone)
    }
}

@Composable
private fun RegionHeader(city: String, district: String, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(GreenPrimary.copy(alpha = 0.08f))
            .border(1.dp, GreenPrimary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(Icons.Filled.LocationOn, null, tint = GreenPrimary, modifier = Modifier.size(14.dp))
        Spacer(Modifier.width(4.dp))
        Text("$city $district", fontSize = 13.sp, color = GreenPrimary)
        Spacer(Modifier.width(6.dp))
        Icon(Icons.Filled.Edit, contentDescription = "지역 변경", tint = GreenPrimary, modifier = Modifier.size(12.dp))
    }
}

@Composable
private fun ZoneSummary(zone: ZoneInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("배출권역", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(
                zone.zoneName.ifBlank { zone.regionName },
                fontSize = 14.sp,
                color = TextPrimary
            )
            val targetAreas = zone.targetArea.toTargetAreas()
            if (targetAreas.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Text("대상 동", fontSize = 12.sp, color = TextSecondary)
                Spacer(Modifier.height(6.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    targetAreas.forEach { area -> InfoChip(area) }
                }
            }
        }
    }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun ZoneTabs(zones: List<ZoneInfo>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    if (zones.size <= 1) return
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.horizontalScroll(rememberScrollState())
    ) {
        zones.forEachIndexed { i, zone ->
            val selected = i == selectedIndex
            val label = zone.zoneName.ifBlank { "${i + 1}권역" }
            Text(
                text = label,
                fontSize = 12.sp,
                color = if (selected) GreenPrimary else TextSecondary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (selected) GreenPrimary.copy(alpha = 0.12f) else Color.Transparent)
                    .border(1.dp, if (selected) GreenPrimary else Color.LightGray, RoundedCornerShape(20.dp))
                    .clickable { onSelect(i) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun TodayWasteRow(info: WasteTypeInfo) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(info.category.color)
        )
        Text(info.category.label, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        Text(info.timeRange, fontSize = 13.sp, color = TextSecondary)
    }
}

@Composable
private fun MethodCard(zone: ZoneInfo, onDetail: (String, String) -> Unit) {
    val allTypes = listOfNotNull(zone.general, zone.food, zone.recyclable)
    if (allTypes.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("배출 방법", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
            Spacer(Modifier.height(10.dp))
            allTypes.forEach { info ->
                MethodRow(info, onDetail)
                Spacer(Modifier.height(8.dp))
            }
            if (zone.collectionType.isNotBlank() || zone.collectionPlace.isNotBlank()) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (zone.collectionType.isNotBlank()) InfoChip(zone.collectionType)
                    if (zone.collectionPlace.isNotBlank()) InfoChip(zone.collectionPlace)
                }
            }
        }
    }
}

@Composable
private fun MethodRow(info: WasteTypeInfo, onDetail: (String, String) -> Unit) {
    Column(
        modifier = Modifier.clickable {
            onDetail(
                info.category.label,
                listOf(
                    "배출 요일: ${info.dow.ifBlank { "확인 필요" }}",
                    "배출 시간: ${info.timeRange}",
                    "",
                    info.method
                ).joinToString("\n")
            )
        }
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(info.category.color)
            )
            Text(info.category.label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
            Text("${info.dow}  ${info.timeRange}", fontSize = 12.sp, color = TextSecondary)
        }
        Spacer(Modifier.height(2.dp))
        Text(info.method, fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(start = 18.dp))
    }
}

@Composable
private fun BulkCard(zone: ZoneInfo, onDetail: (String, String) -> Unit) {
    val bulk = zone.bulk ?: return
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onDetail(
                    "대형폐기물",
                    listOf(
                        "배출 시간: ${bulk.timeRange}",
                        if (bulk.place.isNotBlank()) "배출 장소: ${bulk.place}" else null,
                        "",
                        bulk.method
                    ).filterNotNull().joinToString("\n")
                )
            },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("대형폐기물", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
            if (bulk.timeRange != "시간 미정") {
                Text(bulk.timeRange, fontSize = 13.sp, color = TextSecondary)
            }
            Spacer(Modifier.height(6.dp))
            Text(bulk.method, fontSize = 13.sp, color = TextSecondary)
            if (bulk.place.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text("배출 장소: ${bulk.place}", fontSize = 13.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun DetailDialog(title: String, body: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(body, color = TextSecondary, fontSize = 14.sp) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("확인", color = GreenPrimary)
            }
        }
    )
}

@Composable
private fun ContactCard(zone: ZoneInfo) {
    if (zone.uncollectedDay.isBlank() && zone.deptTel.isBlank()) return
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (zone.uncollectedDay.isNotBlank()) {
                Text("미수거일", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                Text(zone.uncollectedDay.replace("+", ", "), fontSize = 13.sp, color = Color(0xFFE53935))
            }
            if (zone.deptTel.isNotBlank()) {
                if (zone.uncollectedDay.isNotBlank()) Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Phone, null, tint = GreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Column {
                        if (zone.deptName.isNotBlank()) Text(zone.deptName, fontSize = 13.sp, color = TextSecondary)
                        Text(zone.deptTel, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoChip(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        color = GreenPrimary,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(GreenPrimary.copy(alpha = 0.1f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}

private fun String.toTargetAreas(): List<String> {
    if (isBlank() || this == "없음") return emptyList()
    return split("+", ",", "ㆍ", "·", "/")
        .map { it.trim() }
        .filter { it.isNotBlank() }
}
