package com.example.recyclehelper.ui.favorites

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.data.model.RecycleItem
import com.example.recyclehelper.ui.components.RecycleItemCard
import com.example.recyclehelper.ui.search.SearchViewModel
import com.example.recyclehelper.ui.search.buildRegionScheduleHint
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary

@Composable
fun FavoritesScreen(
    viewModel: SearchViewModel,
    onItemClick: (RecycleItem) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val items = remember(state.favorites) { viewModel.getFavoriteItems() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Icon(Icons.Filled.Star, null, tint = GreenPrimary)
            Text("즐겨찾기", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = TextPrimary)
            Text("${items.size}개", color = TextSecondary, fontSize = 14.sp)
        }
        Spacer(Modifier.height(12.dp))

        if (items.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "아직 즐겨찾기한 항목이 없어요\n검색 결과에서 별을 눌러 추가해 보세요",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
            return
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items, key = { it.id }) { item ->
                val zone = state.zones.getOrNull(state.selectedZoneIndex)
                val hint = buildRegionScheduleHint(zone, item.scheduleType)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                ) {
                    RecycleItemCard(
                        item = item,
                        onFavoriteClick = { viewModel.toggleFavorite(it) },
                        regionScheduleText = hint
                    )
                }
            }
        }
    }
}
