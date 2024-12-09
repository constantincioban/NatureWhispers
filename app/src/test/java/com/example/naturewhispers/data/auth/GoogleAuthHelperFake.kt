package com.example.naturewhispers.data.auth

import com.example.naturewhispers.data.local.models.User

class GoogleAuthHelperFake: IAuthHelper {


    override suspend fun login(): User? {
        println("logging... ")
        return User(
            name = "Test", email = "test@mail.com"
        )
    }

    override fun logout() {
        println("logout... ")
    }
}