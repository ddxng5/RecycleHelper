package com.example.recyclehelper.data.auth

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * 로컬 사용자 계정 DB.
 *
 * ⚠️  로컬 데모용 로그인이며 실제 서비스에서는
 *     서버 인증 또는 Firebase Auth 등이 필요합니다.
 */
class UserDatabase(context: Context) : SQLiteOpenHelper(
    context.applicationContext, DB_NAME, null, DB_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE $TABLE (
                id           INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_UID     TEXT    UNIQUE NOT NULL,
                $COL_NICK    TEXT    NOT NULL,
                $COL_HASH    TEXT    NOT NULL,
                $COL_CREATED INTEGER NOT NULL
            )
            """.trimIndent()
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // 버전업 시 기존 테이블은 유지하고 필요한 컬럼만 추가
    }

    /** 신규 사용자 삽입. 성공 true, 중복·오류 false. */
    fun insertUser(userId: String, nickname: String, passwordHash: String): Boolean = try {
        val cv = ContentValues().apply {
            put(COL_UID, userId)
            put(COL_NICK, nickname)
            put(COL_HASH, passwordHash)
            put(COL_CREATED, System.currentTimeMillis())
        }
        writableDatabase.insert(TABLE, null, cv) != -1L
    } catch (e: Exception) {
        e.printStackTrace(); false
    }

    /** 아이디 중복 여부. */
    fun isUserIdExists(userId: String): Boolean = try {
        readableDatabase
            .rawQuery("SELECT COUNT(*) FROM $TABLE WHERE $COL_UID = ?", arrayOf(userId))
            .use { it.moveToFirst() && it.getInt(0) > 0 }
    } catch (e: Exception) { false }

    /** 아이디 + 비밀번호 해시 검증. */
    fun validateLogin(userId: String, passwordHash: String): Boolean = try {
        readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM $TABLE WHERE $COL_UID = ? AND $COL_HASH = ?",
            arrayOf(userId, passwordHash)
        ).use { it.moveToFirst() && it.getInt(0) > 0 }
    } catch (e: Exception) { false }

    /** userId 에 해당하는 닉네임 반환. 없으면 null. */
    fun getNickname(userId: String): String? = try {
        readableDatabase
            .rawQuery("SELECT $COL_NICK FROM $TABLE WHERE $COL_UID = ?", arrayOf(userId))
            .use { if (it.moveToFirst()) it.getString(0) else null }
    } catch (e: Exception) { null }

    companion object {
        private const val DB_NAME    = "recycle_users.db"
        private const val DB_VERSION = 1
        private const val TABLE      = "users"
        private const val COL_UID     = "user_id"
        private const val COL_NICK    = "nickname"
        private const val COL_HASH    = "password_hash"
        private const val COL_CREATED = "created_at"
    }
}
