package com.example.recyclehelper.data.repository

import com.example.recyclehelper.BuildConfig
import com.example.recyclehelper.data.model.BulkWasteInfo
import com.example.recyclehelper.data.model.Category
import com.example.recyclehelper.data.model.WasteTypeInfo
import com.example.recyclehelper.data.model.ZoneInfo
import android.util.Log
import com.example.recyclehelper.data.remote.RetrofitClient
import com.example.recyclehelper.data.remote.dto.WasteItemDto
import java.time.DayOfWeek

class WasteRepository {

    private val api = RetrofitClient.wasteApi

    /**
     * 공공데이터 API 는 한 번 요청에 처리할 수 있는 건수 제한이 있다.
     * 1,000건씩 페이지를 나눠 totalCount 에 도달할 때까지 전부 수집한다.
     */
    private val PAGE_SIZE = 1_000
    private val TAG = "WasteRepository"

    suspend fun getAvailableRegions(): Map<String, List<String>> {
        return try {
            val allItems = mutableListOf<WasteItemDto>()
            var pageNo = 1
            var totalCount = -1

            while (true) {
                val response = api.getWasteInfo(
                    serviceKey = BuildConfig.WASTE_API_KEY,
                    pageNo    = pageNo,
                    numOfRows = PAGE_SIZE
                )

                val resultCode = response.response.header.resultCode.trimStart('0').ifEmpty { "0" }
                val isSuccess  = resultCode == "0" ||
                        response.response.header.resultMsg.contains("정상")
                if (!isSuccess) break

                // 첫 응답에서 전체 건수 확인
                if (totalCount < 0) {
                    totalCount = response.response.body.totalCount
                    Log.d(TAG, "getAvailableRegions: totalCount=$totalCount")
                }

                val pageItems = response.response.body.items?.item.orEmpty()
                if (pageItems.isEmpty()) break

                allItems += pageItems
                Log.d(TAG, "getAvailableRegions: page=$pageNo fetched=${pageItems.size} accumulated=${allItems.size}")

                if (allItems.size >= totalCount) break
                pageNo++
            }

            allItems
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

    suspend fun getZones(cityName: String, districtName: String): List<ZoneInfo> {
        return try {
            val allItems = mutableListOf<WasteItemDto>()
            var pageNo = 1
            var totalCount = -1

            while (true) {
                val response = api.getWasteInfo(
                    serviceKey   = BuildConfig.WASTE_API_KEY,
                    cityName     = cityName,
                    districtName = districtName,
                    pageNo       = pageNo,
                    numOfRows    = PAGE_SIZE
                )

                val resultCode = response.response.header.resultCode.trimStart('0').ifEmpty { "0" }
                val isSuccess  = resultCode == "0" ||
                        response.response.header.resultMsg.contains("정상")
                if (!isSuccess) break

                if (totalCount < 0) {
                    totalCount = response.response.body.totalCount
                    Log.d(TAG, "getZones[$cityName $districtName]: totalCount=$totalCount")
                }

                val pageItems = response.response.body.items?.item.orEmpty()
                if (pageItems.isEmpty()) break

                allItems += pageItems
                if (allItems.size >= totalCount) break
                pageNo++
            }

            allItems
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
