package com.example.naturewhispers.data.local.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.naturewhispers.data.entities.User
import com.example.naturewhispers.data.entities.UserWithPresets

@Dao
interface UserDao {

    @Upsert
    suspend fun upsertUser(user: User)

    @Query("select * from users where :id = id")
    fun getUserById(id: Int): User?

    @Delete
    suspend fun deleteUser(user: User)

    @Transaction
    @Query("select * from users where id = :presetId")
    suspend fun  getUserWithPresetsById(presetId: Int): UserWithPresets?
}