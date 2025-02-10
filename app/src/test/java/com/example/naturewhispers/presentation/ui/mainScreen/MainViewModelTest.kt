package com.example.naturewhispers.presentation.ui.mainScreen

import app.cash.turbine.test
import com.example.naturewhispers.MainDispatcherRule
import com.example.naturewhispers.data.local.entities.Preset
import com.example.naturewhispers.data.local.db.PresetDao
import com.example.naturewhispers.data.local.db.PresetDaoFake
import com.example.naturewhispers.data.local.db.StatDao
import com.example.naturewhispers.data.local.db.StatDaoFake
import com.example.naturewhispers.data.local.preferences.SettingsManager
import com.example.naturewhispers.data.preferences.SettingsManagerFake
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.ContentType
import com.example.naturewhispers.presentation.redux.Store
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit

@RunWith(JUnit4::class)
class MainViewModelTest {


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: MainViewModel
    private lateinit var store: Store<AppState>
    private lateinit var presetDao: PresetDao
    private lateinit var statDao: StatDao
    private lateinit var settingsManager: SettingsManager


    private val preset = Preset(
        id = 1,
        title = "test_title",
        sound = "test_sound",
        duration = 60_000,
    )

    @Before
    fun setUp() {
        store = Store(AppState())
        presetDao = PresetDaoFake()
        statDao = StatDaoFake()
        settingsManager = SettingsManagerFake()
        viewModel = MainViewModel(presetDao, statDao, settingsManager, store)
    }

    @Test
    fun `init, check that state is initialized with preferences values`() = runTest {
        val username = "test_username"
        val dailyGoal = 10
        val profilePicUri = "test_uri"

        settingsManager.saveStringSetting(SettingsManager.USERNAME, username)
        settingsManager.saveStringSetting(SettingsManager.PROFILE_PIC_URI, profilePicUri)
        settingsManager.saveIntSetting(SettingsManager.DAILY_GOAL, dailyGoal)


        viewModel = MainViewModel(presetDao, statDao, settingsManager, store)
        assertThat(viewModel.uiState.value.username).isEqualTo(username)
        assertThat(viewModel.uiState.value.dailyGoal).isEqualTo(dailyGoal.toFloat())
        assertThat(viewModel.uiState.value.profilePicUri).isEqualTo(profilePicUri)
    }

    @Test
    fun `init, check that store is updated with preferences values`() = runTest {
        val username = "test_username"
        val dailyGoal = 10
        val profilePicUri = "test_uri"

        settingsManager.saveStringSetting(SettingsManager.USERNAME, username)
        settingsManager.saveStringSetting(SettingsManager.PROFILE_PIC_URI, profilePicUri)
        settingsManager.saveIntSetting(SettingsManager.DAILY_GOAL, dailyGoal)

        viewModel = MainViewModel(presetDao, statDao, settingsManager, store)

        assertThat(store.state.value.username).isEqualTo(username)
        assertThat(store.state.value.dailyGoal).isEqualTo(dailyGoal.toString())
        assertThat(store.state.value.profilePicUri).isEqualTo(profilePicUri)
    }

    @Test
    fun `init, check that state has latest presets`() = runTest {
        presetDao.upsertPreset(preset)
        viewModel = MainViewModel(presetDao, statDao, settingsManager, store)
        assertThat(viewModel.uiState.value.presets).containsExactly(preset)
    }

    @Test
    fun `calculateTodaysTime, returns correct value`()  {
        val totalTime = TimeUnit.MILLISECONDS.toSeconds(300_000).toInt() / 60
        (statDao as StatDaoFake).insertThreeHundredThousandTodaysTimeStats()
        viewModel = MainViewModel(presetDao, statDao, settingsManager, store)
        assertThat(viewModel.uiState.value.todaysTime).isEqualTo(totalTime)
    }

    @Test
    fun `calculateStreak, 0 streak, returns correct value`() {
        (statDao as StatDaoFake).insertZeroStreakStats()
        viewModel = MainViewModel(presetDao, statDao, settingsManager, store)
        assertThat(viewModel.uiState.value.streak).isEqualTo(0)
    }

    @Test
    fun `calculateStreak, 1 streak, returns correct value`() {
        (statDao as StatDaoFake).insertOneStreakStats()
        viewModel = MainViewModel(presetDao, statDao, settingsManager, store)
        assertThat(viewModel.uiState.value.streak).isEqualTo(1)
    }

    @Test
    fun `calculateStreak, 3 streak, returns correct value`() {
        (statDao as StatDaoFake).insertThreeStreakStats()
        viewModel = MainViewModel(presetDao, statDao, settingsManager, store)
        assertThat(viewModel.uiState.value.streak).isEqualTo(3)
    }

    @Test
    fun `updateContentType, returns correct value`() {
        viewModel.sendEvent(MainEvents.OnUpdateContentType(ContentType.IMAGE))
        viewModel.sendEvent(MainEvents.OnUpdateContentType(ContentType.AUDIO))
        assertThat(store.state.value.contentType).isEqualTo(ContentType.AUDIO)
    }

    @Test
    fun `logPreliminaryDuration, logs correct value`() = runBlocking {
        viewModel.sendEvent(MainEvents.SetStartDuration)
        delay(1_000)
        viewModel.sendEvent(MainEvents.LogPreliminaryDuration)
        assertThat(viewModel.uiState.value.preliminaryDuration).isLessThan(1_000 + 30)
    }

    @Test
    fun `logPreliminaryDuration, startTimestamp is 0, no logging`() {
        viewModel.sendEvent(MainEvents.LogPreliminaryDuration)
        assertThat(viewModel.uiState.value.preliminaryDuration).isEqualTo(0)
    }

    @Test
    fun `updateProfilePic, updated store`() {
        val uri = "test_uri"
        viewModel.sendEvent(MainEvents.OnUpdateProfilePic(uri))
        assertThat(store.state.value.profilePicUri).isEqualTo(uri)
    }

    @Test
    fun `updateProfilePic, updated settings manager`() = runTest {
        val uri = "test_uri"
        viewModel.sendEvent(MainEvents.OnUpdateProfilePic(uri))
        val profilePicSetting = settingsManager.readStringSetting(SettingsManager.PROFILE_PIC_URI)
        assertThat(profilePicSetting).isEqualTo(uri)
    }

    @Test
    fun `toggleIsLoading, toggles loading value in state`() {
        val isLoading = viewModel.uiState.value.isLoading
        viewModel.sendEvent(MainEvents.ToggleIsLoading)
        assertThat(viewModel.uiState.value.isLoading).isNotEqualTo(isLoading)
    }

    @Test
    fun `logStat, logs correct value`() = runBlocking {
        var statsTotal = -1
        statDao.getStats().test {
            val items = awaitItem()
            statsTotal = items.size
            cancelAndConsumeRemainingEvents()
        }
        viewModel.sendEvent(MainEvents.SetStartDuration)
        delay(2_000)
        viewModel.sendEvent(MainEvents.LogStat)
        statDao.getStats().test {
            val items = awaitItem()
            assertThat(items.size).isEqualTo(statsTotal + 1)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `logStat, duration less than 2, no logging `() = runBlocking {
        var statsTotal = -1
        statDao.getStats().test {
            val items = awaitItem()
            statsTotal = items.size
            cancelAndConsumeRemainingEvents()
        }
        viewModel.sendEvent(MainEvents.SetStartDuration)
        delay(1_000)
        viewModel.sendEvent(MainEvents.LogStat)
        statDao.getStats().test {
            val items = awaitItem()
            assertThat(items.size).isEqualTo(statsTotal)
            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun `updateCurrentPreset, updates currentPreset in state`() = runTest {
        presetDao.upsertPreset(preset)
        viewModel = MainViewModel(presetDao, statDao, settingsManager, store)

        viewModel.sendEvent(MainEvents.OnPresetSelected(preset.id))
        assertThat(viewModel.uiState.value.currentPreset).isEqualTo(preset)
    }










}