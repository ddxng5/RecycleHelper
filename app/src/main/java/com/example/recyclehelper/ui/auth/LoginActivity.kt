package com.example.recyclehelper.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import com.example.recyclehelper.MainActivity
import com.example.recyclehelper.R
import com.example.recyclehelper.data.auth.UserDatabase
import com.example.recyclehelper.data.auth.UserSessionManager
import com.example.recyclehelper.data.local.WasteItemStore
import com.example.recyclehelper.notification.NotificationHelper
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.RecycleHelperTheme
import com.example.recyclehelper.util.PasswordHashUtil

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 품목 DB / 알림 채널 미리 로드
        WasteItemStore.ensureLoaded(applicationContext)
        NotificationHelper.ensureChannel(this)

        // 이미 로그인 상태면 바로 MainActivity로
        if (UserSessionManager(this).isLoggedIn()) {
            goToMain(); return
        }

        setContent {
            RecycleHelperTheme {
                LoginScreen(
                    onLoginSuccess   = { goToMain() },
                    onNavigateToRegister = {
                        startActivity(Intent(this, RegisterActivity::class.java))
                    },
                    onGuestLogin = {
                        UserSessionManager(this).loginAsGuest()
                        goToMain()
                    }
                )
            }
        }
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

// ─────────────────────────────────────────
//  로그인 화면 Composable
// ─────────────────────────────────────────
@Composable
internal fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onGuestLogin: () -> Unit
) {
    val context = LocalContext.current
    val db      = remember { UserDatabase(context) }
    val session = remember { UserSessionManager(context) }

    var userId   by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F8F3))
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ── 헤더 ──
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GreenPrimary)
                .padding(top = 72.dp, bottom = 48.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.ic_app_logo),
                    contentDescription = "앱 로고",
                    modifier = Modifier.size(90.dp)
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    "분리배출 도우미",
                    color = Color.White,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "우리 동네 배출 정보를 편리하게 확인하세요",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // ── 로그인 카드 ──
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("로그인", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(20.dp))

                OutlinedTextField(
                    value = userId,
                    onValueChange = { userId = it; errorMsg = "" },
                    label = { Text("아이디") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; errorMsg = "" },
                    label = { Text("비밀번호") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                if (errorMsg.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(errorMsg, color = Color(0xFFE53935), fontSize = 13.sp)
                }

                Spacer(Modifier.height(20.dp))

                Button(
                    onClick = {
                        val uid = userId.trim()
                        val pw  = password
                        when {
                            uid.isBlank() -> errorMsg = "아이디를 입력해 주세요."
                            pw.isBlank()  -> errorMsg = "비밀번호를 입력해 주세요."
                            else -> {
                                if (db.validateLogin(uid, PasswordHashUtil.hash(pw))) {
                                    session.login(uid, db.getNickname(uid) ?: uid)
                                    onLoginSuccess()
                                } else {
                                    errorMsg = "아이디 또는 비밀번호가 올바르지 않습니다."
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("로그인", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = onNavigateToRegister) {
                        Text("회원가입", color = GreenPrimary, fontWeight = FontWeight.Medium)
                    }
                    TextButton(onClick = onGuestLogin) {
                        Text("게스트로 시작", color = Color.Gray)
                    }
                }
            }
        }
    }
}
