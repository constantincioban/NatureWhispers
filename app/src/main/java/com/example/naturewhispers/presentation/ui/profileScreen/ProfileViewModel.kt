package com.example.naturewhispers.presentation.ui.profileScreen

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.local.preferences.SettingsManager
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val store: Store<AppState>,
): ViewModel() {


    private var _state = mutableStateOf(ProfileState())
    val uiState: State<ProfileState> = _state

    init {
        viewModelScope.launch {
            val username = store.state.value.username
            val dailyGoal = store.state.value.dailyGoal
            val darkTheme = store.state.value.darkTheme
            _state.value = _state.value.copy(
                username = username,
                usernamePreliminary = username,
                dailyGoal = dailyGoal,
                dailyGoalPreliminary = dailyGoal,
                darkTheme = darkTheme.toBoolean()
            )
        }
    }

    fun sendEvent(event: ProfileEvents) {
        when (event) {
            is ProfileEvents.OnUpdateDailyGoal -> updateDailyGoal(event.dailyGoal)
            is ProfileEvents.OnUpdateUsername -> updateUsername(event.username)
            is ProfileEvents.OnSave -> saveProfileChanges()
            is ProfileEvents.OnUpdateTheme -> updateTheme(event.darkTheme)
        }
    }

    private fun updateTheme(darkTheme: Boolean) = viewModelScope.launch {
        store.update { it.copy(darkTheme = darkTheme.toString()) }
        _state.value = _state.value.copy(darkTheme = darkTheme)
        settingsManager.saveStringSetting(SettingsManager.DARK_THEME, darkTheme.toString())
    }

    private fun saveProfileChanges() {
        val dailyGoal = _state.value.dailyGoalPreliminary.ifEmpty { "1" }
        val username = _state.value.usernamePreliminary
        _state.value = _state.value.copy(
            username = username,
            dailyGoal = dailyGoal,
        )

        viewModelScope.launch {
            settingsManager.saveIntSetting(SettingsManager.DAILY_GOAL, dailyGoal.toInt())
            settingsManager.saveStringSetting(SettingsManager.USERNAME, username)
            store.update { it.copy(username = username, dailyGoal = dailyGoal) }
        }

    }

    private fun updateDailyGoal(dailyGoal: String) {
        if ((dailyGoal.length < 4 && dailyGoal.all { it.isDigit() }) || dailyGoal.isEmpty())
            _state.value = _state.value.copy(dailyGoalPreliminary = dailyGoal.trim())
    }

    private fun updateUsername(username: String) {
        _state.value = _state.value.copy(usernamePreliminary = username)
    }

}

data class ProfileState(
    val username: String = "",
    val dailyGoal: String = "",
    val usernamePreliminary: String = "",
    val dailyGoalPreliminary: String = "",
    val darkTheme: Boolean = false,
)

sealed interface ProfileEvents{
    data class OnUpdateUsername(val username: String): ProfileEvents
    data class OnUpdateDailyGoal(val dailyGoal: String): ProfileEvents
    data class OnUpdateTheme(val darkTheme: Boolean): ProfileEvents
    data object OnSave: ProfileEvents
}