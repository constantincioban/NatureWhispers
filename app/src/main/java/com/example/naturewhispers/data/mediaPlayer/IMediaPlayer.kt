package com.example.naturewhispers.data.mediaPlayer

import com.example.naturewhispers.data.local.models.Audio
import com.example.naturewhispers.presentation.ui.PlayerState
import kotlinx.coroutines.flow.Flow

interface IMediaPlayer {

    val state: Flow<PlayerState>
    fun prepare(audio: Audio)
    fun play()
    fun pause()
    fun stop()
    fun seekTo(position: Int)

}