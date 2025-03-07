package com.example.naturewhispers.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.naturewhispers.data.local.entities.Converters
import com.example.naturewhispers.data.local.entities.Preset
import com.example.naturewhispers.data.local.entities.Stat

@Database(entities = [ Preset::class, Stat::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NWDatabase: RoomDatabase() {
    abstract val presetDao: PresetDao
    abstract val statDao: StatDao
}