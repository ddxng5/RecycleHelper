package com.example.recyclehelper.data.model

/**
 * 배출 완료 기록 한 건.
 *
 * id = "${date}_${wasteType}"  → 하루에 같은 종류는 한 번만 기록 (토글용 고유 키)
 * date : ISO 날짜 "2025-05-31"
 * wasteType : "재활용품" | "생활쓰레기" | "음식물쓰레기"
 */
data class DisposalRecord(
    val id: String,
    val date: String,
    val wasteType: String,
    val city: String,
    val district: String,
    val timestamp: Long = System.currentTimeMillis()
) {
    companion object {
        fun makeId(date: String, wasteType: String) = "${date}_${wasteType}"
    }
}
