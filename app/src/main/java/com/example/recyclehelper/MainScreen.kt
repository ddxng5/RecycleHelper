package com.example.recyclehelper

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recyclehelper.ui.schedule.ScheduleScreen
import com.example.recyclehelper.ui.search.SearchScreen
import com.example.recyclehelper.ui.search.SearchViewModel
import com.example.recyclehelper.ui.settings.SettingsScreen
import com.example.recyclehelper.ui.today.TodayScreen

enum class Tab(val label: String, val icon: ImageVector) {
    SEARCH("검색", Icons.Filled.Search),
    TODAY("오늘 배출", Icons.Filled.Today),
    SCHEDULE("배출 일정", Icons.Filled.CalendarMonth),
    SETTINGS("설정", Icons.Filled.Settings)
}

@Composable
fun MainScreen() {
    var current by remember { mutableStateOf(Tab.SEARCH) }

    // ViewModel 을 여기서 만들어 검색·설정 화면에 공유
    val searchViewModel: SearchViewModel = viewModel()

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
                Tab.SEARCH -> SearchScreen(viewModel = searchViewModel)
                Tab.TODAY -> Box(Modifier.verticalScroll(rememberScrollState())) { TodayScreen() }
                Tab.SCHEDULE -> Box(Modifier.verticalScroll(rememberScrollState())) { ScheduleScreen() }
                Tab.SETTINGS -> Box(Modifier.verticalScroll(rememberScrollState())) {
                    SettingsScreen(searchViewModel = searchViewModel)
                }
            }
        }
    }
}
