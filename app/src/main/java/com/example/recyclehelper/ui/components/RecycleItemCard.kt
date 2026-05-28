package com.example.recyclehelper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.data.model.Category
import com.example.recyclehelper.data.model.RecycleItem
import com.example.recyclehelper.ui.theme.CardBackground
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.InfoBlue
import com.example.recyclehelper.ui.theme.LocationRed
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary

/** 카테고리 칩 (예: 재활용품) */
@Composable
fun CategoryChip(category: Category) {
    val bg = category.color.copy(alpha = 0.15f)
    Row(
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(
            text = category.label,
            color = category.color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SmallTag(label: String, fg: Color = GreenPrimary, bg: Color = GreenPrimary.copy(alpha = 0.1f)) {
    Text(
        label,
        fontSize = 11.sp,
        color = fg,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .background(bg, RoundedCornerShape(50))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

/**
 * 검색 결과 품목 카드.
 *
 * 새 필드(`wasteGroup`, `subCategory`, `scheduleType`, `isSpecialWaste`)와
 * 우리 동네 배출 정보(`regionScheduleText`)를 함께 노출한다.
 */
@Composable
fun RecycleItemCard(
    item: RecycleItem,
    onFavoriteClick: (RecycleItem) -> Unit = {},
    regionScheduleText: String? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {

            // 제목 + 즐겨찾기
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = item.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(Modifier.weight(1f))
                IconButton(onClick = { onFavoriteClick(item) }) {
                    Icon(
                        imageVector = if (item.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "즐겨찾기",
                        tint = if (item.isFavorite) StarColor else TextSecondary
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // 대분류 / 세부분류 / 배출유형 태그 줄
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CategoryChip(item.category)
                if (item.wasteGroup.isNotBlank()) SmallTag(item.wasteGroup)
                if (item.subCategory.isNotBlank() && item.subCategory != item.category.label) {
                    SmallTag("분류: ${item.subCategory}", fg = TextSecondary, bg = TextSecondary.copy(alpha = 0.1f))
                }
            }
            if (item.scheduleType.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                SmallTag("배출 유형: ${item.scheduleType}", fg = InfoBlue, bg = InfoBlue.copy(alpha = 0.1f))
            }

            Spacer(Modifier.height(16.dp))

            // 배출 방법
            SectionHeader(icon = { Icon(Icons.Filled.Info, null, tint = InfoBlue) }, title = "배출 방법")
            Text(item.disposalMethod, color = TextPrimary, fontSize = 15.sp)
            Spacer(Modifier.height(14.dp))

            // 배출 장소
            if (item.disposalLocation.isNotBlank()) {
                SectionHeader(icon = { Icon(Icons.Filled.LocationOn, null, tint = LocationRed) }, title = "배출 장소")
                Text(item.disposalLocation, color = TextPrimary, fontSize = 15.sp)
                Spacer(Modifier.height(14.dp))
            }

            // 주의사항
            if (item.cautions.isNotEmpty()) {
                Text("주의사항", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
                Spacer(Modifier.height(6.dp))
                item.cautions.forEach { caution ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text("• ", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold)
                        Text(caution, color = TextSecondary, fontSize = 14.sp)
                    }
                }
            }

            // 특수폐기물 강조
            if (item.isSpecialWaste) {
                Spacer(Modifier.height(12.dp))
                SpecialWasteWarning(item)
            }

            // 우리 동네 배출 정보
            if (!regionScheduleText.isNullOrBlank()) {
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GreenPrimary.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Filled.CalendarMonth, null, tint = GreenPrimary)
                    Spacer(Modifier.height(0.dp))
                    Text(
                        "  $regionScheduleText",
                        color = GreenPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialWasteWarning(item: RecycleItem) {
    val message = when (item.scheduleType) {
        "특수폐기물" -> "전용 수거함 또는 지정 장소에 배출하세요. 일반 재활용품으로 배출하지 마세요."
        "대형폐기물" -> "대형폐기물은 지자체 신고 후 배출해야 합니다."
        else -> "전용 수거함 또는 지정 장소에 배출해야 합니다."
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3F3), RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFFE53935).copy(alpha = 0.3f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Icon(Icons.Filled.Warning, null, tint = Color(0xFFE53935))
        Spacer(Modifier.height(0.dp))
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(
                "특수 배출 안내",
                color = Color(0xFFE53935),
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(message, color = TextPrimary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun SectionHeader(icon: @Composable () -> Unit, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(bottom = 6.dp)
    ) {
        icon()
        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
    }
}

private val StarColor = Color(0xFFFFC107)
