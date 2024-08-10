package com.example.naturewhispers.data.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

data class UserWithPresets(
    @Embedded val user: User,
    @Relation(
        parentColumn = "id",
        entityColumn = "userId"
    )
    val presets: List<Preset>
)