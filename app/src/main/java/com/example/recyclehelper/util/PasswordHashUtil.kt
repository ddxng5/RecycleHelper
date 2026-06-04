package com.example.recyclehelper.util

import java.security.MessageDigest

/**
 * 로컬 데모용 비밀번호 단방향 해시 유틸.
 *
 * ⚠️  주의: 이 구현은 기말 프로젝트 수준의 로컬 데모용입니다.
 *         실제 서비스에서는 서버 측 bcrypt / Argon2 솔팅 해시 또는
 *         Firebase Auth, OAuth 등의 외부 인증 시스템을 사용해야 합니다.
 */
object PasswordHashUtil {
    fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
