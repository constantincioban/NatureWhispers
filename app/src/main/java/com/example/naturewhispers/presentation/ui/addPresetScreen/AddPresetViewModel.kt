package com.example.naturewhispers.presentation.ui.addPresetScreen

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.local.db.PresetDao
import com.example.naturewhispers.data.entities.Preset
import com.example.naturewhispers.data.local.predefined.LocalData
import com.example.naturewhispers.navigation.Screens
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.ContentType
import com.example.naturewhispers.presentation.redux.Store
import com.example.naturewhispers.presentation.ui.mainScreen.MainEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPresetViewModel @Inject constructor(
    private val dao: PresetDao,
    savedStateHandle: SavedStateHandle,
    private val store: Store<AppState>
) : ViewModel() {


    private var state = mutableStateOf(AddPresetState())
    val uiState: State<AddPresetState> = state

    private val _eventChannel = Channel<String>()
    val eventChannel = _eventChannel.receiveAsFlow()

    init {

        savedStateHandle.get<Int>(Screens.Preset.presetIdArg)?.let { presetId ->
            if (presetId == 0) return@let

            viewModelScope.launch {
                val preset = dao.getPresetById(presetId) ?: return@launch
                Log.i(TAG, "[edit]: $preset")
                state.value = state.value.copy(
                    presetId = presetId,
                    title = preset.title,
                    duration = preset.duration.toFloat() / 60f,
                    chosenPreliminarySound = preset.sound,
                    chosenSound = preset.sound,
                )
            }
        }
    }

    fun sendEvent(event: AddPresetEvents) = viewModelScope.launch {
        when (event) {
            AddPresetEvents.OnAddPreset -> addPreset()
            is AddPresetEvents.OnDurationChanged -> updateDuration(event.duration)
            is AddPresetEvents.OnChosenSoundChanged -> updateChosenSound()
            is AddPresetEvents.OnTitleChanged -> updateTitle(event.title)
            is AddPresetEvents.OnToggleShowSoundListDialog -> toggleSoundListDialog()
            is AddPresetEvents.OnPlayingSoundChanged -> updatePlayingSound(event.playingSound)
            is AddPresetEvents.OnUpdateChosenPreliminarySound -> updateChosenPreliminarySound(event.sound)
            is AddPresetEvents.OnDeletePreset -> deletePreset(event.presetId)
            AddPresetEvents.OnToggleShowDeleteDialog -> toggleDeleteDialog()
            is AddPresetEvents.OnUpdateFileUri -> updateFileUri(event.fileUri)
            is AddPresetEvents.OnUpdateMaxDuration -> updateMaxDuration(event.duration)
            is AddPresetEvents.OnUpdateContentType -> updateContentType(event.type)
        }
    }

    private fun updateContentType(type: ContentType) = viewModelScope.launch {
        store.update { it.copy(contentType = type) }
    }


    private fun updateMaxDuration(duration: Float) {
        state.value = state.value.copy(maxDuration = duration, duration = 0f)
    }

    private fun updateFileUri(fileUri: String) {
        state.value = state.value.copy(fileUri = fileUri)
    }


    private fun toggleDeleteDialog() {
        state.value = state.value.copy(showDeleteDialog = !state.value.showDeleteDialog)
    }

    private fun deletePreset(presetId: Int) = viewModelScope.launch {
        dao.deletePresetById(presetId)
    }

    private fun updateChosenPreliminarySound(sound: String) {
        state.value = state.value.copy(chosenPreliminarySound = sound)
    }

    private fun updatePlayingSound(sound: String) {
        state.value = state.value.copy(playingSound = sound)
    }

    private fun toggleSoundListDialog() {
        state.value = state.value.copy(showSoundListDialog = !state.value.showSoundListDialog)
    }

    private suspend fun addPreset() {
        if (!validate()) return

        dao.upsertPreset(
            Preset(
                id = state.value.presetId,
                title = state.value.title,
                sound = state.value.chosenSound,
                duration = state.value.duration.toInt() * 60,
                userId = 0,
                fileUri = state.value.fileUri
            )
        )
        state.value = state.value.copy(presetAddedSuccessfully = true)
    }

    private fun validate(): Boolean {
        val duration = state.value.duration
        val title = state.value.title
        val sound = state.value.chosenSound

        if (title.isEmpty()) {
            sendEvent("Title cannot be empty!")
            return false
        }
        if (duration < 1) {
            sendEvent("Duration is not valid")
            return false
        }
        if (sound.isEmpty()) {
            sendEvent("Pick a nature sound!")
            return false
        }

        return true
    }

    private fun updateDuration(duration: Float) {
        state.value = state.value.copy(duration = duration)
    }

    private fun updateChosenSound() {
        state.value = state.value.copy(chosenSound = state.value.chosenPreliminarySound)
        if (LocalData.meditationSounds.map { it.key.title }.contains(state.value.chosenPreliminarySound))
            state.value = state.value.copy(maxDuration = 3600f)
    }

    private fun updateTitle(title: String) {
        if (title.length <= 20)
            state.value = state.value.copy(title = title)
        else sendEvent("No more than 20 characters")
    }

    private fun sendEvent(message: String) = viewModelScope.launch {
        _eventChannel.send(message)
    }


}

sealed interface AddPresetEvents {
    data class OnTitleChanged(val title: String) : AddPresetEvents
    data class OnDurationChanged(val duration: Float) : AddPresetEvents
    data object OnChosenSoundChanged : AddPresetEvents
    data object OnAddPreset : AddPresetEvents
    data object OnToggleShowSoundListDialog : AddPresetEvents
    data object OnToggleShowDeleteDialog : AddPresetEvents
    data class OnPlayingSoundChanged(val playingSound: String) : AddPresetEvents
    data class OnUpdateChosenPreliminarySound(val sound: String) : AddPresetEvents
    data class OnDeletePreset(val presetId: Int) : AddPresetEvents
    data class OnUpdateFileUri(val fileUri: String) : AddPresetEvents
    data class OnUpdateMaxDuration(val duration: Float): AddPresetEvents
    data class OnUpdateContentType(val type: ContentType) : AddPresetEvents

}

data class AddPresetState(
    val presetId: Int = 0,
    val title: String = "",
    val duration: Float = 0f,
    val maxDuration: Float = 60 * 60f,
    val presetAddedSuccessfully: Boolean = false,
    val showSoundListDialog: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val chosenSound: String = "",
    val chosenPreliminarySound: String = "",
    val fileUri: String = "",
    val playingSound: String = "",
)