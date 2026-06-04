package com.example.recyclehelper.data.prefs

import android.content.Context
import com.example.recyclehelper.data.model.DisposalRecord
import com.example.recyclehelper.data.remote.dto.WasteItemDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * SharedPreferences 기반 사용자별 영속 저장소.
 *
 * userId 를 파일명에 포함시켜 사용자별 데이터를 자동 격리한다.
 *   - 일반 사용자: recycle_helper_prefs_test01
 *   - 게스트:      recycle_helper_prefs_guest
 *
 * 저장 항목: 지역·즐겨찾기·최근검색어·알림설정·배출기록·지역목록캐시 등
 */
class PrefsManager(context: Context, userId: String = "guest") {

    private val gson = Gson()

    private val prefs = context.applicationContext
        .getSharedPreferences("${PREFS_NAME}_$userId", Context.MODE_PRIVATE)

    var selectedCity: String?
        get() = prefs.getString(KEY_CITY, null)
        set(value) { prefs.edit().putString(KEY_CITY, value).apply() }

    var selectedDistrict: String?
        get() = prefs.getString(KEY_DISTRICT, null)
        set(value) { prefs.edit().putString(KEY_DISTRICT, value).apply() }

    var favorites: Set<String>
        get() = prefs.getStringSet(KEY_FAVORITES, emptySet()).orEmpty().toSet()
        set(value) { prefs.edit().putStringSet(KEY_FAVORITES, value).apply() }

    var recentQueries: List<String>
        get() {
            val raw = prefs.getString(KEY_RECENT, null).orEmpty()
            if (raw.isBlank()) return emptyList()
            return raw.split(SEPARATOR).filter { it.isNotBlank() }
        }
        set(value) {
            prefs.edit().putString(KEY_RECENT, value.joinToString(SEPARATOR)).apply()
        }

    var notifyEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFY, false)
        set(value) { prefs.edit().putBoolean(KEY_NOTIFY, value).apply() }

    /** 알림 시간 — 시(0~23), 기본 20 */
    var notifyHour: Int
        get() = prefs.getInt(KEY_NOTIFY_HOUR, 20)
        set(value) { prefs.edit().putInt(KEY_NOTIFY_HOUR, value).apply() }

    /** 알림 시간 — 분(0,15,30,45), 기본 0 */
    var notifyMinute: Int
        get() = prefs.getInt(KEY_NOTIFY_MINUTE, 0)
        set(value) { prefs.edit().putInt(KEY_NOTIFY_MINUTE, value).apply() }

    /** 알림 시점: 0=당일, 1=전날 */
    var notifyAdvanceDays: Int
        get() = prefs.getInt(KEY_NOTIFY_ADVANCE, 0)
        set(value) { prefs.edit().putInt(KEY_NOTIFY_ADVANCE, value).apply() }

    /** 알림 받을 배출 유형 Set */
    var notifyWasteTypes: Set<String>
        get() = prefs.getStringSet(KEY_NOTIFY_TYPES, DEFAULT_NOTIFY_TYPES).orEmpty()
        set(value) { prefs.edit().putStringSet(KEY_NOTIFY_TYPES, value).apply() }

    /** 지역 목록 캐시 (Map<시도, List<시군구>>) — Gson JSON 직렬화 */
    var cachedRegions: Map<String, List<String>>
        get() {
            val json = prefs.getString(KEY_REGIONS_CACHE, null) ?: return emptyMap()
            return try {
                val type = object : TypeToken<Map<String, List<String>>>() {}.type
                gson.fromJson(json, type) ?: emptyMap()
            } catch (_: Exception) {
                emptyMap()
            }
        }
        set(value) {
            prefs.edit().putString(KEY_REGIONS_CACHE, gson.toJson(value)).apply()
        }

    /** 캐시 저장 시각 (System.currentTimeMillis()) */
    var regionsCacheTimestamp: Long
        get() = prefs.getLong(KEY_REGIONS_CACHE_TS, 0L)
        set(value) { prefs.edit().putLong(KEY_REGIONS_CACHE_TS, value).apply() }

    // ─── 배출 완료 기록 ────────────────────────────────────────
    /** 전체 배출 완료 기록 리스트 (JSON 직렬화) */
    var disposalRecords: List<DisposalRecord>
        get() {
            val json = prefs.getString(KEY_DISPOSAL, null) ?: return emptyList()
            return try {
                val type = object : TypeToken<List<DisposalRecord>>() {}.type
                gson.fromJson(json, type) ?: emptyList()
            } catch (_: Exception) { emptyList() }
        }
        set(value) { prefs.edit().putString(KEY_DISPOSAL, gson.toJson(value)).apply() }

    // ─── Zone DTO 캐시 (시도+시군구별) ────────────────────────
    private fun zoneItemsKey(city: String, district: String) = "${KEY_ZONE_ITEMS}_${city}_${district}"
    private fun zoneItemsTsKey(city: String, district: String) = "${KEY_ZONE_ITEMS_TS}_${city}_${district}"

    fun getCachedZoneItems(city: String, district: String): List<WasteItemDto> {
        val json = prefs.getString(zoneItemsKey(city, district), null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<WasteItemDto>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (_: Exception) { emptyList() }
    }

    fun setCachedZoneItems(city: String, district: String, items: List<WasteItemDto>) {
        prefs.edit().putString(zoneItemsKey(city, district), gson.toJson(items)).apply()
    }

    fun getZoneItemsCacheTimestamp(city: String, district: String): Long =
        prefs.getLong(zoneItemsTsKey(city, district), 0L)

    fun setZoneItemsCacheTimestamp(city: String, district: String, ts: Long) {
        prefs.edit().putLong(zoneItemsTsKey(city, district), ts).apply()
    }

    // ─── 월별 검색 횟수 ────────────────────────────────────────
    /** yearMonth = "2025-05" 형식으로 저장 */
    fun getMonthlySearchCount(yearMonth: String): Int =
        prefs.getInt("${KEY_SEARCH_COUNT}$yearMonth", 0)

    fun incrementMonthlySearchCount(yearMonth: String) {
        val key = "${KEY_SEARCH_COUNT}$yearMonth"
        prefs.edit().putInt(key, prefs.getInt(key, 0) + 1).apply()
    }

    companion object {
        private const val PREFS_NAME = "recycle_helper_prefs"
        const val KEY_CITY       = "selected_city"
        const val KEY_DISTRICT   = "selected_district"
        const val KEY_FAVORITES  = "favorites"
        const val KEY_RECENT     = "recent_queries"
        const val KEY_NOTIFY     = "notify_enabled"
        private const val KEY_REGIONS_CACHE    = "regions_cache"
        private const val KEY_REGIONS_CACHE_TS = "regions_cache_ts"
        private const val KEY_DISPOSAL         = "disposal_records"
        private const val KEY_SEARCH_COUNT     = "search_count_"
        private const val KEY_ZONE_ITEMS       = "zone_items"
        private const val KEY_ZONE_ITEMS_TS    = "zone_items_ts"

        private const val KEY_NOTIFY_HOUR      = "notify_hour"
        private const val KEY_NOTIFY_MINUTE    = "notify_minute"
        private const val KEY_NOTIFY_ADVANCE   = "notify_advance_days"
        private const val KEY_NOTIFY_TYPES     = "notify_waste_types"
        val DEFAULT_NOTIFY_TYPES = setOf("재활용품", "생활쓰레기", "음식물쓰레기")

        private const val SEPARATOR = ""
        const val MAX_RECENT = 10

        /** 캐시 유효 기간: 24시간 */
        const val CACHE_TTL_MS = 24L * 60 * 60 * 1_000
    }
}
