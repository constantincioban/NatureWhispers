package com.example.naturewhispers.presentation.redux

import com.example.naturewhispers.data.entities.Preset


data class AppState(
    val topBarTitle: String = "Main",
    val isLoggedIn: Boolean = false,
    val username: String = "",
    val userId: String = "",
    val presets: List<Preset> = listOf(),
    val dailyGoal: String = "",
    val profilePicUri: String = "",
    val darkTheme: String = "",
    val isPlaying: Boolean = false,
    val isPausedInBackground: Boolean = false,
    val isPlayingInForeground: Boolean = false,
    val showForegroundService: Boolean = false,
    val contentType: ContentType? = null,
)

enum class ContentType {
    IMAGE, AUDIO
}
