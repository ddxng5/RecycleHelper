package com.example.recyclehelper.ui.search

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.data.model.RecycleItem
import com.example.recyclehelper.data.model.WasteCategory
import com.example.recyclehelper.ui.components.RecycleItemCard
import com.example.recyclehelper.ui.components.RegionPickerDialog
import com.example.recyclehelper.ui.theme.CardBackground
import com.example.recyclehelper.ui.theme.GreenLight
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onItemClick: (RecycleItem) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var showRegionPicker by remember { mutableStateOf(false) }

    if (showRegionPicker) {
        RegionPickerDialog(
            currentCity = state.selectedCity,
            currentDistrict = state.selectedDistrict,
            regions = state.availableRegions,
            isLoading = state.isRegionLoading,
            onDismiss = { showRegionPicker = false },
            onConfirm = { city, district ->
                viewModel.updateRegion(city, district)
                showRegionPicker = false
            }
        )
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenPrimary)
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column {
                    Text(
                        text = "분리배출 도우미",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "올바른 분리배출 방법과 지역별 배출 일정을 확인해요",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    RegionChip(
                        city = state.selectedCity,
                        district = state.selectedDistrict,
                        onClick = { showRegionPicker = true },
                        textColor = Color.White,
                        backgroundColor = Color.White.copy(alpha = 0.2f)
                    )
                }
            }
        }

        item {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("검색 물품을 입력해주세요") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search,
                    capitalization = KeyboardCapitalization.None
                ),
                keyboardActions = KeyboardActions(
                    onSearch = { viewModel.onSearchCommit(state.query) }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedContainerColor = CardBackground,
                    focusedContainerColor = CardBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(10.dp))

            // 카테고리 필터 chip
            CategoryFilterRow(
                selected = state.selectedWasteCategory,
                onSelect = viewModel::selectWasteCategory
            )
            Spacer(Modifier.height(12.dp))
        }

        // 검색어가 비었을 때만 최근 검색어 노출
        if (state.query.isBlank() && state.recentQueries.isNotEmpty()) {
            item {
                RecentQueriesRow(
                    queries = state.recentQueries,
                    onSelect = { q -> viewModel.onSearchCommit(q) },
                    onClearAll = { viewModel.clearRecentQueries() }
                )
                Spacer(Modifier.height(8.dp))
            }
        }

        when {
            // 1) 검색어도 카테고리도 없으면 → 안내 + 추천 카테고리
            state.query.isBlank() && state.selectedWasteCategory == WasteCategory.ALL ->
                item { EmptyHintBlock(onSelect = viewModel::selectWasteCategory) }

            // 2) 결과가 비어있고 검색어가 있는 경우 → 추천 검색어 / 카테고리
            state.results.isEmpty() && state.hasNoMatch ->
                item {
                    NoResultBlock(
                        query = state.query,
                        recommendedQueries = state.recommendedQueries,
                        onSelectQuery = { q -> viewModel.onSearchCommit(q) },
                        onSelectCategory = viewModel::selectWasteCategory
                    )
                }

            // 3) 카테고리만 선택된 경우 → 대표 품목 안내 헤더
            state.query.isBlank() && state.selectedWasteCategory != WasteCategory.ALL ->
                item {
                    CategoryHeader(
                        category = state.selectedWasteCategory,
                        count = state.results.size
                    )
                }

            else -> Unit
        }

        items(state.results, key = { it.id }) { item ->
            val zone = state.zones.getOrNull(state.selectedZoneIndex)
            val scheduleHint = buildRegionScheduleHint(zone, item.scheduleType)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (state.query.isNotBlank()) viewModel.onSearchCommit(state.query)
                        onItemClick(item)
                    }
            ) {
                RecycleItemCard(
                    item = item,
                    onFavoriteClick = viewModel::toggleFavorite,
                    regionScheduleText = scheduleHint
                )
            }
        }
    }
}

@Composable
private fun CategoryFilterRow(
    selected: WasteCategory,
    onSelect: (WasteCategory) -> Unit
) {
    // 10개 카테고리를 5개씩 두 줄로 나눠 화면 너비 안에 완전히 표시
    val all = WasteCategory.entries
    val row1 = all.take(5)
    val row2 = all.drop(5)

    Column(
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        listOf(row1, row2).forEach { row ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { cat -> CategoryChip(cat, cat == selected, onSelect) }
            }
        }
    }
}

@Composable
private fun CategoryChip(cat: WasteCategory, isSelected: Boolean, onSelect: (WasteCategory) -> Unit) {
    val bg     = if (isSelected) GreenPrimary else Color.White
    val fg     = if (isSelected) Color.White  else GreenPrimary
    val border = if (isSelected) GreenPrimary else GreenPrimary.copy(alpha = 0.3f)
    Text(
        text = cat.label,
        fontSize = 12.sp,
        color = fg,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(20.dp))
            .clickable { onSelect(cat) }
            .padding(horizontal = 11.dp, vertical = 6.dp)
    )
}

@Composable
private fun RecentQueriesRow(
    queries: List<String>,
    onSelect: (String) -> Unit,
    onClearAll: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.History, null, tint = TextSecondary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text("최근 검색어", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.weight(1f))
            Text(
                "전체 삭제",
                color = TextSecondary,
                fontSize = 12.sp,
                modifier = Modifier
                    .clickable { onClearAll() }
                    .padding(4.dp)
            )
        }
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            queries.forEach { q ->
                Text(
                    q,
                    fontSize = 13.sp,
                    color = GreenPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GreenLight)
                        .clickable { onSelect(q) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun RegionChip(
    city: String,
    district: String,
    onClick: () -> Unit,
    textColor: Color,
    backgroundColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            Icons.Filled.LocationOn,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text("$city $district", color = textColor, fontSize = 13.sp)
        Spacer(Modifier.width(6.dp))
        Icon(
            Icons.Filled.Edit,
            contentDescription = "지역 변경",
            tint = textColor,
            modifier = Modifier.size(12.dp)
        )
    }
}

@Composable
private fun EmptyHintBlock(onSelect: (WasteCategory) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            "검색어를 입력하거나 아래 카테고리를 골라 보세요",
            color = TextSecondary,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            listOf(
                WasteCategory.RECYCLE,
                WasteCategory.GENERAL,
                WasteCategory.FOOD,
                WasteCategory.HAZARDOUS,
                WasteCategory.BULK,
                WasteCategory.APPLIANCE
            ).forEach { c ->
                Text(
                    c.label,
                    fontSize = 13.sp,
                    color = GreenPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GreenLight)
                        .clickable { onSelect(c) }
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
private fun CategoryHeader(category: WasteCategory, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${category.label} 대표 품목",
            fontSize = 14.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(6.dp))
        Text("${count}개", fontSize = 12.sp, color = TextSecondary)
    }
}

@Composable
private fun NoResultBlock(
    query: String,
    recommendedQueries: List<String>,
    onSelectQuery: (String) -> Unit,
    onSelectCategory: (WasteCategory) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Text(
            "'$query' 에 대한 검색 결과가 없습니다.",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "다른 이름으로 검색하거나 아래 카테고리를 선택해 보세요.",
            color = TextSecondary,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(12.dp))
        Text("추천 검색어", fontWeight = FontWeight.Medium, color = TextPrimary, fontSize = 13.sp)
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            recommendedQueries.forEach { q ->
                Text(
                    q,
                    fontSize = 13.sp,
                    color = GreenPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(GreenLight)
                        .clickable { onSelectQuery(q) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("추천 카테고리", fontWeight = FontWeight.Medium, color = TextPrimary, fontSize = 13.sp)
        Spacer(Modifier.height(6.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.horizontalScroll(rememberScrollState())
        ) {
            WasteCategory.entries.filter { it != WasteCategory.ALL }.forEach { c ->
                Text(
                    c.label,
                    fontSize = 13.sp,
                    color = GreenPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White)
                        .border(1.dp, GreenPrimary.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                        .clickable { onSelectCategory(c) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * 검색 결과 카드에 "재활용품 배출일: 월, 수, 금 / 18:00 ~ 24:00" 같은 한 줄을 만들어 돌려준다.
 * 카드는 이 문자열을 그대로 표시한다.
 */
internal fun buildRegionScheduleHint(
    zone: com.example.recyclehelper.data.model.ZoneInfo?,
    scheduleType: String
): String? {
    if (zone == null) return null
    val info = when (scheduleType) {
        "재활용품" -> zone.recyclable
        "생활쓰레기" -> zone.general
        "음식물쓰레기" -> zone.food
        else -> null
    }
    if (info != null) {
        val dow = com.example.recyclehelper.util.formatDays(info.dow)
        val time = info.timeRange
        return "$scheduleType 배출일: $dow / $time"
    }
    if (scheduleType == "대형폐기물") {
        return "대형폐기물은 지자체 신고 후 배출"
    }
    if (scheduleType == "특수폐기물") {
        return "전용 수거함 또는 지정 장소 배출"
    }
    return null
}
