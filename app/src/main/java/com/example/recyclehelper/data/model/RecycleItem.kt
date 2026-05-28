package com.example.recyclehelper.data.model

import androidx.compose.ui.graphics.Color

/**
 * 분리배출 품목 정보.
 *
 * 기존 필드(`category`, `disposalMethod`, `disposalLocation`, `cautions`, `isFavorite`)는
 * 모두 그대로 유지하고, 환경부 분리배출 누리집 품목사전 구조를 참고해
 * 다음 필드들을 추가했다:
 *
 *  - subCategory   : 세부 카테고리 (예: 페트병, 종이류, 종이팩, 플라스틱류 …)
 *  - wasteGroup    : 대분류 (예: 재활용폐기물, 일반폐기물, 음식물류폐기물 …)
 *  - scheduleType  : 오늘배출 정보와 연결할 배출 유형 (재활용품 / 생활쓰레기 / 음식물쓰레기 / 특수폐기물 / 대형폐기물)
 *  - keywords      : 사용자가 검색할 수 있는 비슷한 표현
 *  - isSpecialWaste: 전용 수거함 / 약국·보건소 / 별도 신고 등이 필요한 품목 여부
 */
data class RecycleItem(
    val id: String,
    val name: String,
    val category: Category,
    val disposalMethod: String,
    val disposalLocation: String,
    val cautions: List<String>,
    val isFavorite: Boolean = false,

    // ───────── 신규 필드 ─────────
    val subCategory: String = category.label,
    val wasteGroup: String = "",
    val scheduleType: String = "",
    val keywords: List<String> = emptyList(),
    val isSpecialWaste: Boolean = false
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
