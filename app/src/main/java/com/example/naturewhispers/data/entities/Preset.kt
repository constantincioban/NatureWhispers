package com.example.naturewhispers.data.entities

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "presets")
data class Preset(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val sound: String = "",
    val duration: Int = 0,
    val userId: String = "",
    val fileUri: String = "",
)
