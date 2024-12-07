package com.example.naturewhispers.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.naturewhispers.data.entities.Preset
import kotlinx.coroutines.flow.Flow

@Dao
interface PresetDao {

    @Upsert
    suspend fun upsertPreset(preset: Preset)

    @Query("select * from presets")
    fun getPresets(): Flow<List<Preset>>

    @Query("select * from presets where :id = id")
    suspend fun getPresetById(id: Int): Preset?

    @Query("delete from presets where :id = id")
    suspend fun deletePresetById(id: Int)

    @Delete
    suspend fun deletePreset(preset: Preset)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPresets(presets: List<Preset>)
}