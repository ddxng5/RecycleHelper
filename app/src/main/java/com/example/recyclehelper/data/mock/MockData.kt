package com.example.recyclehelper.data.mock

import com.example.recyclehelper.data.local.WasteItemStore
import com.example.recyclehelper.data.model.RecycleItem

/**
 * 품목 사전 접근 위임 객체.
 * 실제 데이터는 assets/waste_items.json 에 있으며 WasteItemStore 가 로드한다.
 * 기존 코드에서 MockData.items / MockData.search() 를 참조하는 곳과 호환을 유지한다.
 */
object MockData {

    val items: List<RecycleItem>
        get() = WasteItemStore.items

    fun search(query: String): List<RecycleItem> = WasteItemStore.search(query)
}
