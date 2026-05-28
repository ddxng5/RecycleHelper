package com.example.recyclehelper.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recyclehelper.data.mock.MockData
import com.example.recyclehelper.data.model.RecycleItem
import com.example.recyclehelper.data.model.RegionData
import com.example.recyclehelper.data.model.ZoneInfo
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
    val selectedDistrict: String = RegionData.DEFAULT_DISTRICT
)

class SearchViewModel : ViewModel() {

    private val repository = WasteRepository()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    init {
        loadAvailableRegions()
        loadZones(_uiState.value.selectedCity, _uiState.value.selectedDistrict)
    }

    fun onQueryChange(query: String) {
        val results = MockData.search(query).map {
            it.copy(isFavorite = _uiState.value.favorites.contains(it.id))
        }
        _uiState.value = _uiState.value.copy(query = query, results = results)
    }

    fun toggleFavorite(item: RecycleItem) {
        val current = _uiState.value.favorites.toMutableSet()
        if (current.contains(item.id)) current.remove(item.id) else current.add(item.id)
        _uiState.value = _uiState.value.copy(
            favorites = current,
            results = _uiState.value.results.map {
                if (it.id == item.id) it.copy(isFavorite = current.contains(it.id)) else it
            }
        )
    }

    fun updateRegion(city: String, district: String) {
        _uiState.value = _uiState.value.copy(
            selectedCity = city,
            selectedDistrict = district,
            selectedZoneIndex = 0,
            zones = emptyList(),
            errorMessage = null
        )
        loadZones(city, district)
    }

    fun selectZone(index: Int) {
        if (index in _uiState.value.zones.indices) {
            _uiState.value = _uiState.value.copy(selectedZoneIndex = index)
        }
    }

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
                loadZones(city, district)
            }
        }
    }

    fun getFavoriteItems(): List<RecycleItem> {
        val favIds = _uiState.value.favorites
        return MockData.items
            .filter { favIds.contains(it.id) }
            .map { it.copy(isFavorite = true) }
    }
}
