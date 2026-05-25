package com.example.recyclehelper.data.model

import java.time.DayOfWeek

/**
 * 한 카테고리의 배출 일정 (요일 + 시간대)
 */
data class DaySchedule(
    val category: Category,
    val dayOfWeek: DayOfWeek,   // 월~일
    val timeRange: String       // 예: "18:00 ~ 익일 06:00"
)

/**
 * 지역(법정동) 단위 배출 일정 묶음
 */
data class RegionSchedule(
    val regionName: String,            // 예: "서울특별시 강남구 역삼동"
    val schedules: List<DaySchedule>
)
