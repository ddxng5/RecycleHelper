package com.example.recyclehelper.data.remote.api

import com.example.recyclehelper.data.remote.dto.WasteApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 행정안전부 생활쓰레기배출정보 조회 API
 * operation: info
 * End Point: https://apis.data.go.kr/1741000/household_waste_info
 */
interface WasteApi {

    @GET("info")
    suspend fun getWasteInfo(
        @Query(value = "serviceKey", encoded = true) serviceKey: String,
        @Query("pageNo")    pageNo: Int    = 1,
        @Query("numOfRows") numOfRows: Int = 100,
        @Query("returnType") returnType: String = "json",
        @Query("ctpvNm")    cityName: String?     = null,  // 시도명 (선택)
        @Query("sggNm")     districtName: String? = null   // 시군구명 (선택)
    ): WasteApiResponse
}
