package com.example.recyclehelper.data.repository

import com.example.recyclehelper.BuildConfig
import com.example.recyclehelper.data.model.BulkWasteInfo
import com.example.recyclehelper.data.model.Category
import com.example.recyclehelper.data.model.WasteTypeInfo
import com.example.recyclehelper.data.model.ZoneInfo
import android.util.Log
import com.example.recyclehelper.data.remote.RetrofitClient
import com.example.recyclehelper.data.remote.dto.WasteItemDto
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.DayOfWeek
import kotlin.math.ceil

class WasteRepository {

    private val api = RetrofitClient.wasteApi

    private val PAGE_SIZE = 1_000
    private val TAG = "WasteRepository"

    /**
     * 1페이지를 먼저 가져와 totalCount를 확인한 뒤,
     * 나머지 페이지를 모두 병렬로 요청해 반환한다.
     */
    private suspend fun fetchAllItems(
        cityName: String? = null,
        districtName: String? = null
    ): List<WasteItemDto> = coroutineScope {
        // 첫 페이지 (순차) — totalCount 확인
        val firstResponse = api.getWasteInfo(
            serviceKey   = BuildConfig.WASTE_API_KEY,
            cityName     = cityName,
            districtName = districtName,
            pageNo       = 1,
            numOfRows    = PAGE_SIZE
        )
        val header = firstResponse.response.header
        val resultCode = header.resultCode.trimStart('0').ifEmpty { "0" }
        if (resultCode != "0" && !header.resultMsg.contains("정상")) return@coroutineScope emptyList()

        val body = firstResponse.response.body
        val totalCount = body.totalCount
        val page1Items = body.items?.item.orEmpty()
        if (page1Items.isEmpty()) return@coroutineScope emptyList()

        Log.d(TAG, "fetchAllItems[$cityName $districtName]: totalCount=$totalCount")

        if (page1Items.size >= totalCount) return@coroutineScope page1Items

        // 나머지 페이지 병렬 요청
        val lastPage = ceil(totalCount.toDouble() / PAGE_SIZE).toInt()
        val deferred = (2..lastPage).map { pageNo ->
            async {
                try {
                    api.getWasteInfo(
                        serviceKey   = BuildConfig.WASTE_API_KEY,
                        cityName     = cityName,
                        districtName = districtName,
                        pageNo       = pageNo,
                        numOfRows    = PAGE_SIZE
                    ).response.body.items?.item.orEmpty()
                } catch (e: Exception) {
                    Log.e(TAG, "fetchAllItems page $pageNo failed", e)
                    emptyList()
                }
            }
        }

        val result = page1Items.toMutableList()
        deferred.awaitAll().forEach { result += it }
        Log.d(TAG, "fetchAllItems[$cityName $districtName]: collected=${result.size}")
        result
    }

    suspend fun getAvailableRegions(): Map<String, List<String>> {
        return try {
            fetchAllItems()
                .mapNotNull { item ->
                    val city     = item.ctpvNm?.trim().orEmpty()
                    val district = item.sggNm?.trim().orEmpty()
                    if (city.isBlank() || district.isBlank()) null else city to district
                }
                .groupBy({ it.first }, { it.second })
                .mapValues { (_, districts) -> districts.distinct().sorted() }
                .toSortedMap()
                .also { Log.d(TAG, "getAvailableRegions: cities=${it.keys}") }
        } catch (e: Exception) {
            Log.e(TAG, "getAvailableRegions failed", e)
            emptyMap()
        }
    }

    /** API에서 원시 DTO 목록을 가져온다. 캐시 전략은 호출자(ViewModel)가 담당한다. */
    suspend fun fetchZoneItems(cityName: String, districtName: String): List<WasteItemDto> {
        return try {
            fetchAllItems(cityName, districtName)
        } catch (e: Exception) {
            Log.e(TAG, "fetchZoneItems failed", e)
            emptyList()
        }
    }

    /** 원시 DTO 목록을 ZoneInfo 리스트로 변환한다 (네트워크 호출 없음, 즉시 반환). */
    fun parseZones(
        items: List<WasteItemDto>,
        cityName: String,
        districtName: String
    ): List<ZoneInfo> =
        items
            .filter { item ->
                item.ctpvNm.isSameRegionName(cityName) &&
                item.sggNm.isSameRegionName(districtName)
            }
            .map { toZoneInfo(it, cityName, districtName) }
            .distinctBy { zone ->
                listOf(zone.regionName, zone.zoneName,
                       zone.collectionType, zone.collectionPlace)
                    .joinToString("|")
            }

    suspend fun getZones(cityName: String, districtName: String): List<ZoneInfo> {
        return try {
            parseZones(fetchZoneItems(cityName, districtName), cityName, districtName)
        } catch (e: Exception) {
            Log.e(TAG, "getZones failed", e)
            emptyList()
        }
    }

    private fun toZoneInfo(
        item: WasteItemDto,
        fallbackCity: String,
        fallbackDistrict: String
    ): ZoneInfo {
        val city = item.ctpvNm?.takeIf { it.isNotBlank() } ?: fallbackCity
        val district = item.sggNm?.takeIf { it.isNotBlank() } ?: fallbackDistrict

        return ZoneInfo(
            regionName = "$city $district",
            zoneName = item.mngZoneNm.orEmpty(),
            targetArea = item.mngZoneTrgtRgnNm.orEmpty(),
            collectionType = item.emsnPlcType.orEmpty(),
            collectionPlace = item.emsnPlc.orEmpty(),
            general = makeWasteTypeInfo(
                Category.GENERAL,
                item.lfWstEmsnDow,
                item.lfWstEmsnBgngTm,
                item.lfWstEmsnEndTm,
                item.lfWstEmsnMthd
            ),
            food = makeWasteTypeInfo(
                Category.FOOD,
                item.fodWstEmsnDow,
                item.fodWstEmsnBgngTm,
                item.fodWstEmsnEndTm,
                item.fodWstEmsnMthd
            ),
            recyclable = makeWasteTypeInfo(
                Category.RECYCLABLE,
                item.rcyclEmsnDow,
                item.rcyclEmsnBgngTm,
                item.rcyclEmsnEndTm,
                item.rcyclEmsnMthd
            ),
            bulk = makeBulkInfo(
                item.bulkBgngTm,
                item.bulkEndTm,
                item.bulkMthd,
                item.bulkPlc
            ),
            uncollectedDay = item.unclltDay.orEmpty(),
            deptName = item.mngDeptNm.orEmpty(),
            deptTel = item.mngDeptTelno.orEmpty()
        )
    }

    private fun makeWasteTypeInfo(
        category: Category,
        dow: String?,
        startTm: String?,
        endTm: String?,
        method: String?
    ): WasteTypeInfo? {
        if (method.isNullOrBlank()) return null
        return WasteTypeInfo(
            category = category,
            dow = dow.orEmpty(),
            startTime = startTm.orEmpty(),
            endTime = endTm.orEmpty(),
            method = method,
            days = parseDayOfWeek(dow)
        )
    }

    private fun makeBulkInfo(
        startTm: String?,
        endTm: String?,
        method: String?,
        place: String?
    ): BulkWasteInfo? {
        if (method.isNullOrBlank()) return null
        return BulkWasteInfo(
            startTime = startTm.orEmpty(),
            endTime = endTm.orEmpty(),
            method = method,
            place = place.orEmpty()
        )
    }

    private fun parseDayOfWeek(raw: String?): List<DayOfWeek> {
        if (raw.isNullOrBlank()) return emptyList()

        val normalized = raw
            .replace("요일", "")
            .replace(" ", "")
            .replace(",", "+")

        if (normalized.contains("매일")) return DayOfWeek.entries.toList()
        if (normalized.contains("평일")) {
            return listOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
            )
        }

        val dayKeys = listOf("월", "화", "수", "목", "금", "토", "일")
        val days = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )

        val range = Regex("([월화수목금토일])~([월화수목금토일])").find(normalized)
        if (range != null) {
            val start = dayKeys.indexOf(range.groupValues[1])
            val end = dayKeys.indexOf(range.groupValues[2])
            if (start in 0..end) return days.subList(start, end + 1)
        }

        return dayKeys.indices
            .filter { normalized.contains(dayKeys[it]) }
            .map { days[it] }
    }

    private fun String?.isSameRegionName(target: String): Boolean {
        val source = this?.trim().orEmpty()
        val normalizedTarget = target.trim()
        return source == normalizedTarget ||
            source.contains(normalizedTarget) ||
            normalizedTarget.contains(source)
    }

}
