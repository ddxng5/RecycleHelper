package com.example.recyclehelper.data.mock

import com.example.recyclehelper.data.local.WasteItemStore
import com.example.recyclehelper.data.model.Category
import com.example.recyclehelper.data.model.DaySchedule
import com.example.recyclehelper.data.model.RecycleItem
import com.example.recyclehelper.data.model.RegionSchedule
import java.time.DayOfWeek

/**
 * API 연동 전 사용하는 임시(mock) 데이터.
 *
 * 품목사전(생활폐기물 분리배출 누리집 분류 참고)을 200개 이상으로 확장하면서
 * 실제 데이터는 assets/waste_items.json 으로 옮겼다.
 * MockData 는 기존 코드(`MockData.items`, `MockData.search`, `MockData.regionSchedule`)
 * 호환을 위해 WasteItemStore 로 위임만 한다.
 */
object MockData {

    /** WasteItemStore 가 로딩된 후의 전체 품목. */
    val items: List<RecycleItem>
        get() = WasteItemStore.items

    /**
     * 품목명 / 세부카테고리 / 대분류 / keywords 중 어디든 부분 일치하면 결과에 포함.
     * 상위 검색 로직(필터·정렬)은 SearchViewModel.search() 에서 다시 다듬는다.
     */
    fun search(query: String): List<RecycleItem> = WasteItemStore.search(query)

    /**
     * 우리 동네 배출 일정 (mock).
     * 기존 호환을 위해 그대로 유지한다.
     */
    val regionSchedule = RegionSchedule(
        regionName = "서울특별시 강남구 역삼동",
        schedules = listOf(
            DaySchedule(Category.GENERAL, DayOfWeek.MONDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.FOOD, DayOfWeek.MONDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.GENERAL, DayOfWeek.TUESDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.FOOD, DayOfWeek.TUESDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.RECYCLABLE, DayOfWeek.WEDNESDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.FOOD, DayOfWeek.WEDNESDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.GENERAL, DayOfWeek.THURSDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.FOOD, DayOfWeek.THURSDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.RECYCLABLE, DayOfWeek.FRIDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.FOOD, DayOfWeek.FRIDAY, "18:00 ~ 익일 06:00"),
            DaySchedule(Category.LARGE, DayOfWeek.SATURDAY, "08:00 ~ 18:00")
        )
    )
}
