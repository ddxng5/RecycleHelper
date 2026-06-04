package com.example.recyclehelper.data.model

/**
 * 검색 화면 상단의 카테고리 필터 chip 에 대응하는 enum.
 *  - label  : UI 에 보이는 한글 라벨
 *  - groups : 이 카테고리에 묶일 RecycleItem.wasteGroup 값들
 *  - extra  : 위 wasteGroup 에 해당하지는 않지만 함께 노출할 RecycleItem.subCategory 값들
 *
 *  예) 음식물 카테고리는 wasteGroup="음식물류폐기물" 항목과
 *     subCategory="음식물 제외 품목" 항목을 함께 노출한다.
 */
enum class WasteCategory(
    val label: String,
    val groups: Set<String>,
    val extraSubCategories: Set<String> = emptySet()
) {
    ALL("전체", emptySet()),
    RECYCLE("재활용", setOf("재활용폐기물")),
    GENERAL("일반", setOf("일반폐기물")),
    FOOD("음식물", setOf("음식물류폐기물"), extraSubCategories = setOf("음식물 제외 품목")),
    HAZARDOUS("유해폐기물", setOf("생활계 유해폐기물")),
    BULK("대형폐기물", setOf("대형폐기물")),
    APPLIANCE("폐가전", setOf("폐가전")),
    TEXTILE("의류", setOf("의류/섬유류")),
    CONSTRUCTION("공사장", setOf("공사장 생활폐기물")),
    ETC("기타", setOf("기타"));

    fun matches(item: RecycleItem): Boolean {
        if (this == ALL) return true
        if (item.wasteGroup in groups) return true
        if (item.subCategory in extraSubCategories) return true
        return false
    }
}
