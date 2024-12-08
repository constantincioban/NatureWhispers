package com.example.naturewhispers.data.preferences

import com.example.naturewhispers.data.local.preferences.SettingsManager


class SettingsManagerFake: SettingsManager {

    private val integersMap = mutableMapOf<String, Int>()
    private val booleansMap = mutableMapOf<String, Boolean>()
    private val stringsMap = mutableMapOf<String, String>()
    private val stringListsMap = mutableMapOf<String, List<String>>()

    override suspend fun saveUserDetails(
        email: String,
        authPreference: SettingsManager.AuthPreference
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserDetails(): Pair<String, SettingsManager.AuthPreference> {
        TODO("Not yet implemented")
    }


    override suspend fun saveIntSetting(key: String, value: Int) {
        integersMap[key] = value
    }

    override suspend fun saveBooleanSetting(key: String, value: Boolean) {
        booleansMap[key] = value
    }

    override suspend fun saveStringSetting(key: String, value: String) {
        stringsMap[key] = value
    }

    override suspend fun saveStringListSetting(key: String, value: List<String>) {
        stringListsMap[key] = value
    }

    override suspend fun readStringSetting(key: String): String {
        return stringsMap[key] ?: ""
    }

    override suspend fun readIntSetting(key: String): Int {
        return integersMap[key] ?: 0
    }

    override suspend fun readBooleanSetting(key: String): Boolean {
        return booleansMap[key] ?: true
    }

    override suspend fun readStringListSetting(key: String): List<String> {
        return stringListsMap[key] ?: listOf()
    }
}