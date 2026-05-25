// data/remote/dto/WasteApiResponse.kt

package com.example.recyclehelper.data.remote.dto

/**
 * 공공데이터 API 공통 응답 구조
 * response > header + body 형태
 */
data class WasteApiResponse(
    val response: ResponseWrapper
)

data class ResponseWrapper(
    val header: ResponseHeader,
    val body: ResponseBody
)

data class ResponseHeader(
    val resultCode: String,   // "00" 이면 정상
    val resultMsg: String     // "NORMAL_SERVICE"
)

data class ResponseBody(
    val items: ItemsWrapper?,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int
)

data class ItemsWrapper(
    val item: List<WasteItemDto>
)

/**
 * API에서 받아오는 쓰레기 배출 정보 하나.
 * 실제 필드명은 API 문서의 응답 변수명과 정확히 맞춰야 합니다.
 * (API 문서 "미리보기"에서 확인 가능)
 */
data class WasteItemDto(
    val ctpvNm: String?,       // 시도명 (서울특별시)
    val sggNm: String?,        // 시군구명 (강남구)
    val mngAreaNm: String?,    // 관리구역명
    val dscrtPlcTy: String?,   // 배출장소유형
    val lvStwDscrtMtd: String?,  // 생활쓰레기 배출방법
    val fdStwDscrtMtd: String?,  // 음식물쓰레기 배출방법
    val rcycGdDscrtMtd: String?, // 재활용품 배출방법
    val dscrtDow: String?,     // 배출요일
    val dscrtTmzn: String?,    // 배출시간대
    val mngDeptNm: String?,    // 관리부서명
    val mngDeptTelno: String?  // 연락처
)