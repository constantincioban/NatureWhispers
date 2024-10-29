package com.example.naturewhispers.presentation.ui.profileScreen

import com.example.naturewhispers.MainDispatcherRule
import com.example.naturewhispers.data.local.preferences.SettingsManager
import com.example.naturewhispers.data.preferences.SettingsManagerFake
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ProfileViewModelTest {


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ProfileViewModel
    private lateinit var store: Store<AppState>
    private lateinit var settingsManager: SettingsManager

    @Before
    fun setUp() {
        store = Store(AppState())
        settingsManager = SettingsManagerFake()
        viewModel = ProfileViewModel(settingsManager, store)

    }

    @Test
    fun `sets initial state from store`() = runTest {
        val username = "test_username"
        val dailyGoal = "10"
        val darkTheme = true
        store.update { it.copy(username = username, dailyGoal = dailyGoal, darkTheme = darkTheme.toString()) }
        viewModel = ProfileViewModel(settingsManager, store)
        assertThat(viewModel.uiState.value.username).isEqualTo(username)
        assertThat(viewModel.uiState.value.dailyGoal).isEqualTo(dailyGoal)
        assertThat(viewModel.uiState.value.darkTheme).isEqualTo(darkTheme)
    }

    @Test
    fun `updateTheme, updates darkTheme in state`() {
        val darkTheme = true
        viewModel.sendEvent(ProfileEvents.OnUpdateTheme(darkTheme))
        assertThat(viewModel.uiState.value.darkTheme).isEqualTo(darkTheme)
    }

    @Test
    fun `updateTheme, updates darkTheme in settings manager`() = runTest {
        val darkTheme = true
        viewModel.sendEvent(ProfileEvents.OnUpdateTheme(darkTheme))
        assertThat(settingsManager.readStringSetting(SettingsManager.DARK_THEME).toBoolean()).isEqualTo(darkTheme)
    }

    @Test
    fun `updateTheme, updates darkTheme in store`() = runTest {
        val darkTheme = true
        viewModel.sendEvent(ProfileEvents.OnUpdateTheme(darkTheme))
        assertThat(store.state.value.darkTheme.toBoolean()).isEqualTo(darkTheme)
    }

    @Test
    fun `saveProfileChanges, saves username changes in settings manager`() = runTest {
        val username = "test_username"
        viewModel.sendEvent(ProfileEvents.OnUpdateUsername(username))
        viewModel.sendEvent(ProfileEvents.OnSave)
        assertThat(settingsManager.readStringSetting(SettingsManager.USERNAME)).isEqualTo(username)
    }

    @Test
    fun `saveProfileChanges, saves username changes in store`() = runTest {
        val username = "test_username"
        viewModel.sendEvent(ProfileEvents.OnUpdateUsername(username))
        viewModel.sendEvent(ProfileEvents.OnSave)
        assertThat(store.state.value.username).isEqualTo(username)
    }

    @Test
    fun `saveProfileChanges, saves dailyGoal changes in settings manager`() = runTest {
        val dailyGoal = "10"
        viewModel.sendEvent(ProfileEvents.OnUpdateDailyGoal(dailyGoal))
        viewModel.sendEvent(ProfileEvents.OnSave)
        assertThat(settingsManager.readIntSetting(SettingsManager.DAILY_GOAL)).isEqualTo(dailyGoal.toInt())
    }

    @Test
    fun `saveProfileChanges, saves dailyGoal changes in store`() = runTest {
        val dailyGoal = "10"
        viewModel.sendEvent(ProfileEvents.OnUpdateDailyGoal(dailyGoal))
        viewModel.sendEvent(ProfileEvents.OnSave)
        assertThat(store.state.value.dailyGoal).isEqualTo(dailyGoal)
    }

    @Test
    fun `updateDailyGoal, saves correct value`() {
        val dailyGoal = "10"
        viewModel.sendEvent(ProfileEvents.OnUpdateDailyGoal(dailyGoal))
        assertThat(viewModel.uiState.value.dailyGoalPreliminary).isEqualTo(dailyGoal)
    }

    @Test
    fun `updateDailyGoal, wrong length, does not save`() {
        val dailyGoal = "1000"
        viewModel.sendEvent(ProfileEvents.OnUpdateDailyGoal(dailyGoal))
        assertThat(viewModel.uiState.value.dailyGoalPreliminary).isNotEqualTo(dailyGoal)
    }

    @Test
    fun `updateDailyGoal, not only digits, does not save`() {
        val dailyGoal = "11test"
        viewModel.sendEvent(ProfileEvents.OnUpdateDailyGoal(dailyGoal))
        assertThat(viewModel.uiState.value.dailyGoalPreliminary).isNotEqualTo(dailyGoal)
    }

    @Test
    fun `updateDailyGoal, empty string, does save`() {
        val dailyGoal = ""
        viewModel.sendEvent(ProfileEvents.OnUpdateDailyGoal("test"))
        viewModel.sendEvent(ProfileEvents.OnUpdateDailyGoal(""))
        assertThat(viewModel.uiState.value.dailyGoalPreliminary).isEqualTo(dailyGoal)
    }

    @Test
    fun `updateUsername, saves correct value`() {
        val username = "test_username"
        viewModel.sendEvent(ProfileEvents.OnUpdateUsername(username))
        assertThat(viewModel.uiState.value.usernamePreliminary).isEqualTo(username)
    }

}