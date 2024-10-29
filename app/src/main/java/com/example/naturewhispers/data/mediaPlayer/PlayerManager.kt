package com.example.naturewhispers.data.mediaPlayer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.naturewhispers.data.local.models.Audio
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import com.example.naturewhispers.presentation.ui.PlayerEvents
import com.example.naturewhispers.presentation.ui.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlayerManager @Inject constructor(
    private val mediaPlayer: IMediaPlayer,
    private val store: Store<AppState>
) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    private var _state = mutableStateOf(PlayerState())
    val state: State<PlayerState> = _state

    init {
        observeMediaPlayerState()
    }

    private fun observeMediaPlayerState() {
        coroutineScope.launch {
            mediaPlayer.state.collectLatest {
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

    fun dispatchEvent(event: PlayerEvents) {
        coroutineScope.launch {
            when (event) {
                is PlayerEvents.OnTogglePlayPause -> togglePlayPause()
                is PlayerEvents.OnPreparePlayer -> preparePlayer(event.id, event.sound)
                PlayerEvents.OnStopPlayer -> stopPlayer()
                is PlayerEvents.OnSeekTo -> seekTo(event.position)
            }
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
                    durationMillis = preset.duration * 1000L + 1000
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

    fun dispose() {
        coroutineScope.coroutineContext[Job]?.cancel()
    }
}
