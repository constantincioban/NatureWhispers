package com.example.naturewhispers.presentation.ui.addPresetScreen

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.example.naturewhispers.MainDispatcherRule
import com.example.naturewhispers.data.local.entities.Preset
import com.example.naturewhispers.data.local.db.PresetDaoFake
import com.example.naturewhispers.data.local.db.PresetDao
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(JUnit4::class)
class AddPresetViewModelTest {


    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: AddPresetViewModel
    private lateinit var store: Store<AppState>
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var presetDao: PresetDao

    private val preset = Preset(
        id = 0,
        title = "test_title",
        sound = "test_sound",
        duration = 60_000,
    )


    @Before
    fun setUp() {
        store = Store(AppState())
        savedStateHandle = SavedStateHandle()
        presetDao = PresetDaoFake()
        viewModel = AddPresetViewModel(presetDao, savedStateHandle, store)
    }

    @Test
    fun `updateMaxDuration resets duration to 0 when maxDuration is updated`()   {
        viewModel.sendEvent(AddPresetEvents.OnDurationChanged(20f))
        viewModel.sendEvent(AddPresetEvents.OnUpdateMaxDuration(50f))
        assertThat(viewModel.uiState.value.duration).isEqualTo(0f)
    }

    @Test
    fun `updateMaxDuration updates maxDuration in state`() {
        val duration = 120f
        viewModel.sendEvent(AddPresetEvents.OnUpdateMaxDuration(duration))
        assertThat(viewModel.uiState.value.maxDuration).isEqualTo(duration)
    }

    @Test
    fun `updateFileUri, updates fileUri in state`() {
        val fileUri = "test_uri"
        viewModel.sendEvent(AddPresetEvents.OnUpdateFileUri(fileUri))
        assertThat(viewModel.uiState.value.fileUri).isEqualTo(fileUri)
    }

    @Test
    fun `toggleDeleteDialog, toggles showDeleteDialog in state`() {
        viewModel.sendEvent(AddPresetEvents.OnToggleShowDeleteDialog)
        assertThat(viewModel.uiState.value.showDeleteDialog).isTrue()
    }

    @Test
    fun `deletePreset, deletes preset from database`() = runTest {
        val presetId = 1
        viewModel.sendEvent(AddPresetEvents.OnDeletePreset(presetId))
        advanceUntilIdle()
        assertThat(presetDao.getPresetById(presetId)).isNull()
    }

    @Test
    fun `updateChosenPreliminarySound, updates chosenPreliminarySound in state`() {
        val sound = "test_sound"
        viewModel.sendEvent(AddPresetEvents.OnUpdateChosenPreliminarySound(sound))
        assertThat(viewModel.uiState.value.chosenPreliminarySound).isEqualTo(sound)
    }

    @Test
    fun `updatePlayingSound, updates playingSound in state`() {
        val sound = "test_sound"
        viewModel.sendEvent(AddPresetEvents.OnPlayingSoundChanged(sound))
        assertThat(viewModel.uiState.value.playingSound).isEqualTo(sound)
    }

    @Test
    fun `toggleSoundListDialog, toggles showSoundListDialog in state`() {
        viewModel.sendEvent(AddPresetEvents.OnToggleShowSoundListDialog)
        assertThat(viewModel.uiState.value.showSoundListDialog).isTrue()
    }

    @Test
    fun `addPreset, adds preset to database`() = runTest {
        viewModel.sendEvent(AddPresetEvents.OnTitleChanged(preset.title))
        viewModel.sendEvent(AddPresetEvents.OnDurationChanged(preset.duration.toFloat() / 60))
        viewModel.sendEvent(AddPresetEvents.OnUpdateChosenPreliminarySound("test_sound"))
        viewModel.sendEvent(AddPresetEvents.OnChosenSoundChanged)
        viewModel.sendEvent(AddPresetEvents.OnAddPreset)
        advanceUntilIdle()
        assertThat(presetDao.getPresetById(0)).isEqualTo(preset)
    }

    @Test
    fun `addPreset, no title, preset not added`() = runTest {
        viewModel.sendEvent(AddPresetEvents.OnTitleChanged(""))
        viewModel.sendEvent(AddPresetEvents.OnDurationChanged(preset.duration.toFloat() / 60))
        viewModel.sendEvent(AddPresetEvents.OnUpdateChosenPreliminarySound("test_sound"))
        viewModel.sendEvent(AddPresetEvents.OnChosenSoundChanged)
        viewModel.sendEvent(AddPresetEvents.OnAddPreset)
        advanceUntilIdle()
        assertThat(presetDao.getPresetById(0)).isNull()
    }

    @Test
    fun `addPreset, duration is 0, preset not added`() = runTest {
        viewModel.sendEvent(AddPresetEvents.OnTitleChanged(""))
        viewModel.sendEvent(AddPresetEvents.OnUpdateChosenPreliminarySound("test_sound"))
        viewModel.sendEvent(AddPresetEvents.OnChosenSoundChanged)
        viewModel.sendEvent(AddPresetEvents.OnAddPreset)
        advanceUntilIdle()
        assertThat(presetDao.getPresetById(0)).isNull()
    }

    @Test
    fun `addPreset, no sound, preset not added`() = runTest {
        viewModel.sendEvent(AddPresetEvents.OnTitleChanged(preset.title))
        viewModel.sendEvent(AddPresetEvents.OnDurationChanged(preset.duration.toFloat() / 60))
        viewModel.sendEvent(AddPresetEvents.OnAddPreset)
        advanceUntilIdle()
        assertThat(presetDao.getPresetById(0)).isNull()
    }

    @Test
    fun `updateTitle, updates title in state`() {
        val title = "test_title"
        viewModel.sendEvent(AddPresetEvents.OnTitleChanged(title))
        assertThat(viewModel.uiState.value.title).isEqualTo(title)
    }

    @Test
    fun `updateDuration, updates duration in state`() {
        val duration = 20f
        viewModel.sendEvent(AddPresetEvents.OnDurationChanged(duration))
        assertThat(viewModel.uiState.value.duration).isEqualTo(duration)
    }

    @Test
    fun `updateChosenSound, updates chosenSound in state`() {
        val sound = "test_sound"
        viewModel.sendEvent(AddPresetEvents.OnUpdateChosenPreliminarySound(sound))
    }

    @Test
    fun `sendEvent, wrong nature sound, correct message received`() = runTest {
        viewModel.sendEvent(AddPresetEvents.OnTitleChanged(preset.title))
        viewModel.sendEvent(AddPresetEvents.OnDurationChanged(preset.duration.toFloat() / 60))
        viewModel.sendEvent(AddPresetEvents.OnAddPreset)
        advanceUntilIdle()
        viewModel.eventChannel.test {
            val event = awaitItem()
            assertThat(event).isEqualTo(ToastMessages.PickANatureSound.message)
        }
    }

    @Test
    fun `sendEvent, wrong duration, correct message received`() = runTest {
        viewModel.sendEvent(AddPresetEvents.OnTitleChanged(preset.title))
        viewModel.sendEvent(AddPresetEvents.OnUpdateChosenPreliminarySound(preset.sound))
        viewModel.sendEvent(AddPresetEvents.OnChosenSoundChanged)
        viewModel.sendEvent(AddPresetEvents.OnAddPreset)
        advanceUntilIdle()
        viewModel.eventChannel.test {
            val event = awaitItem()
            assertThat(event).isEqualTo(ToastMessages.DurationIsNotValid.message)
        }
    }

    @Test
    fun `sendEvent, empty title, correct message received`() = runTest {
        viewModel.sendEvent(AddPresetEvents.OnDurationChanged(preset.duration.toFloat() / 60))
        viewModel.sendEvent(AddPresetEvents.OnUpdateChosenPreliminarySound(preset.sound))
        viewModel.sendEvent(AddPresetEvents.OnChosenSoundChanged)
        viewModel.sendEvent(AddPresetEvents.OnAddPreset)
        advanceUntilIdle()
        viewModel.eventChannel.test {
            val event = awaitItem()
            assertThat(event).isEqualTo(ToastMessages.TitleCannotBeEmpty.message)
        }
    }

    @Test
    fun `sendEvent, title too long, correct message received`() = runTest {
        viewModel.sendEvent(AddPresetEvents.OnTitleChanged("12345678901234567890123456789012345678901234567890"))
        viewModel.sendEvent(AddPresetEvents.OnDurationChanged(preset.duration.toFloat() / 60))
        viewModel.sendEvent(AddPresetEvents.OnUpdateChosenPreliminarySound(preset.sound))
        viewModel.sendEvent(AddPresetEvents.OnChosenSoundChanged)
        viewModel.sendEvent(AddPresetEvents.OnAddPreset)
        advanceUntilIdle()
        viewModel.eventChannel.test {
            val event = awaitItem()
            assertThat(event).isEqualTo(ToastMessages.TitleTooLong.message)
        }
    }






}