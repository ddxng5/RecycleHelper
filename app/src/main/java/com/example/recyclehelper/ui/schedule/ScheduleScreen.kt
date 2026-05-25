package com.example.recyclehelper.ui.schedule

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.data.mock.MockData
import com.example.recyclehelper.ui.theme.CardBackground
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.TextPrimary
import com.example.recyclehelper.ui.theme.TextSecondary
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun ScheduleScreen() {
    val schedule = MockData.regionSchedule

    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {

        Text(schedule.regionName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text("요일별 배출 일정", color = TextSecondary, fontSize = 14.sp)
        Spacer(Modifier.height(16.dp))

        // 요일 순서대로 정렬해서 표시
        DayOfWeek.entries.forEach { day ->
            val items = schedule.schedules.filter { it.dayOfWeek == day }
            if (items.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(GreenPrimary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                day.getDisplayName(TextStyle.SHORT, Locale.KOREAN),
                                color = GreenPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.size(14.dp))
                        Column {
                            items.forEach { ds ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(vertical = 2.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(ds.category.color)
                                    )
                                    Text("${ds.category.label}  ", color = TextPrimary, fontSize = 15.sp)
                                    Text(ds.timeRange, color = TextSecondary, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
