package com.example.recyclehelper

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.example.recyclehelper.data.local.WasteItemStore
import com.example.recyclehelper.notification.NotificationHelper
import com.example.recyclehelper.ui.theme.RecycleHelperTheme

class MainActivity : ComponentActivity() {

    private var initialTabState = mutableStateOf(Tab.SEARCH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WasteItemStore.ensureLoaded(applicationContext)
        NotificationHelper.ensureChannel(this)
        enableEdgeToEdge()

        initialTabState.value = resolveTab(intent)

        setContent {
            RecycleHelperTheme {
                val tab by initialTabState
                MainScreen(initialTab = tab)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initialTabState.value = resolveTab(intent)
    }

    private fun resolveTab(intent: Intent?): Tab {
        val targetTab = intent?.getStringExtra(NotificationHelper.EXTRA_TARGET_TAB)
        return if (targetTab == NotificationHelper.TAB_TODAY) Tab.TODAY else Tab.SEARCH
    }
}
