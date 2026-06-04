package com.example.recyclehelper.data.model

import java.time.DayOfWeek

data class ZoneInfo(
    val regionName: String,
    val zoneName: String,
    val targetArea: String,
    val collectionType: String,
    val collectionPlace: String,
    val general: WasteTypeInfo?,
    val food: WasteTypeInfo?,
    val recyclable: WasteTypeInfo?,
    val bulk: BulkWasteInfo?,
    val uncollectedDay: String,
    val deptName: String,
    val deptTel: String
)

data class WasteTypeInfo(
    val category: Category,
    val dow: String,
    val startTime: String,
    val endTime: String,
    val method: String,
    val days: List<DayOfWeek>
) {
    val timeRange: String
        get() = if (startTime.isNotBlank() && endTime.isNotBlank()) {
            "$startTime ~ $endTime"
        } else {
            "시간 미정"
        }

    fun isToday(today: DayOfWeek): Boolean = days.contains(today)
}

data class BulkWasteInfo(
    val startTime: String,
    val endTime: String,
    val method: String,
    val place: String
) {
    val timeRange: String
        get() = if (startTime.isNotBlank() && endTime.isNotBlank()) {
            "$startTime ~ $endTime"
        } else {
            "시간 미정"
        }
}
