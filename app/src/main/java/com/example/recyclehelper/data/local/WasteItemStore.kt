package com.example.recyclehelper.data.local

import android.content.Context
import com.example.recyclehelper.data.model.Category
import com.example.recyclehelper.data.model.RecycleItem
import com.google.gson.Gson
import java.io.InputStreamReader

/**
 * assets/waste_items.json 을 로드해 RecycleItem 리스트로 변환·캐싱하는 저장소.
 *
 *  - JSON 은 templates + items 구조.
 *    items 의 각 항목은 template 키로 공통 필드(category/wasteGroup/scheduleType/
 *    disposalMethod/caution/disposalLocation/isSpecialWaste)를 상속한다.
 *  - 같은 항목에서 필드를 다시 지정하면 그 값으로 override.
 *  - load() 는 한 번만 실행되도록 보호. 이후에는 캐시된 items 반환.
 *
 *  기존 MockData 와 인터페이스가 호환되도록 search()/items 를 동일하게 노출한다.
 */
object WasteItemStore {

    private const val ASSET_NAME = "waste_items.json"

    @Volatile
    private var cached: List<RecycleItem>? = null

    val items: List<RecycleItem>
        get() = cached ?: emptyList()

    /** 앱 시작 시 1회 호출. 두 번째 호출부터는 무시. */
    fun ensureLoaded(context: Context) {
        if (cached != null) return
        synchronized(this) {
            if (cached != null) return
            cached = try {
                loadFromAssets(context)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    fun search(query: String): List<RecycleItem> {
        if (query.isBlank()) return emptyList()
        val q = query.trim()
        val pool = items
        return pool.filter { item ->
            item.name.contains(q) ||
                item.subCategory.contains(q) ||
                item.wasteGroup.contains(q) ||
                item.keywords.any { it.contains(q) }
        }
    }

    private fun loadFromAssets(context: Context): List<RecycleItem> {
        val json = context.assets.open(ASSET_NAME).use { stream ->
            InputStreamReader(stream, Charsets.UTF_8).use { reader ->
                Gson().fromJson(reader, WasteItemsFile::class.java)
            }
        }

        val templates = json.templates ?: emptyMap()
        val items = json.items ?: return emptyList()

        return items.mapNotNull { raw ->
            val template = templates[raw.template] ?: return@mapNotNull null

            val subCategory = raw.category ?: template.category ?: ""
            val wasteGroup = raw.wasteGroup ?: template.wasteGroup ?: ""
            val scheduleType = raw.scheduleType ?: template.scheduleType ?: ""
            val disposalMethod = raw.disposalMethod ?: template.disposalMethod ?: ""
            val cautionText = raw.caution ?: template.caution ?: ""
            val disposalLocation = raw.disposalLocation ?: template.disposalLocation ?: ""
            val isSpecialWaste = raw.isSpecialWaste ?: template.isSpecialWaste ?: false

            RecycleItem(
                id = raw.id,
                name = raw.name,
                category = mapCategory(wasteGroup, subCategory),
                disposalMethod = disposalMethod,
                disposalLocation = disposalLocation,
                cautions = if (cautionText.isBlank()) emptyList() else listOf(cautionText),
                subCategory = subCategory,
                wasteGroup = wasteGroup,
                scheduleType = scheduleType,
                keywords = raw.keywords ?: emptyList(),
                isSpecialWaste = isSpecialWaste
            )
        }
    }

    /**
     * 신규 wasteGroup/subCategory 를 기존 Category enum(색/라벨용)에 매핑.
     * 기존 UI(칩, 카드 색상)와 호환을 위해서 유지.
     */
    private fun mapCategory(wasteGroup: String, subCategory: String): Category {
        return when (wasteGroup) {
            "재활용폐기물" -> Category.RECYCLABLE
            "음식물류폐기물" -> Category.FOOD
            "생활계 유해폐기물" -> Category.HAZARDOUS
            "대형폐기물" -> Category.LARGE
            "공사장 생활폐기물" -> Category.LARGE
            "폐가전" -> Category.RECYCLABLE
            "의류/섬유류" -> Category.RECYCLABLE
            else -> if (subCategory == "음식물 제외 품목") Category.GENERAL else Category.GENERAL
        }
    }

    // ───────── JSON DTO ─────────
    private data class WasteItemsFile(
        val templates: Map<String, TemplateDto>?,
        val items: List<ItemDto>?
    )

    private data class TemplateDto(
        val category: String?,
        val wasteGroup: String?,
        val scheduleType: String?,
        val disposalMethod: String?,
        val caution: String?,
        val disposalLocation: String?,
        val isSpecialWaste: Boolean?
    )

    private data class ItemDto(
        val id: String,
        val name: String,
        val template: String,
        val keywords: List<String>?,
        val category: String?,
        val wasteGroup: String?,
        val scheduleType: String?,
        val disposalMethod: String?,
        val caution: String?,
        val disposalLocation: String?,
        val isSpecialWaste: Boolean?
    )
}
