package com.example.naturewhispers.data.auth

import com.example.naturewhispers.data.local.models.User

interface IAuthHelper {

    suspend fun login(): User?
    fun logout(): Unit
}