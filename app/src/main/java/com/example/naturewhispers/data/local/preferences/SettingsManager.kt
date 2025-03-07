package com.example.naturewhispers.data.local.preferences

interface SettingsManager {

    companion object{
        const val DAILY_GOAL = "daily_goal"
        const val USERNAME = "username"
        const val PROFILE_PIC_URI = "profile_pic_uri"
        const val DARK_THEME = "dark_theme"
        const val USER_EMAIL = "user_email"
        const val AUTH_PREF = "auth_preference"
        const val CURRENT_PRESET_ID = "current_preset_id"
    }

    enum class AuthPreference {
        USER, GUEST, NONE
    }

    suspend fun saveUserDetails(email: String, authPreference: AuthPreference)

    suspend fun getUserDetails(): Pair<String, AuthPreference>

    suspend fun saveIntSetting(key: String, value: Int)

    suspend fun saveBooleanSetting(key: String, value: Boolean)

    suspend fun saveStringSetting(key: String, value: String)

    suspend fun saveStringListSetting(key: String, value: List<String>)

    suspend fun readStringSetting(key: String): String

    suspend fun readIntSetting(key: String): Int

    suspend fun readBooleanSetting(key: String): Boolean

    suspend fun readStringListSetting(key: String): List<String>
}