package com.example.naturewhispers.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.naturewhispers.data.entities.Preset
import com.example.naturewhispers.data.entities.Stat
import kotlinx.coroutines.flow.Flow

@Dao
interface StatDao {

    @Upsert
    suspend fun upsertStat(stat: Stat)

    @Query("select * from stats")
    fun getStats(): Flow<List<Stat>>

    @Query("select * from stats where :id = id")
    fun getStatById(id: Int): Stat?

    @Delete
    suspend fun deleteStat(stat: Stat)

    @Transaction
    @Query("select * from stats where id = :presetId")
    fun  getStatsByPresetId(presetId: Int): Flow<List<Stat>>
}