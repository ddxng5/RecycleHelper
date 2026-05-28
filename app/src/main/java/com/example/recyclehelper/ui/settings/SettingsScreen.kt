package com.example.recyclehelper.ui.settings

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.ui.components.RecycleItemCard
import com.example.recyclehelper.ui.components.RegionPickerDialog
import com.example.recyclehelper.ui.search.SearchViewModel
import com.example.recyclehelper.ui.theme.CardBackground
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary

@Composable
fun SettingsScreen(searchViewModel: SearchViewModel) {
    var notifyEnabled by remember { mutableStateOf(true) }
    var showRegionPicker by remember { mutableStateOf(false) }
    val uiState by searchViewModel.uiState.collectAsState()
    val favoriteItems = searchViewModel.getFavoriteItems()

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

        SettingCard(onClick = { showRegionPicker = true }) {
            Icon(Icons.Filled.LocationOn, null, tint = GreenPrimary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("우리 동네", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(
                    "${uiState.selectedCity} ${uiState.selectedDistrict}",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
            Icon(Icons.Filled.ChevronRight, null, tint = TextSecondary)
        }

        Spacer(Modifier.height(8.dp))

        SettingCard(onClick = null) {
            Icon(Icons.Filled.Notifications, null, tint = GreenPrimary)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("배출일 알림", fontWeight = FontWeight.Bold, color = TextPrimary)
                Text("배출 전날 저녁에 알려드려요", color = TextSecondary, fontSize = 13.sp)
            }
            Switch(checked = notifyEnabled, onCheckedChange = { notifyEnabled = it })
        }

        Spacer(Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Star, null, tint = GreenPrimary)
            Spacer(Modifier.width(8.dp))
            Text("즐겨찾기 목록", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            Spacer(Modifier.width(8.dp))
            Text("${favoriteItems.size}개", color = TextSecondary, fontSize = 14.sp)
        }
        Spacer(Modifier.height(8.dp))

        if (favoriteItems.isEmpty()) {
            Text(
                "아직 즐겨찾기한 항목이 없어요\n검색 결과에서 별을 눌러 추가해보세요",
                color = TextSecondary,
                fontSize = 14.sp,
                modifier = Modifier.padding(vertical = 20.dp)
            )
        } else {
            favoriteItems.forEach { item ->
                RecycleItemCard(
                    item = item,
                    onFavoriteClick = { searchViewModel.toggleFavorite(it) }
                )
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
