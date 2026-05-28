package com.example.recyclehelper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recyclehelper.ui.detail.DetailScreen
import com.example.recyclehelper.ui.favorites.FavoritesScreen
import com.example.recyclehelper.ui.search.SearchScreen
import com.example.recyclehelper.ui.search.SearchViewModel
import com.example.recyclehelper.ui.settings.SettingsScreen
import com.example.recyclehelper.ui.today.TodayScreen

enum class Tab(val label: String, val icon: ImageVector) {
    SEARCH("검색", Icons.Filled.Search),
    TODAY("오늘 배출", Icons.Filled.Today),
    FAVORITE("즐겨찾기", Icons.Filled.Star),
    SETTINGS("지역 설정", Icons.Filled.Settings)
}

@Composable
fun MainScreen(initialTab: Tab = Tab.SEARCH) {
    var current by remember { mutableStateOf(initialTab) }
    var detailItemId by remember { mutableStateOf<String?>(null) }
    val searchViewModel: SearchViewModel = viewModel()

    // 외부에서 initialTab 이 바뀌면 (Activity intent 재진입 등) 반영
    LaunchedEffect(initialTab) {
        current = initialTab
    }

    // 상세 화면 overlay
    val detailItem = detailItemId?.let { searchViewModel.findItem(it) }
    if (detailItem != null) {
        DetailScreen(
            item = detailItem,
            viewModel = searchViewModel,
            onBack = { detailItemId = null }
        )
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = current == tab,
                        onClick = { current = tab },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (current) {
                Tab.SEARCH -> SearchScreen(
                    viewModel = searchViewModel,
                    onItemClick = { item -> detailItemId = item.id }
                )
                Tab.TODAY -> Box(Modifier.verticalScroll(rememberScrollState())) {
                    TodayScreen(viewModel = searchViewModel)
                }
                Tab.FAVORITE -> FavoritesScreen(
                    viewModel = searchViewModel,
                    onItemClick = { item -> detailItemId = item.id }
                )
                Tab.SETTINGS -> Box(Modifier.verticalScroll(rememberScrollState())) {
                    SettingsScreen(searchViewModel = searchViewModel)
                }
            }
        }
    }
}
