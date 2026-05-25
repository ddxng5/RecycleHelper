package com.example.recyclehelper.ui.today

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.data.mock.MockData
import com.example.recyclehelper.data.model.Category
import com.example.recyclehelper.ui.theme.GreenLight
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun TodayScreen() {
    val today: DayOfWeek = LocalDate.now().dayOfWeek
    val todayLabel = today.getDisplayName(TextStyle.FULL, Locale.KOREAN)

    val disposableToday = MockData.regionSchedule.schedules
        .filter { it.dayOfWeek == today }
        .map { it.category }
        .distinct()

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = GreenLight),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CalendarMonth, null, tint = GreenPrimary)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "오늘 배출 가능한 쓰레기",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = TextPrimary
                    )
                }
                Text(todayLabel, color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(top = 4.dp))

                Spacer(Modifier.height(16.dp))

                if (disposableToday.isEmpty()) {
                    Text("오늘은 배출 가능한 항목이 없어요", color = TextSecondary)
                } else {
                    disposableToday.forEach { category ->
                        CategoryRow(category)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryRow(category: Category) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(category.color)
        )
        Text(category.label, fontSize = 16.sp, color = TextPrimary)
    }
}
