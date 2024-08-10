package com.example.naturewhispers.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

class SettingsManagerImpl(
    private val context: Context
) : SettingsManager {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")


    override suspend fun saveIntSetting(key: String, value: Int) {
        val dataStoreKey = intPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    override suspend fun saveBooleanSetting(key: String, value: Boolean) {
        val dataStoreKey = booleanPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    override suspend fun saveStringSetting(key: String, value: String) {
        val dataStoreKey = stringPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value
        }
    }

    override suspend fun saveStringListSetting(key: String, value: List<String>) {
        val dataStoreKey = stringSetPreferencesKey(key)
        context.dataStore.edit { settings ->
            settings[dataStoreKey] = value.toSet()
        }
    }

    override suspend fun readStringSetting(key: String): String {
        val dataStoreKey = stringPreferencesKey(key)
        val settings = context.dataStore.data.first()
        return settings[dataStoreKey] ?: ""
    }

    override suspend fun readIntSetting(key: String): Int {
        val dataStoreKey = intPreferencesKey(key)
        val settings = context.dataStore.data.first()
        return settings[dataStoreKey] ?: 0
    }

    override suspend fun readBooleanSetting(key: String): Boolean {
        val dataStoreKey = booleanPreferencesKey(key)
        val settings = context.dataStore.data.first()
        return settings[dataStoreKey] ?: true
    }

    override suspend fun readStringListSetting(key: String): List<String> {
        val dataStoreKey = stringSetPreferencesKey(key)
        val settings = context.dataStore.data.first()
        return settings[dataStoreKey]?.toList() ?: listOf()
    }
}

