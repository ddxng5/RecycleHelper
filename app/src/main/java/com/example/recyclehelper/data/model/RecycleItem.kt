package com.example.recyclehelper.data.model

import androidx.compose.ui.graphics.Color

/**
 * 분리배출 품목 정보
 */
data class RecycleItem(
    val id: String,
    val name: String,                 // 예: "페트병"
    val category: Category,           // 분류
    val disposalMethod: String,       // 배출 방법
    val disposalLocation: String,     // 배출 장소
    val cautions: List<String>,       // 주의사항 목록
    val isFavorite: Boolean = false   // 즐겨찾기 여부
)

/**
 * 쓰레기 분류 카테고리.
 * label 은 화면 표시용, color 는 칩(Chip) 색상.
 */
enum class Category(val label: String, val color: Color) {
    RECYCLABLE("재활용품", Color(0xFF4CAF50)),
    GENERAL("일반쓰레기", Color(0xFF9E9E9E)),
    FOOD("음식물쓰레기", Color(0xFFFF9800)),
    LARGE("대형폐기물", Color(0xFF795548)),
    HAZARDOUS("유해폐기물", Color(0xFFF44336))
}
