package com.example.naturewhispers.presentation.ui

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.local.models.Audio
import com.example.naturewhispers.data.mediaPlayer.IMediaPlayer
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val mediaPlayer: IMediaPlayer,
    private val store: Store<AppState>,
): ViewModel() {

    private var _state = mutableStateOf(PlayerState())
    val state: State<PlayerState> = _state

    init {
        viewModelScope.launch {
            mediaPlayer.state.collectLatest {
                Log.i(TAG, "[PlayerState]: $it")
                _state.value = _state.value.copy(
                    amplitudes = it.amplitudes,
                    isLoading = it.isLoading,
                    isPlaying = it.isPlaying,
                    sound = it.sound,
                    duration = it.duration,
                    currentlyPlayingId = it.currentlyPlayingId,
                    currentPosition = it.currentPosition,
                )
            }
        }
    }

    fun dispatchEvent(event: PlayerEvents) = viewModelScope.launch {
        when (event) {
            is PlayerEvents.OnTogglePlayPause -> togglePlayPause()
            is PlayerEvents.OnPreparePlayer -> preparePlayer(event.id, event.sound)
            PlayerEvents.OnStopPlayer -> stopPlayer()
            is PlayerEvents.OnSeekTo -> seekTo(event.position)
        }
    }

    private fun seekTo(position: Int) {
        mediaPlayer.seekTo(position)
    }

    private suspend fun stopPlayer() {
        mediaPlayer.stop()
        _state.value = _state.value.copy(currentPosition = 0L)
        store.update { it.copy(isPlaying = false) }

    }

    private fun preparePlayer(id: Int, sound: String) {
        store.state.value.presets.find { it.id == id }?.let { preset ->
            mediaPlayer.prepare(
                Audio(
                    title = preset.sound,
                    artist = preset.title,
                    uri = preset.fileUri,
                    durationMillis = preset.duration * 1000L
                )
            )
        } ?: mediaPlayer.prepare(Audio(title = sound, durationMillis = 30 * 1000L))

    }

    private suspend fun togglePlayPause() {
        store.update { it.copy(isPlaying = !store.state.value.isPlaying) }

        if (_state.value.isPlaying) {
            mediaPlayer.pause()
            return
        }

        mediaPlayer.play()
    }

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