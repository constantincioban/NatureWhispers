package com.example.naturewhispers.presentation.redux

import com.example.naturewhispers.data.local.entities.Preset


data class AppState(
    val username: String = "",
    val userId: String = "",
    val presets: List<Preset> = listOf(),
    val dailyGoal: String = "",
    val profilePicUri: String = "",
    val darkTheme: String = "",
    val isPlaying: Boolean = false,
    val contentType: ContentType? = null,
    val isLoggedIn: Boolean = false,
    val userEmail: String = "",
)

enum class ContentType {
    IMAGE, AUDIO
}
