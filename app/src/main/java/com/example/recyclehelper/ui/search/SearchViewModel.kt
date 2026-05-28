package com.example.recyclehelper.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.recyclehelper.data.local.WasteItemStore
import com.example.recyclehelper.data.mock.MockData
import com.example.recyclehelper.data.model.RecycleItem
import com.example.recyclehelper.data.model.RegionData
import com.example.recyclehelper.data.model.WasteCategory
import com.example.recyclehelper.data.model.ZoneInfo
import com.example.recyclehelper.data.prefs.PrefsManager
import com.example.recyclehelper.data.repository.WasteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val results: List<RecycleItem> = emptyList(),
    val favorites: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isRegionLoading: Boolean = false,
    val availableRegions: Map<String, List<String>> = RegionData.cities,
    val zones: List<ZoneInfo> = emptyList(),
    val selectedZoneIndex: Int = 0,
    val errorMessage: String? = null,
    val selectedCity: String = RegionData.DEFAULT_CITY,
    val selectedDistrict: String = RegionData.DEFAULT_DISTRICT,
    val isRegionConfigured: Boolean = false,
    val recentQueries: List<String> = emptyList(),
    val notifyEnabled: Boolean = false,
    val selectedWasteCategory: WasteCategory = WasteCategory.ALL,
    /** 검색 결과가 비었을 때 보여줄 추천 검색어 */
    val recommendedQueries: List<String> = DEFAULT_RECOMMENDED_QUERIES,
    /** 검색이 한 번도 호출되지 않은 상태(첫 진입). 카테고리 필터만 적용한 대표 품목 보여줄 때 사용 */
    val hasNoMatch: Boolean = false
) {
    /** 사용자가 요청한 별칭. zones 를 그대로 노출. */
    val regionItems: List<ZoneInfo> get() = zones
}

private val DEFAULT_RECOMMENDED_QUERIES = listOf(
    "페트병", "종이류", "종이팩", "플라스틱류", "비닐류",
    "유리병", "캔류", "스티로폼", "일반쓰레기", "음식물쓰레기",
    "폐건전지", "폐의약품", "대형폐기물"
)

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = WasteRepository()
    private val prefs = PrefsManager(application)

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        // 앱 시작 시 한 번만 품목 DB 로드 (이미 MainActivity 에서 호출하지만 안전망)
        WasteItemStore.ensureLoaded(application)

        val savedCity = prefs.selectedCity
        val savedDistrict = prefs.selectedDistrict
        val configured = !savedCity.isNullOrBlank() && !savedDistrict.isNullOrBlank()

        val city = savedCity ?: RegionData.DEFAULT_CITY
        val district = savedDistrict ?: RegionData.DEFAULT_DISTRICT

        _uiState.value = _uiState.value.copy(
            selectedCity = city,
            selectedDistrict = district,
            isRegionConfigured = configured,
            favorites = prefs.favorites,
            recentQueries = prefs.recentQueries,
            notifyEnabled = prefs.notifyEnabled
        )

        // 카테고리 = ALL, query = "" 상태이므로 첫 화면은 빈 결과 + 추천 검색어 표시
        loadAvailableRegions()
        loadRegionInfo(city, district)
    }

    fun onQueryChange(query: String) {
        applySearch(query, _uiState.value.selectedWasteCategory)
    }

    /** 검색 액션 (Enter, 검색 버튼, 최근 검색어 클릭 시). 최근 검색어 목록 갱신. */
    fun onSearchCommit(query: String) {
        val trimmed = query.trim()
        applySearch(trimmed, _uiState.value.selectedWasteCategory)
        if (trimmed.isBlank()) return
        val updated = (listOf(trimmed) + _uiState.value.recentQueries.filter { it != trimmed })
            .take(PrefsManager.MAX_RECENT)
        prefs.recentQueries = updated
        _uiState.value = _uiState.value.copy(recentQueries = updated)
    }

    fun selectWasteCategory(category: WasteCategory) {
        applySearch(_uiState.value.query, category)
    }

    fun clearRecentQueries() {
        prefs.recentQueries = emptyList()
        _uiState.value = _uiState.value.copy(recentQueries = emptyList())
    }

    fun toggleFavorite(item: RecycleItem) {
        val current = _uiState.value.favorites.toMutableSet()
        if (current.contains(item.id)) current.remove(item.id) else current.add(item.id)
        prefs.favorites = current
        _uiState.value = _uiState.value.copy(
            favorites = current,
            results = _uiState.value.results.map {
                if (it.id == item.id) it.copy(isFavorite = current.contains(it.id)) else it
            }
        )
    }

    fun updateRegion(city: String, district: String) {
        prefs.selectedCity = city
        prefs.selectedDistrict = district
        _uiState.value = _uiState.value.copy(
            selectedCity = city,
            selectedDistrict = district,
            selectedZoneIndex = 0,
            zones = emptyList(),
            errorMessage = null,
            isRegionConfigured = true
        )
        loadRegionInfo(city, district)
    }

    fun selectZone(index: Int) {
        if (index in _uiState.value.zones.indices) {
            _uiState.value = _uiState.value.copy(selectedZoneIndex = index)
        }
    }

    fun setNotifyEnabled(enabled: Boolean) {
        prefs.notifyEnabled = enabled
        _uiState.value = _uiState.value.copy(notifyEnabled = enabled)
    }

    /** 사용자가 요청한 시그니처. 내부적으로 getZones 를 호출한다. */
    fun loadRegionInfo(city: String, district: String) = loadZones(city, district)

    fun loadZones(city: String, district: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                zones = emptyList(),
                selectedZoneIndex = 0
            )
            try {
                val zones = repository.getZones(city, district)
                _uiState.value = _uiState.value.copy(
                    zones = zones,
                    selectedZoneIndex = 0,
                    isLoading = false,
                    errorMessage = if (zones.isEmpty()) "해당 지역 데이터가 없습니다" else null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "데이터를 불러올 수 없어요"
                )
            }
        }
    }

    private fun loadAvailableRegions() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRegionLoading = true)
            val regions = repository.getAvailableRegions()
            if (regions.isEmpty()) {
                _uiState.value = _uiState.value.copy(isRegionLoading = false)
                return@launch
            }

            val current = _uiState.value
            val currentDistricts = regions[current.selectedCity].orEmpty()
            if (current.selectedDistrict in currentDistricts) {
                _uiState.value = current.copy(
                    availableRegions = regions,
                    isRegionLoading = false
                )
            } else {
                val city = regions.keys.first()
                val district = regions.getValue(city).first()
                _uiState.value = current.copy(
                    availableRegions = regions,
                    selectedCity = city,
                    selectedDistrict = district,
                    selectedZoneIndex = 0,
                    zones = emptyList(),
                    isRegionLoading = false
                )
                loadRegionInfo(city, district)
            }
        }
    }

    fun getFavoriteItems(): List<RecycleItem> {
        val favIds = _uiState.value.favorites
        return MockData.items
            .filter { favIds.contains(it.id) }
            .map { it.copy(isFavorite = true) }
    }

    fun findItem(id: String): RecycleItem? =
        MockData.items.firstOrNull { it.id == id }?.let {
            it.copy(isFavorite = _uiState.value.favorites.contains(it.id))
        }

    // ───────── 8 단계 검색 로직 ─────────
    /**
     * 1) 공백 제거 및 정규화
     * 2) 현재 선택된 카테고리 필터 적용
     * 3) 품목명 완전 일치
     * 4) 품목명 부분 일치
     * 5) keywords 부분 일치
     * 6) subCategory 부분 일치
     * 7) wasteGroup 부분 일치
     * 8) 결과 없으면 추천 검색어/추천 카테고리 노출(UI 에서 처리)
     *
     * query 가 비어있고 카테고리만 선택된 경우, 해당 카테고리에 속하는
     * 대표 품목(현재는 전체 매칭 품목의 앞 30개)을 결과로 반환한다.
     */
    private fun applySearch(rawQuery: String, category: WasteCategory) {
        val q = rawQuery.trim().replace(Regex("\\s+"), " ")
        val favorites = _uiState.value.favorites
        val pool = MockData.items.filter { category.matches(it) }

        val results: List<RecycleItem> = if (q.isBlank()) {
            // query 없음: 카테고리만 선택된 경우 대표 품목 노출 (ALL 이면 빈 리스트)
            if (category == WasteCategory.ALL) emptyList()
            else pool.take(30)
        } else {
            val seen = mutableSetOf<String>()
            val ordered = mutableListOf<RecycleItem>()

            fun addIfNew(item: RecycleItem) {
                if (seen.add(item.id)) ordered.add(item)
            }

            // 3) 완전 일치
            pool.filter { it.name == q }.forEach(::addIfNew)
            // 4) 품목명 부분 일치
            pool.filter { it.name.contains(q) }.forEach(::addIfNew)
            // 5) keywords 부분 일치
            pool.filter { item -> item.keywords.any { it.contains(q) } }.forEach(::addIfNew)
            // 6) subCategory 부분 일치
            pool.filter { it.subCategory.contains(q) }.forEach(::addIfNew)
            // 7) wasteGroup 부분 일치
            pool.filter { it.wasteGroup.contains(q) }.forEach(::addIfNew)

            ordered
        }

        val resultsMarked = results.map { it.copy(isFavorite = favorites.contains(it.id)) }
        _uiState.value = _uiState.value.copy(
            query = rawQuery,
            results = resultsMarked,
            selectedWasteCategory = category,
            hasNoMatch = q.isNotBlank() && resultsMarked.isEmpty()
        )
    }
}
