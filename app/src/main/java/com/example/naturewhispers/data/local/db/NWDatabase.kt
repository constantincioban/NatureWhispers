package com.example.naturewhispers.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.naturewhispers.data.entities.Converters
import com.example.naturewhispers.data.entities.Preset
import com.example.naturewhispers.data.entities.Stat
import com.example.naturewhispers.data.entities.User

@Database(entities = [User::class, Preset::class, Stat::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NWDatabase: RoomDatabase() {
    abstract val userDao: UserDao
    abstract val presetDao: PresetDao
    abstract val statDao: StatDao
}