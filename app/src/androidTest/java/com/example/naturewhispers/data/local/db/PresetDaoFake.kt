package com.example.naturewhispers.data.local.db

import com.example.naturewhispers.data.local.entities.Preset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class PresetDaoFake: PresetDao {

    private val presets = mutableListOf<Preset>()

    override suspend fun upsertPreset(preset: Preset) {
        presets.add(preset)
    }

    override fun getPresets(): Flow<List<Preset>> = flow { emit(presets) }

    override suspend fun getPresetById(id: Int): Preset? = presets.find { it.id == id }

    override suspend fun deletePresetById(id: Int) {
        presets.removeIf { it.id == id }
    }

    override suspend fun deletePreset(preset: Preset) {
        presets.remove(preset)
    }

    override suspend fun insertPresets(presets: List<Preset>) {
        presets.forEach {
            upsertPreset(it)

        }
    }
}