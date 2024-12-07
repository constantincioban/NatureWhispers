package com.example.naturewhispers.presentation.ui.mainScreen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.entities.Preset
import com.example.naturewhispers.data.local.db.PresetDao
import com.example.naturewhispers.data.local.db.StatDao
import com.example.naturewhispers.data.entities.Stat
import com.example.naturewhispers.data.local.preferences.SettingsManager
import com.example.naturewhispers.data.utils.ImmutableList
import com.example.naturewhispers.data.utils.countConsecutiveDates
import com.example.naturewhispers.data.utils.isSameDate
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.ContentType
import com.example.naturewhispers.presentation.redux.Store
import com.example.naturewhispers.presentation.ui.profileScreen.ProfileEvents
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class MainViewModel @Inject constructor(
    presetDao: PresetDao,
    private val statDao: StatDao,
    private val settingsManager: SettingsManager,
    private val store: Store<AppState>,
) : ViewModel() {


    private var state = mutableStateOf(MainState())
    val uiState: State<MainState> = state

    var darkTheme: String? = null
        private set

    private var startTimestamp: Long = 0


    init {
        viewModelScope.launch {
            val username =
                settingsManager.readStringSetting(SettingsManager.USERNAME).trim().ifEmpty { "Anonymous" }

            val dailyGoal =
                settingsManager.readIntSetting(SettingsManager.DAILY_GOAL).toString().map { i -> if (i == '0') '1' else i }.joinToString("")

            val profilePicUri =
                settingsManager.readStringSetting(SettingsManager.PROFILE_PIC_URI)

            val darkThemeSetting =
                settingsManager.readStringSetting(SettingsManager.DARK_THEME)

            state.value = state.value.copy(
                username = username,
                dailyGoal = dailyGoal.toFloat(),
                profilePicUri = profilePicUri,
            )

            darkTheme = darkThemeSetting

            store.update {
                it.copy(
                    username = username,
                    dailyGoal = dailyGoal,
                    profilePicUri = profilePicUri,
                    darkTheme = darkThemeSetting
                )
            }
        }
        viewModelScope.launch {
            presetDao.getPresets().collectLatest {
                val presetsFilteredByUser = it.filter { it.userId == store.state.value.userEmail }
                state.value = state.value.copy(presets = ImmutableList(
                    presetsFilteredByUser.reversed()
                ))
                store.update { appState -> appState.copy(presets = presetsFilteredByUser.reversed()) }
            }
        }
        viewModelScope.launch {
            statDao.getStats().collectLatest {
                val statsFilteredByUser = it.filter { it.userId == store.state.value.userEmail }
                state.value = state.value.copy(
                    todaysTime = calculateTodaysTime(statsFilteredByUser),
                    streak = calculateStreak(statsFilteredByUser),
                    stats = ImmutableList(statsFilteredByUser)
                )
            }
        }
        observeCurrentPreset()
    }

    private fun observeCurrentPreset() = viewModelScope.launch {
        snapshotFlow { state.value.currentPreset }.collectLatest {
                state.value = state.value.copy(isBottomSheetShown = it != null)
        }
    }

    fun sendEvent(event: MainEvents) = viewModelScope.launch {
        when (event) {
            is MainEvents.LogStat -> logStat()
            is MainEvents.ToggleIsLoading -> toggleIsLoading()
            is MainEvents.OnUpdateProfilePic -> updateProfilePic(event.uri)
            is MainEvents.LogPreliminaryDuration -> logPreliminaryDuration()
            is MainEvents.OnUpdateContentType -> updateContentType(event.type)
            MainEvents.SetStartDuration -> setStartDuration()
            is MainEvents.OnPresetSelected -> updateCurrentPreset(event.id)
            is MainEvents.OnUpdateTheme -> updateTheme(event.darkTheme)
        }
    }


    private fun updateTheme(darkTheme: Boolean) = viewModelScope.launch {
        store.update { it.copy(darkTheme = darkTheme.toString()) }
        settingsManager.saveStringSetting(SettingsManager.DARK_THEME, darkTheme.toString())
    }

    private fun calculateTodaysTime(stats: List<Stat>): Int {
        return  stats.filter { isSameDate(it.date, System.currentTimeMillis()) }
            .sumOf { TimeUnit.MILLISECONDS.toSeconds(it.duration) }.toInt() / 60
    }

    private fun calculateStreak(stats: List<Stat>): Int {
        val groupedByDates = stats.groupBy { Instant.ofEpochMilli(it.date).atZone(ZoneId.systemDefault()).toLocalDate() }
        val filteredByGoal = groupedByDates.filter {
            it.value.sumOf { it.duration }.milliseconds.inWholeMinutes >= it.value.last().currentGoal
        }
        val firstStatPerDate = filteredByGoal.map { it.value.first() }
        return countConsecutiveDates(firstStatPerDate.map { it.date }.sortedDescending())
    }

    private fun updateContentType(type: ContentType) = viewModelScope.launch {
        store.update { it.copy(contentType = type) }
    }

    private fun setStartDuration() {
        startTimestamp = System.currentTimeMillis()
    }

    private fun logPreliminaryDuration() {
        if (startTimestamp == 0L) return
        val currentDuration = System.currentTimeMillis() - startTimestamp
        state.value = state.value.copy(preliminaryDuration = state.value.preliminaryDuration + currentDuration)
        startTimestamp = 0L
    }

    private fun updateProfilePic(uri: String) = viewModelScope.launch {
        store.update { it.copy(profilePicUri = uri) }
        settingsManager.saveStringSetting(SettingsManager.PROFILE_PIC_URI, uri)
    }

    private fun toggleIsLoading() {
        state.value = state.value.copy(isLoading = !state.value.isLoading)
    }

    private suspend fun logStat() {
        logPreliminaryDuration()
        if (TimeUnit.MILLISECONDS.toSeconds(state.value.preliminaryDuration) > 1L) {
            statDao.upsertStat(
                Stat(
                    duration = state.value.preliminaryDuration,
                    date = System.currentTimeMillis(),
                    presetTitle = state.value.currentPreset?.title ?: "Unknown",
                    presetId = state.value.currentPreset?.id ?: 0,
                    userId = store.state.value.userEmail,
                    currentGoal = store.state.value.dailyGoal.ifEmpty { "1" }.toInt()
                )
            )
        }
        startTimestamp = 0L
        state.value = state.value.copy(preliminaryDuration = 0L)
    }

    private fun updateCurrentPreset(id: Int) {
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
    val todaysTime: Int = 0,
    val streak: Int = 0,
    val presets: ImmutableList<Preset> = ImmutableList(),
    val stats: ImmutableList<Stat> = ImmutableList(),
)

sealed interface MainEvents {
    data object LogStat : MainEvents
    data object SetStartDuration : MainEvents
    data object LogPreliminaryDuration : MainEvents
    data object ToggleIsLoading : MainEvents
    data class OnUpdateProfilePic(val uri: String) : MainEvents
    data class OnUpdateContentType(val type: ContentType) : MainEvents
    data class OnPresetSelected(val id: Int) : MainEvents
    data class OnUpdateTheme(val darkTheme: Boolean): MainEvents

}