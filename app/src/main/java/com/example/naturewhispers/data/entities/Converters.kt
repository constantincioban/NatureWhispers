package com.example.naturewhispers.data.entities

import androidx.room.TypeConverter
import com.example.naturewhispers.data.utils.fromJson
import com.google.gson.Gson

class Converters {
    @TypeConverter
    fun fromPresetsIdsList(value: List<Int>): String = Gson().toJson(value)

    @TypeConverter
    fun toPresetsIdsList(value: String): List<Int> = Gson().fromJson<List<Int>>(value)
}
