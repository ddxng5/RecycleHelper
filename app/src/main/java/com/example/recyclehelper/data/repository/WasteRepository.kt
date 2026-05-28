package com.example.recyclehelper.data.repository

import com.example.recyclehelper.BuildConfig
import com.example.recyclehelper.data.model.BulkWasteInfo
import com.example.recyclehelper.data.model.Category
import com.example.recyclehelper.data.model.WasteTypeInfo
import com.example.recyclehelper.data.model.ZoneInfo
import com.example.recyclehelper.data.remote.RetrofitClient
import com.example.recyclehelper.data.remote.dto.WasteItemDto
import java.time.DayOfWeek

class WasteRepository {

    private val api = RetrofitClient.wasteApi
    private val fullDataRows = 12_000

    suspend fun getAvailableRegions(): Map<String, List<String>> {
        return try {
            val response = api.getWasteInfo(
                serviceKey = BuildConfig.WASTE_API_KEY,
                numOfRows = fullDataRows
            )

            val resultCode = response.response.header.resultCode.trimStart('0').ifEmpty { "0" }
            val isSuccess = resultCode == "0" || response.response.header.resultMsg.contains("정상")
            if (!isSuccess) return emptyMap()

            response.response.body.items?.item
                .orEmpty()
                .mapNotNull { item ->
                    val city = item.ctpvNm?.trim().orEmpty()
                    val district = item.sggNm?.trim().orEmpty()
                    if (city.isBlank() || district.isBlank()) null else city to district
                }
                .groupBy({ it.first }, { it.second })
                .mapValues { (_, districts) -> districts.distinct().sorted() }
                .toSortedMap()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyMap()
        }
    }

    suspend fun getZones(cityName: String, districtName: String): List<ZoneInfo> {
        return try {
            val response = api.getWasteInfo(
                serviceKey = BuildConfig.WASTE_API_KEY,
                cityName = cityName,
                districtName = districtName,
                numOfRows = fullDataRows
            )

            val resultCode = response.response.header.resultCode.trimStart('0').ifEmpty { "0" }
            val isSuccess = resultCode == "0" || response.response.header.resultMsg.contains("정상")
            if (!isSuccess) return emptyList()

            val allItems = response.response.body.items?.item ?: return emptyList()
            val filtered = allItems.filter { item ->
                item.ctpvNm.isSameRegionName(cityName) && item.sggNm.isSameRegionName(districtName)
            }

            filtered.map { item ->
                toZoneInfo(item, cityName, districtName)
            }.distinctBy { zone ->
                listOf(
                    zone.regionName,
                    zone.zoneName,
                    zone.collectionType,
                    zone.collectionPlace
                ).joinToString("|")
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
