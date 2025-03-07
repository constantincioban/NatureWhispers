package com.example.naturewhispers.presentation.ui.addPresetScreen

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.local.db.PresetDao
import com.example.naturewhispers.data.local.entities.Preset
import com.example.naturewhispers.data.local.predefined.PredefinedData
import com.example.naturewhispers.navigation.Screens
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.ContentType
import com.example.naturewhispers.presentation.redux.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class AddPresetViewModel @Inject constructor(
    private val dao: PresetDao,
    savedStateHandle: SavedStateHandle,
    private val store: Store<AppState>
) : ViewModel() {


    private var state = MutableStateFlow(AddPresetState())
    val uiState: StateFlow<AddPresetState> = state


    private var messageIsActive = false
    private val _eventChannel = Channel<String>(0)
    val eventChannel = _eventChannel.receiveAsFlow()

    init {

        savedStateHandle.get<Int>(Screens.AddPreset.presetIdArg)?.let { presetId ->
            if (presetId == 0) return@let

            viewModelScope.launch {
                val preset = dao.getPresetById(presetId) ?: return@launch
                state.value = state.value.copy(
                    presetId = presetId,
                    title = preset.title,
                    duration = preset.duration.toFloat() / 60f,
                    chosenPreliminarySound = preset.sound,
                    chosenSound = preset.sound,
                )

            }
        }
        viewModelScope.launch {
            snapshotFlow { state.value.chosenSound }
                .distinctUntilChanged()
                .collect { chosenSound ->
                    if (PredefinedData.meditationSounds.map { it.key.title }.contains(chosenSound))
                        state.value = state.value.copy(maxDuration = 3600f)
                }
        }
        viewModelScope.launch {
            uiState.map { it.maxDuration }
                .distinctUntilChanged()
                .collect { _ ->
                    println("StateFlow: maxDuration changed")
                    state.value = state.value.copy(duration = 0f)
                }
        }
        /*viewModelScope.launch {
            snapshotFlow { state.value.maxDuration }
                .distinctUntilChanged()
                .collect { _ ->
                    println("snapshotFlow: maxDuration changed ")
                    state.value = state.value.copy(duration = 0f)
                }
        }*/
        viewModelScope.launch {
            snapshotFlow { state.value.title }
                .distinctUntilChanged()
                .collect { title ->
                    println("snapshotFlow: title changed ")
                    if (title.length > 20) {
                        state.value = state.value.copy(title = state.value.title.substring(0, 20))
                        sendEvent(ToastMessages.TitleTooLong)
                    }
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
            is AddPresetEvents.OnSendToastMessage -> sendEvent(event.message)
        }
    }



    private fun updateContentType(type: ContentType) = viewModelScope.launch {
        store.update { it.copy(contentType = type) }
    }


    private fun updateMaxDuration(duration: Float) {
        println("updateMaxDuration: $duration")
        state.value = state.value.copy(maxDuration = duration)
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
                userId = store.state.value.userEmail,
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
            sendEvent(ToastMessages.TitleCannotBeEmpty)
            return false
        }
        if (title.length > 20) {
            sendEvent(ToastMessages.TitleTooLong)
            return false
        }
        if (duration < 1) {
            sendEvent(ToastMessages.DurationIsNotValid)
            return false
        }
        if (sound.isEmpty()) {
            sendEvent(ToastMessages.PickANatureSound)
            return false
        }

        return true
    }

    private fun updateDuration(duration: Float) {
        state.value = state.value.copy(duration = duration)
    }

    private fun updateChosenSound() {
        state.value = state.value.copy(chosenSound = state.value.chosenPreliminarySound)
    }

    private fun updateTitle(title: String) {
        state.value = state.value.copy(title = title)
    }

    private fun sendEvent(toast: ToastMessages) = viewModelScope.launch {
        if (!messageIsActive) {
            _eventChannel.send(toast.message)
            messageIsActive = true
            delay(3500)
            messageIsActive = false
        }
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
    data class OnSendToastMessage(val message: ToastMessages): AddPresetEvents
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

enum class ToastMessages(val message: String) {
    TitleTooLong("Title cannot be longer than 20 characters"),
    TitleCannotBeEmpty("Title cannot be empty"),
    DurationIsNotValid("Duration is not valid"),
    PickANatureSound("Pick a nature sound")
}