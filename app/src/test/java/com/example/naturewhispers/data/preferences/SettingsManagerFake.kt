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
        saveStringSetting(SettingsManager.USER_EMAIL, email)
        saveStringSetting(SettingsManager.AUTH_PREF, authPreference.name)
    }

    override suspend fun getUserDetails(): Pair<String, SettingsManager.AuthPreference> {
        val email = readStringSetting(SettingsManager.USER_EMAIL)
        val authPreference = readStringSetting(SettingsManager.AUTH_PREF).let {
            SettingsManager.AuthPreference.valueOf(it.ifEmpty { SettingsManager.AuthPreference.NONE.name }.uppercase())
        }
        return Pair(email, authPreference)
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