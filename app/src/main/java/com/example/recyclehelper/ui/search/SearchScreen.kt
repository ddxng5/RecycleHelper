package com.example.recyclehelper.ui.search

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recyclehelper.ui.components.RecycleItemCard
import com.example.recyclehelper.ui.components.RegionPickerDialog
import com.example.recyclehelper.ui.theme.CardBackground
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.TextSecondary

@Composable
fun SearchScreen(viewModel: SearchViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showRegionPicker by remember { mutableStateOf(false) }

    if (showRegionPicker) {
        RegionPickerDialog(
            currentCity = state.selectedCity,
            currentDistrict = state.selectedDistrict,
            onDismiss = { showRegionPicker = false },
            onConfirm = { city, district ->
                viewModel.updateRegion(city, district)
                showRegionPicker = false
            }
        )
    }

    LaunchedEffect(Unit) {
        viewModel.loadRegionInfo(
            city = state.selectedCity,
            district = state.selectedDistrict
        )
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {

        // ── 초록색 헤더 ──
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GreenPrimary)
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                Column {
                    Text(
                        text = "♻  분리배출 도우미",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "올바른 분리배출로 지구를 지켜요 🌍",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    // 지역 표시 + 변경 버튼
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { showRegionPicker = true }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${state.selectedCity} ${state.selectedDistrict}",
                            color = Color.White,
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            Icons.Filled.Edit,
                            contentDescription = "지역 변경",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
            }
        }

        // ── 검색창 ──
        item {
            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = state.query,
                onValueChange = viewModel::onQueryChange,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("페트병, 치킨박스, 건전지...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GreenPrimary,
                    unfocusedContainerColor = CardBackground,
                    focusedContainerColor = CardBackground
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))
        }

        // ── Mock 데이터 안내 ──
        item {
            MockNotice()
            Spacer(Modifier.height(8.dp))
        }

        // ── 검색 결과 ──
        when {
            state.query.isBlank() -> item { EmptyHint() }
            state.results.isEmpty() -> item { NoResult(state.query) }
            else -> items(state.results, key = { it.id }) { item ->
                RecycleItemCard(item = item, onFavoriteClick = viewModel::toggleFavorite)
            }
        }
    }
}

@Composable
private fun MockNotice() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(10.dp))
            .padding(14.dp)
    ) {
        Icon(Icons.Filled.Info, null, tint = TextSecondary)
        Text(
            text = "현재 목(mock) 데이터를 사용 중입니다. 실제 환경에서는 " +
                    "기후에너지환경부와 행정안전부 API를 연동해야 합니다.",
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}

@Composable
private fun EmptyHint() {
    Box(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("검색어를 입력해 보세요", color = TextSecondary)
    }
}

@Composable
private fun NoResult(query: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text("'$query' 에 대한 결과가 없어요", color = TextSecondary)
    }
}
