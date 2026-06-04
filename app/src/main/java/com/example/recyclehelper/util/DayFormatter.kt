package com.example.recyclehelper.util

/**
 * API/MockData 원본 응답에 들어있는 "월+화+수+목" 같은 표현을
 * UI 표시용 "월, 화, 수, 목" 형태로 정리한다.
 *
 *  - 공통 구분자(+ / · ㆍ ,)를 모두 쉼표 + 공백으로 변환
 *  - "요일" 접미사 정리
 *  - 빈값/널은 "-" 로 노출
 */
fun formatDays(days: String?): String {
    if (days.isNullOrBlank()) return "-"
    return days
        .replace("+", ", ")
        .replace("/", ", ")
        .replace("·", ", ")
        .replace("ㆍ", ", ")
        .replace(Regex("\\s*,\\s*"), ", ")
        .replace(Regex("\\s+"), " ")
        .replace(Regex(",\\s*,"), ",")
        .trim()
        .trimEnd(',')
        .trim()
}
