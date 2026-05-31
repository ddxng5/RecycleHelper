package com.example.recyclehelper

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import com.example.recyclehelper.data.auth.UserSessionManager
import com.example.recyclehelper.data.local.WasteItemStore
import com.example.recyclehelper.notification.NotificationHelper
import com.example.recyclehelper.ui.auth.LoginActivity
import com.example.recyclehelper.ui.theme.RecycleHelperTheme

class MainActivity : ComponentActivity() {

    private var initialTabState = mutableStateOf(Tab.SEARCH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WasteItemStore.ensureLoaded(applicationContext)
        NotificationHelper.ensureChannel(this)
        enableEdgeToEdge()

        // 세션 확인 — 로그인 안 된 경우 LoginActivity로 전환
        if (!UserSessionManager(this).isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        initialTabState.value = resolveTab(intent)

        setContent {
            RecycleHelperTheme {
                val tab by initialTabState
                MainScreen(
                    initialTab = tab,
                    onLogout = {
                        NotificationHelper.cancelDaily(this)
                        UserSessionManager(this).logout()
                        startActivity(
                            Intent(this, LoginActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        initialTabState.value = resolveTab(intent)
    }

    private fun resolveTab(intent: Intent?): Tab {
        return when (intent?.getStringExtra(NotificationHelper.EXTRA_TARGET_TAB)) {
            NotificationHelper.TAB_TODAY    -> Tab.TODAY
            NotificationHelper.TAB_CALENDAR -> Tab.CALENDAR
            else -> Tab.SEARCH
        }
    }
}
