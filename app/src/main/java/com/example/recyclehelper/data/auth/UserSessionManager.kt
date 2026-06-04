package com.example.recyclehelper.data.auth

import android.content.Context

/**
 * 현재 로그인 세션을 SharedPreferences 에 저장·관리한다.
 *
 * ⚠️  로컬 데모용 세션입니다.
 *     실제 서비스에서는 서버 토큰(JWT 등) 또는 Firebase Auth를 사용해야 합니다.
 */
class UserSessionManager(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /** 일반 회원 로그인. */
    fun login(userId: String, nickname: String) {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putString(KEY_USER_ID, userId)
            .putString(KEY_NICKNAME, nickname)
            .putBoolean(KEY_IS_GUEST, false)
            .putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
            .apply()
    }

    /** 게스트 로그인. */
    fun loginAsGuest() {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, true)
            .putString(KEY_USER_ID, GUEST_ID)
            .putString(KEY_NICKNAME, "게스트")
            .putBoolean(KEY_IS_GUEST, true)
            .putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
            .apply()
    }

    /** 로그아웃. */
    fun logout() {
        prefs.edit()
            .putBoolean(KEY_LOGGED_IN, false)
            .putBoolean(KEY_IS_GUEST, false)
            .remove(KEY_USER_ID)
            .remove(KEY_NICKNAME)
            .apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_LOGGED_IN, false)
    fun isGuest(): Boolean   = prefs.getBoolean(KEY_IS_GUEST, false)

    /**
     * 현재 userId 반환.
     * 로그인 안 된 경우 기본값 "guest" 를 반환해 PrefsManager 호환성 유지.
     */
    fun getCurrentUserId(): String =
        prefs.getString(KEY_USER_ID, GUEST_ID) ?: GUEST_ID

    fun getCurrentNickname(): String =
        prefs.getString(KEY_NICKNAME, "게스트") ?: "게스트"

    companion object {
        private const val PREF_NAME     = "user_session"
        private const val KEY_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID   = "current_user_id"
        private const val KEY_NICKNAME  = "current_nickname"
        private const val KEY_IS_GUEST  = "is_guest"
        private const val KEY_LOGIN_TIME = "login_time"
        const val GUEST_ID = "guest"
    }
}
