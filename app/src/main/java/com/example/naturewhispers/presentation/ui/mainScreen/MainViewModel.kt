package com.example.naturewhispers.presentation.ui.mainScreen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.entities.Preset
import com.example.naturewhispers.data.local.db.PresetDao
import com.example.naturewhispers.data.local.db.StatDao
import com.example.naturewhispers.data.entities.Stat
import com.example.naturewhispers.data.local.preferences.SettingsManager
import com.example.naturewhispers.data.utils.ImmutableList
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.ContentType
import com.example.naturewhispers.presentation.redux.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    presetDao: PresetDao,
    private val statDao: StatDao,
    private val settingsManager: SettingsManager,
    private val store: Store<AppState>,
) : ViewModel() {


    private var state = mutableStateOf(MainState())
    val uiState: State<MainState> = state

    private var startDuration: Long = 0


    init {
        viewModelScope.launch {
            val username = store.state.value.username.ifEmpty {
                settingsManager.readStringSetting(SettingsManager.USERNAME).trim().ifEmpty { "Anonymous" }
            }
            val dailyGoal = store.state.value.dailyGoal.ifEmpty {
                settingsManager.readIntSetting(SettingsManager.DAILY_GOAL).toString().ifEmpty { 1F }
            }.toString()
            val profilePicUri = store.state.value.profilePicUri.ifEmpty {
                settingsManager.readStringSetting(SettingsManager.PROFILE_PIC_URI)
            }
            val darkTheme = store.state.value.darkTheme.ifEmpty {
                settingsManager.readStringSetting(SettingsManager.DARK_THEME)
            }
            store.update {
                it.copy(
                    username = username,
                    dailyGoal = dailyGoal,
                    profilePicUri = profilePicUri,
                    darkTheme = darkTheme
                )
            }
            store.state.collectLatest {
                state.value = state.value.copy(
                    username = it.username,
                    dailyGoal = it.dailyGoal.toFloat(),
                    profilePicUri = it.profilePicUri,
                )
            }
        }
        viewModelScope.launch {
            presetDao.getPresets().collectLatest {
                state.value = state.value.copy(presets = ImmutableList(it.reversed()))
                store.update { appState -> appState.copy(presets = it.reversed()) }
            }
        }
        viewModelScope.launch {
            statDao.getStats().collectLatest {
                state.value = state.value.copy(stats = ImmutableList(it))
            }
        }
    }

    fun sendEvent(event: MainEvents) = viewModelScope.launch {
        when (event) {
            is MainEvents.ToggleBottomSheet -> toggleBottomSheet(event.id)
            is MainEvents.LogStat -> logStat()
            is MainEvents.ToggleIsLoading -> toggleIsLoading()
            is MainEvents.OnUpdateProfilePic -> updateProfilePic(event.uri)
            is MainEvents.LogPreliminaryDuration -> logPreliminaryDuration()
            is MainEvents.OnUpdateContentType -> updateContentType(event.type)
        }
    }

    private fun updateContentType(type: ContentType) = viewModelScope.launch {
        store.update { it.copy(contentType = type) }
    }


    private fun logPreliminaryDuration() {
        if (startDuration == 0L)
            startDuration = System.currentTimeMillis()
        else {
            val currentDuration = System.currentTimeMillis() - startDuration
            state.value =
                state.value.copy(preliminaryDuration = state.value.preliminaryDuration + currentDuration)
            startDuration = 0L
        }
    }

    private fun updateProfilePic(uri: String) = viewModelScope.launch {
        store.update { it.copy(profilePicUri = uri) }
        settingsManager.saveStringSetting(SettingsManager.PROFILE_PIC_URI, uri)
    }

    private fun toggleIsLoading() {
        state.value = state.value.copy(isLoading = !state.value.isLoading)
    }

    private suspend fun logStat() {
        if (state.value.preliminaryDuration == 0L && startDuration == 0L) return

        val duration = if (startDuration != 0L)
            state.value.preliminaryDuration + (System.currentTimeMillis() - startDuration)
            else state.value.preliminaryDuration
        if (duration <= 1) return
        statDao.upsertStat(
            Stat(
                duration = duration,
                date = System.currentTimeMillis(),
                presetTitle = state.value.currentPreset?.title ?: "Unknown",
                currentGoal = store.state.value.dailyGoal.ifEmpty { "1" }.toInt()
            )
        )
        startDuration = 0L
        state.value = state.value.copy(preliminaryDuration = 0L)
    }

    private fun toggleBottomSheet(id: Int) {
        state.value = state.value.copy(isBottomSheetShown = !state.value.isBottomSheetShown,)
        if (id != 0)
            state.value = state.value.copy(currentPreset = state.value.presets.find { it.id == id })
    }

}

data class MainState(
    val currentlyPlayingId: Int = 0,
    val currentPreset: Preset? = null,
    val startDuration: Long = 0,
    val isBottomSheetShown: Boolean = false,
    val isLoading: Boolean = false,
    val username: String = "",
    val dailyGoal: Float = 0F,
    val profilePicUri: String = "",
    val preliminaryDuration: Long = 0,
    val presets: ImmutableList<Preset> = ImmutableList(),
    val stats: ImmutableList<Stat> = ImmutableList(),
)

sealed interface MainEvents {
    data class ToggleBottomSheet(val id: Int) : MainEvents
    data object LogStat : MainEvents
    data object LogPreliminaryDuration : MainEvents
    data object ToggleIsLoading : MainEvents
    data class OnUpdateProfilePic(val uri: String) : MainEvents
    data class OnUpdateContentType(val type: ContentType) : MainEvents
}