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
import com.example.recyclehelper.ui.theme.GreenPrimary
import com.example.recyclehelper.ui.theme.RecycleHelperTheme
import com.example.recyclehelper.util.PasswordHashUtil

class RegisterActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RecycleHelperTheme {
                RegisterScreen(
                    onRegisterSuccess = { userId, nickname ->
                        UserSessionManager(this).login(userId, nickname)
                        startActivity(
                            Intent(this, MainActivity::class.java)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        )
                    },
                    onBack = { finish() }
                )
            }
        }
    }
}

private val ErrorRed = Color(0xFFE53935)

// 필드 아래에 표시할 인라인 에러 문구
@Composable
private fun FieldError(message: String) {
    if (message.isNotBlank()) {
        Text(
            text = message,
            color = ErrorRed,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 4.dp, top = 2.dp)
        )
    }
}

// ─────────────────────────────────────────
//  회원가입 화면 Composable
// ─────────────────────────────────────────
@Composable
internal fun RegisterScreen(
    onRegisterSuccess: (userId: String, nickname: String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val db      = remember { UserDatabase(context) }

    var userId          by remember { mutableStateOf("") }
    var nickname        by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // 필드를 한 번이라도 수정했는지 — 터치 전에는 에러 미표시
    var userIdTouched          by remember { mutableStateOf(false) }
    var nicknameTouched        by remember { mutableStateOf(false) }
    var passwordTouched        by remember { mutableStateOf(false) }
    var confirmPasswordTouched by remember { mutableStateOf(false) }

    // 필드별 에러 메시지
    var userIdError          by remember { mutableStateOf("") }
    var nicknameError        by remember { mutableStateOf("") }
    var passwordError        by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    var generalError         by remember { mutableStateOf("") }


    fun validateUserId(value: String): String = when {
        value.isBlank() -> "아이디를 입력해 주세요."
        value.length < 4 -> "아이디는 4자 이상이어야 합니다."
        !value.matches(Regex("^[a-zA-Z0-9_]+$")) -> "영문, 숫자, 밑줄(_)만 사용할 수 있습니다."
        else -> ""
    }

    fun validateNickname(value: String): String = when {
        value.isBlank() -> "닉네임을 입력해 주세요."
        value.length < 2 -> "닉네임은 2자 이상이어야 합니다."
        else -> ""
    }

    fun validatePassword(value: String): String = when {
        value.isBlank() -> "비밀번호를 입력해 주세요."
        value.length < 6 -> "비밀번호는 6자 이상이어야 합니다."
        else -> ""
    }

    fun validateConfirmPassword(pw: String, confirm: String): String = when {
        confirm.isBlank() -> "비밀번호를 한 번 더 입력해 주세요."
        pw != confirm -> "비밀번호가 일치하지 않습니다."
        else -> ""
    }

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
                .padding(top = 64.dp, bottom = 36.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(R.drawable.ic_app_logo),
                    contentDescription = "앱 로고",
                    modifier = Modifier.size(72.dp)
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "회원가입",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "분리배출 도우미에 오신 것을 환영합니다",
                    color = Color.White.copy(alpha = 0.85f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        // ── 회원가입 카드 ──
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("계정 만들기", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(Modifier.height(8.dp))

                // ── 아이디 ──
                OutlinedTextField(
                    value = userId,
                    onValueChange = {
                        userId = it
                        generalError = ""
                        userIdTouched = true
                        userIdError = validateUserId(it)
                    },
                    label = { Text("아이디") },
                    placeholder = { Text("영문/숫자 조합 (예: user123)", color = Color.LightGray) },
                    singleLine = true,
                    isError = userIdError.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                if (userIdTouched) FieldError(userIdError)

                Spacer(Modifier.height(4.dp))

                // ── 닉네임 ──
                OutlinedTextField(
                    value = nickname,
                    onValueChange = {
                        nickname = it
                        nicknameTouched = true
                        nicknameError = validateNickname(it)
                    },
                    label = { Text("닉네임") },
                    placeholder = { Text("앱에서 사용할 이름 (예: 초록이)", color = Color.LightGray) },
                    singleLine = true,
                    isError = nicknameError.isNotBlank(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                if (nicknameTouched) FieldError(nicknameError)

                Spacer(Modifier.height(4.dp))

                // ── 비밀번호 ──
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordTouched = true
                        passwordError = validatePassword(it)
                        if (confirmPasswordTouched) confirmPasswordError = validateConfirmPassword(it, confirmPassword)
                    },
                    label = { Text("비밀번호") },
                    placeholder = { Text("6자 이상 입력해 주세요", color = Color.LightGray) },
                    singleLine = true,
                    isError = passwordError.isNotBlank(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                if (passwordTouched) FieldError(passwordError)

                Spacer(Modifier.height(4.dp))

                // ── 비밀번호 확인 ──
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordTouched = true
                        confirmPasswordError = validateConfirmPassword(password, it)
                    },
                    label = { Text("비밀번호 확인") },
                    placeholder = { Text("비밀번호를 한 번 더 입력해 주세요", color = Color.LightGray) },
                    singleLine = true,
                    isError = confirmPasswordError.isNotBlank(),
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                if (confirmPasswordTouched) FieldError(confirmPasswordError)

                // 중복 아이디 등 서버 수준 에러
                if (generalError.isNotBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Text(generalError, color = ErrorRed, fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp))
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        val uid  = userId.trim()
                        val nick = nickname.trim()

                        // 모든 필드를 touched 처리 후 한 번에 검사
                        userIdTouched = true; nicknameTouched = true
                        passwordTouched = true; confirmPasswordTouched = true
                        userIdError          = validateUserId(uid)
                        nicknameError        = validateNickname(nick)
                        passwordError        = validatePassword(password)
                        confirmPasswordError = validateConfirmPassword(password, confirmPassword)

                        if (listOf(userIdError, nicknameError, passwordError, confirmPasswordError).any { it.isNotBlank() }) return@Button

                        if (db.isUserIdExists(uid)) {
                            userIdError = "이미 사용 중인 아이디입니다."
                            return@Button
                        }

                        if (db.insertUser(uid, nick, PasswordHashUtil.hash(password))) {
                            onRegisterSuccess(uid, nick)
                        } else {
                            generalError = "회원가입 중 오류가 발생했습니다. 다시 시도해 주세요."
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Text("회원가입 완료", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                TextButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("취소", color = Color.Gray)
                }
            }
        }
    }
}
