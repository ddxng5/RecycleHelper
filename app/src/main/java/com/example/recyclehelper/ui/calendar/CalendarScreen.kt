package com.example.recyclehelper.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.data.model.Category
import com.example.recyclehelper.data.model.WasteTypeInfo
import com.example.recyclehelper.data.model.ZoneInfo
import com.example.recyclehelper.ui.search.SearchViewModel
import com.example.recyclehelper.ui.theme.CardBackground
import com.example.recyclehelper.ui.theme.GreenLight
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.ScreenBackground
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary
import com.example.recyclehelper.util.formatDays
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

// ─── 요일 표시 순서: 일 ~ 토 ───
private val WEEKDAY_LABELS = listOf("일", "월", "화", "수", "목", "금", "토")

// 일요일 → grid column 0
private fun DayOfWeek.toColumnIndex(): Int = if (this == DayOfWeek.SUNDAY) 0 else value

/**
 * 특정 날짜에 배출 가능한 폐기물 종류 목록을 반환한다.
 * (general / food / recyclable 중 해당 요일에 해당하는 항목만 포함)
 */
private fun collectionsOnDay(dow: DayOfWeek, zone: ZoneInfo): List<WasteTypeInfo> =
    listOfNotNull(
        zone.general?.takeIf { it.days.contains(dow) },
        zone.food?.takeIf { it.days.contains(dow) },
        zone.recyclable?.takeIf { it.days.contains(dow) }
    )

// ─────────────────────────────────────────
//  메인 컴포저블
// ─────────────────────────────────────────
@Composable
fun CalendarScreen(viewModel: SearchViewModel) {
    val state by viewModel.uiState.collectAsState()
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }

    val zone = state.zones.getOrNull(state.selectedZoneIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
    ) {
        // ─── 상단 헤더 ───
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            Column {
                Text(
                    "배출 달력",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    if (state.isRegionConfigured)
                        "${state.selectedCity} ${state.selectedDistrict}"
                    else
                        "지역을 설정하면 배출 일정이 표시됩니다",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = GreenPrimary)
                            Spacer(Modifier.height(12.dp))
                            Text("지역 배출 정보를 불러오는 중...", color = TextSecondary, fontSize = 13.sp)
                        }
                    }
                }

                zone == null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            state.errorMessage
                                ?: "지역 배출 정보가 없습니다.\n지역 설정 탭에서 우리 동네를 선택해 주세요.",
                            color = TextSecondary,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                else -> {
                    // ─── 권역 탭 (복수 권역일 때) ───
                    if (state.zones.size > 1) {
                        ZoneTabRow(
                            zones = state.zones,
                            selectedIndex = state.selectedZoneIndex,
                            onSelect = viewModel::selectZone
                        )
                    }

                    // ─── 범례 ───
                    Legend()

                    // ─── 월 이동 + 달력 그리드 ───
                    MonthHeader(
                        yearMonth = currentMonth,
                        onPrev = { currentMonth = currentMonth.minusMonths(1) },
                        onNext = { currentMonth = currentMonth.plusMonths(1) }
                    )

                    CalendarGrid(
                        yearMonth = currentMonth,
                        zone = zone,
                        selectedDate = selectedDate,
                        onDateClick = { selectedDate = it }
                    )

                    Spacer(Modifier.height(16.dp))

                    // ─── 선택 날짜 상세 ───
                    selectedDate?.let { date ->
                        DayDetailPanel(date = date, zone = zone, viewModel = viewModel)
                    }

                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────
//  권역 탭 (스크롤 가능)
// ─────────────────────────────────────────
@Composable
private fun ZoneTabRow(
    zones: List<ZoneInfo>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
                    .background(
                        if (selected) GreenPrimary.copy(alpha = 0.12f) else Color.Transparent
                    )
                    .border(
                        1.dp,
                        if (selected) GreenPrimary else Color.LightGray,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelect(i) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

// ─────────────────────────────────────────
//  범례
// ─────────────────────────────────────────
@Composable
private fun Legend() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        listOf(
            Category.GENERAL to "생활쓰레기",
            Category.FOOD to "음식물쓰레기",
            Category.RECYCLABLE to "재활용품"
        ).forEach { (cat, label) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(cat.color)
                )
                Text(label, fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

// ─────────────────────────────────────────
//  월 이동 헤더
// ─────────────────────────────────────────
@Composable
private fun MonthHeader(
    yearMonth: YearMonth,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        IconButton(onClick = onPrev) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "이전 달",
                tint = GreenPrimary
            )
        }
        Text(
            text = "${yearMonth.year}년 ${yearMonth.monthValue}월",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.width(140.dp),
            textAlign = TextAlign.Center
        )
        IconButton(onClick = onNext) {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "다음 달",
                tint = GreenPrimary
            )
        }
    }
}

// ─────────────────────────────────────────
//  달력 그리드
// ─────────────────────────────────────────
@Composable
private fun CalendarGrid(
    yearMonth: YearMonth,
    zone: ZoneInfo,
    selectedDate: LocalDate?,
    onDateClick: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val firstDay = yearMonth.atDay(1)
    val totalDays = yearMonth.lengthOfMonth()
    val startOffset = firstDay.dayOfWeek.toColumnIndex()   // 0(일)~6(토)
    val totalCells = startOffset + totalDays
    val rowCount = (totalCells + 6) / 7

    Column(modifier = Modifier.padding(horizontal = 12.dp)) {
        // 요일 헤더 행
        Row(modifier = Modifier.fillMaxWidth()) {
            WEEKDAY_LABELS.forEachIndexed { col, label ->
                Text(
                    text = label,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when (col) {
                        0 -> Color(0xFFE53935)   // 일요일 → 빨강
                        6 -> Color(0xFF1565C0)   // 토요일 → 파랑
                        else -> TextSecondary
                    }
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // 날짜 셀 행
        for (row in 0 until rowCount) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                for (col in 0 until 7) {
                    val dayNumber = row * 7 + col - startOffset + 1
                    if (dayNumber < 1 || dayNumber > totalDays) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val date = yearMonth.atDay(dayNumber)
                        val collections = collectionsOnDay(date.dayOfWeek, zone)
                        DayCell(
                            day = dayNumber,
                            isSelected = date == selectedDate,
                            isToday = date == today,
                            isSunday = col == 0,
                            isSaturday = col == 6,
                            collections = collections,
                            onClick = { onDateClick(date) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────
//  날짜 셀
// ─────────────────────────────────────────
@Composable
private fun DayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    isSunday: Boolean,
    isSaturday: Boolean,
    collections: List<WasteTypeInfo>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isSelected -> GreenPrimary
        isToday -> GreenLight
        else -> Color.Transparent
    }
    val textColor = when {
        isSelected -> Color.White
        isSunday -> Color(0xFFE53935)
        isSaturday -> Color(0xFF1565C0)
        else -> TextPrimary
    }

    Column(
        modifier = modifier
            .height(52.dp)
            .padding(2.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .then(
                if (isToday && !isSelected)
                    Modifier.border(1.dp, GreenPrimary, RoundedCornerShape(8.dp))
                else Modifier
            )
            .clickable { onClick() }
            .padding(horizontal = 2.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = day.toString(),
            fontSize = 13.sp,
            fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            textAlign = TextAlign.Center
        )

        if (collections.isNotEmpty()) {
            Spacer(Modifier.height(3.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                collections.take(3).forEach { info ->
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) Color.White else info.category.color
                            )
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────
//  선택 날짜 상세 패널
// ─────────────────────────────────────────
@Composable
private fun DayDetailPanel(
    date: LocalDate,
    zone: ZoneInfo,
    viewModel: SearchViewModel
) {
    val dow = date.dayOfWeek
    val collections = collectionsOnDay(dow, zone)
    val korDow = dow.getDisplayName(TextStyle.SHORT, Locale.KOREAN)
    val dateLabel = "${date.monthValue}월 ${date.dayOfMonth}일 (${korDow})"
    val isUncollected = zone.uncollectedDay.isNotBlank() &&
        formatDays(zone.uncollectedDay).contains(korDow)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // 날짜 제목
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GreenPrimary)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    dateLabel,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextPrimary
                )
            }

            Spacer(Modifier.height(12.dp))

            when {
                isUncollected -> {
                    Text(
                        "⛔ 미수거일 (${formatDays(zone.uncollectedDay)})",
                        color = Color(0xFFE53935),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                collections.isEmpty() -> {
                    Text(
                        "이 날은 배출 예정 품목이 없습니다",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                else -> {
                    collections.forEachIndexed { idx, info ->
                        val isCompleted = viewModel.isDisposed(date, info.category.label)
                        CollectionDetailRow(
                            info = info,
                            isCompleted = isCompleted,
                            onToggle = { viewModel.toggleDisposal(date, info.category.label) }
                        )
                        if (idx < collections.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = Color(0xFFEEEEEE)
                            )
                        }
                    }
                }
            }

            // 미수거일 정보가 있고, 수거 항목도 있으면 미수거 안내 추가
            if (!isUncollected && zone.uncollectedDay.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFEEEEEE))
                Spacer(Modifier.height(6.dp))
                Text(
                    "미수거일: ${formatDays(zone.uncollectedDay)}",
                    fontSize = 12.sp,
                    color = Color(0xFFE53935).copy(alpha = 0.8f)
                )
            }
        }
    }
}

// ─────────────────────────────────────────
//  배출 유형 행
// ─────────────────────────────────────────
@Composable
private fun CollectionDetailRow(
    info: WasteTypeInfo,
    isCompleted: Boolean = false,
    onToggle: () -> Unit = {}
) {
    Column {
        // 유형 헤더 + 완료 버튼
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(info.category.color)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                info.category.label,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            // 배출 완료 토글
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isCompleted) GreenPrimary else Color.White)
                    .border(1.dp, GreenPrimary, RoundedCornerShape(20.dp))
                    .clickable { onToggle() }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.Filled.CheckCircle
                                  else Icons.Outlined.RadioButtonUnchecked,
                    contentDescription = "배출 완료",
                    tint = if (isCompleted) Color.White else GreenPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    if (isCompleted) "완료" else "배출 완료",
                    fontSize = 12.sp,
                    color = if (isCompleted) Color.White else GreenPrimary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(Modifier.height(6.dp))

        // 배출 요일
        if (info.dow.isNotBlank()) {
            InfoLine(label = "배출 요일", value = formatDays(info.dow))
        }

        // 배출 시간
        if (info.timeRange != "시간 미정") {
            InfoLine(label = "배출 시간", value = info.timeRange)
        }

        // 배출 방법
        if (info.method.isNotBlank()) {
            Spacer(Modifier.height(4.dp))
            Text(
                info.method,
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Text(
            "$label  ",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.width(64.dp)
        )
        Text(
            value,
            fontSize = 13.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Medium
        )
    }
}
