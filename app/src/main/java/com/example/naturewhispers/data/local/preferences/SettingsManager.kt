package com.example.naturewhispers.data.local.preferences

interface SettingsManager {

    companion object{
        const val DAILY_GOAL = "daily_goal"
        const val USERNAME = "username"
        const val PROFILE_PIC_URI = "profile_pic_uri"
        const val DARK_THEME = ""
    }

    suspend fun saveIntSetting(key: String, value: Int)

    suspend fun saveBooleanSetting(key: String, value: Boolean)

    suspend fun saveStringSetting(key: String, value: String)

    suspend fun saveStringListSetting(key: String, value: List<String>)

    suspend fun readStringSetting(key: String): String

    suspend fun readIntSetting(key: String): Int

    suspend fun readBooleanSetting(key: String): Boolean

    suspend fun readStringListSetting(key: String): List<String>
}