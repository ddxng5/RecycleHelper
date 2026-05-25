// data/remote/RetrofitClient.kt

package com.example.recyclehelper.data.remote

import com.example.recyclehelper.data.remote.api.WasteApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // API 기본 URL (API 페이지의 "서비스URL" 확인)
    private const val BASE_URL =
        "https://apis.data.go.kr/1741000/household_waste_info/"

    // 로그 찍어주는 인터셉터 (개발 중 디버깅용)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val wasteApi: WasteApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WasteApi::class.java)
    }
}