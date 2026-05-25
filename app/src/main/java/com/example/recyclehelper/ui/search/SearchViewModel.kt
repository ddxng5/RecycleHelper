package com.example.recyclehelper.ui.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recyclehelper.data.mock.MockData
import com.example.recyclehelper.data.model.RecycleItem
import com.example.recyclehelper.data.repository.WasteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 검색 화면 상태
 */
data class SearchUiState(
    val query: String = "",
    val results: List<RecycleItem> = emptyList(),
    val favorites: Set<String> = emptySet(),

    val isLoading: Boolean = false,
    val regionItems: List<RecycleItem> = emptyList(),
    val errorMessage: String? = null,

    val selectedCity: String = "서울특별시",
    val selectedDistrict: String = "강남구"
)

/**
 * 검색 + 즐겨찾기 ViewModel.
 * MainScreen 에서 생성하여 SearchScreen / SettingsScreen 양쪽에 공유한다.
 */
class SearchViewModel : ViewModel() {

    private val repository = WasteRepository()

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    fun onQueryChange(query: String) {
        val results = MockData.search(query).map {
            it.copy(isFavorite = _uiState.value.favorites.contains(it.id))
        }

        _uiState.value = _uiState.value.copy(
            query = query,
            results = results
        )
    }

    fun toggleFavorite(item: RecycleItem) {
        val current = _uiState.value.favorites.toMutableSet()

        if (current.contains(item.id)) {
            current.remove(item.id)
        } else {
            current.add(item.id)
        }

        _uiState.value = _uiState.value.copy(
            favorites = current,
            results = _uiState.value.results.map {
                if (it.id == item.id) {
                    it.copy(isFavorite = current.contains(it.id))
                } else {
                    it
                }
            }
        )
    }

    fun updateRegion(city: String, district: String) {
        _uiState.value = _uiState.value.copy(
            selectedCity = city,
            selectedDistrict = district
        )
        loadRegionInfo(city, district)
    }

    /**
     * 지역 정보 로딩 함수
     */
    fun loadRegionInfo(city: String, district: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                Log.d("API_TEST", "지역 정보 호출 시작: city=$city, district=$district")

                val items = repository.getWasteInfo(
                    cityName = city,
                    districtName = district
                )

                Log.d("API_TEST", "지역 정보 호출 성공: ${items.size}개")
                Log.d("API_TEST", "첫 번째 데이터: ${items.firstOrNull()}")

                _uiState.value = _uiState.value.copy(
                    results = items,
                    regionItems = items,
                    isLoading = false,
                    errorMessage = null
                )

            } catch (e: Exception) {
                Log.e("API_TEST", "지역 정보 호출 실패", e)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "데이터를 불러올 수 없어요"
                )
            }
        }
    }

    /**
     * 즐겨찾기된 품목 전체 목록 반환
     */
    fun getFavoriteItems(): List<RecycleItem> {
        val favIds = _uiState.value.favorites

        return MockData.items
            .filter { favIds.contains(it.id) }
            .map { it.copy(isFavorite = true) }
    }
}