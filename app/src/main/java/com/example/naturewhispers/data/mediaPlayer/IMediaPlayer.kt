package com.example.naturewhispers.data.mediaPlayer

import com.example.naturewhispers.data.local.models.Audio
import kotlinx.coroutines.flow.Flow

interface IMediaPlayer {

    val state: Flow<PlayerState>
    fun prepare(audio: Audio)
    fun play()
    fun pause()
    fun stop()
    fun seekTo(position: Int)

}

data class PlayerState(
    val sound: String = "",
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val currentPosition: Long = 0,
    val currentlyPlayingId: Int = 0,
    val duration: Long = 0L,
    val amplitudes: List<Int> = listOf(),
)

sealed interface PlayerEvents {
    data object OnTogglePlayPause: PlayerEvents
    data class OnPreparePlayer(val id: Int = -1, val sound: String = ""): PlayerEvents
    data object OnStopPlayer: PlayerEvents
    data class OnSeekTo(val position: Int): PlayerEvents
}