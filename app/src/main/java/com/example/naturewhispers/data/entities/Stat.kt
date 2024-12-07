package com.example.naturewhispers.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stats")
data class Stat(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: Long = 0,
    val duration: Long = 0,
    val presetTitle: String = "",
    val presetId: Int = 0,
    val userId: String = "",
    val currentGoal: Int = 0,
)
