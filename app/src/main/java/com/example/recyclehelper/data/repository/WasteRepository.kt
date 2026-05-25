// data/repository/WasteRepository.kt

package com.example.recyclehelper.data.repository

import com.example.recyclehelper.BuildConfig
import com.example.recyclehelper.data.mock.MockData
import com.example.recyclehelper.data.model.Category
import com.example.recyclehelper.data.model.RecycleItem
import com.example.recyclehelper.data.remote.RetrofitClient
import com.example.recyclehelper.data.remote.dto.WasteItemDto

class WasteRepository {

    private val api = RetrofitClient.wasteApi

    /**
     * 지역별 쓰레기 배출 정보를 가져온다.
     * API 실패 시 Mock 데이터로 fallback.
     */
    suspend fun getWasteInfo(
        cityName: String,
        districtName: String
    ): List<RecycleItem> {
        return try {
            // 1. API 호출 시도
            val response = api.getWasteInfo(
                serviceKey = BuildConfig.WASTE_API_KEY,
                cityName = cityName,
                districtName = districtName,
                numOfRows = 100
            )

            // 2. 응답 코드 확인
            if (response.response.header.resultCode == "00") {
                // 성공 → DTO를 앱 모델로 변환
                val items = response.response.body.items?.item ?: emptyList()
                items.map { it.toRecycleItem() }
            } else {
                // API 에러 → Mock으로 fallback
                MockData.items
            }
        } catch (e: Exception) {
            // 네트워크 에러 등 → Mock으로 fallback
            e.printStackTrace()
            MockData.items
        }
    }

    /**
     * API DTO → 앱에서 쓰는 RecycleItem 으로 변환
     */
    private fun WasteItemDto.toRecycleItem(): RecycleItem {
        return RecycleItem(
            id = "${ctpvNm}_${sggNm}_${mngAreaNm}".hashCode().toString(),
            name = mngAreaNm ?: "알 수 없음",
            category = guessCategory(lvStwDscrtMtd),
            disposalMethod = buildString {
                lvStwDscrtMtd?.let { append("생활: $it") }
                fdStwDscrtMtd?.let { append("\n음식물: $it") }
                rcycGdDscrtMtd?.let { append("\n재활용: $it") }
            },
            disposalLocation = dscrtPlcTy ?: "지정 장소",
            cautions = listOf(
                "배출 요일: ${dscrtDow ?: "확인 필요"}",
                "배출 시간: ${dscrtTmzn ?: "확인 필요"}",
                "문의: ${mngDeptNm ?: ""} ${mngDeptTelno ?: ""}"
            )
        )
    }

    /**
     * 배출방법 텍스트로 카테고리 추정 (간단 버전)
     */
    private fun guessCategory(method: String?): Category {
        if (method == null) return Category.GENERAL
        return when {
            "재활용" in method -> Category.RECYCLABLE
            "음식물" in method -> Category.FOOD
            "대형" in method -> Category.LARGE
            else -> Category.GENERAL
        }
    }


}