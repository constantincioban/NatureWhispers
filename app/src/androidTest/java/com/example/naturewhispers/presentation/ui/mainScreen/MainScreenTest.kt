package com.example.naturewhispers.presentation.ui.mainScreen

import MediaPlayerImpl
import androidx.activity.compose.setContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.click
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.isNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onChildAt
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performMouseInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.naturewhispers.TestTags
import com.example.naturewhispers.data.di.AppModule
import com.example.naturewhispers.data.di.MediaModule
import com.example.naturewhispers.data.mediaPlayer.IMediaPlayer
import com.example.naturewhispers.data.mediaPlayer.PlayerManager
import com.example.naturewhispers.navigation.Screens
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import com.example.naturewhispers.presentation.ui.MainActivity
import com.example.naturewhispers.presentation.ui.theme.NatureWhispersTheme
import com.google.common.truth.Truth
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalTestApi::class)
@HiltAndroidTest
@UninstallModules(AppModule::class, MediaModule::class)
class MainScreenTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)
    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var playerManager: PlayerManager

    @Before
    fun setUp() {
        hiltRule.inject()

        composeRule.activity.setContent {
            val navController = rememberNavController()
            NatureWhispersTheme {
                NavHost(navController = navController, startDestination = Screens.Main.route ) {
                    composable(route = Screens.Main.route) {
                        MainScreen(playerManager = playerManager, navigateTo = { route, params ->})
                    }
                }
            }
        }

    }


    @Test
    fun clickOnPresetItem_OpensBottomSheet() {
        composeRule.onNodeWithTag(TestTags.BOTTOM_SHEET_PRESET).assertDoesNotExist()
        composeRule.onAllNodesWithTag(TestTags.PRESET_CARD)
            .onFirst()  // Selects the first node in the list
            .performClick()
        composeRule.onNodeWithTag(TestTags.BOTTOM_SHEET_PRESET).assertIsDisplayed()
    }

    @Test
    fun openBottomSheet_PlayerIsNotPlaying() {
        composeRule.onAllNodesWithTag(TestTags.PRESET_CARD)
            .onFirst()  // Selects the first node in the list
            .performClick()
        Truth.assertThat(playerManager.state.value.isPlaying).isFalse()
    }


    @Test
    fun openBottomSheet_clickOnPlay_PlayerIsPlaying() = runTest {
        composeRule.onAllNodesWithTag(TestTags.PRESET_CARD)
            .onFirst()  // Selects the first node in the list
            .performClick()
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onNodeWithTag(TestTags.PLAY_PAUSE, true).isDisplayed()
        }
        composeRule.onNodeWithTag(TestTags.PLAY_PAUSE, true).performClick()
        composeRule.awaitIdle()
        Truth.assertThat(playerManager.state.value.isPlaying).isTrue()
    }

    @Test
    fun openBottomSheet_clickOnPlayClickOnPauseClickOnPlay_PlayerIsPlaying() = runTest {
        composeRule.onAllNodesWithTag(TestTags.PRESET_CARD)
            .onFirst()  // Selects the first node in the list
            .performClick()
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onNodeWithTag(TestTags.PLAY_PAUSE, true).isDisplayed()
        }
        composeRule.onNodeWithTag(TestTags.PLAY_PAUSE, true).performClick()
        composeRule.awaitIdle()
        composeRule.onNodeWithTag(TestTags.PLAY_PAUSE, true).performClick()
        composeRule.awaitIdle()
        composeRule.onNodeWithTag(TestTags.PLAY_PAUSE, true).performClick()
        composeRule.awaitIdle()
        Truth.assertThat(playerManager.state.value.isPlaying).isTrue()
    }

    @Test
    fun openBottomSheet_clickOnPlayClickOutsideOpenBottomSheet_PlayerIsInInitialState() = runTest(timeout = 20.seconds) {
        composeRule.onAllNodesWithTag(TestTags.PRESET_CARD)
            .onFirst()  // Selects the first node in the list
            .performClick()
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onNodeWithTag(TestTags.PLAY_PAUSE, true).isDisplayed()
        }
        composeRule.onNodeWithTag(TestTags.PLAY_PAUSE, true).performClick()
        composeRule.awaitIdle()
        Thread.sleep(2000)
        composeRule.onNodeWithTag(TestTags.BOTTOM_SHEET_PRESET)
            .performMouseInput {
        click(Offset(1f, 1f))  // Click near the top of the screen
    }
        composeRule.awaitIdle()
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onNodeWithTag(TestTags.BOTTOM_SHEET_PRESET).isNotDisplayed()
        }
        composeRule.onAllNodesWithTag(TestTags.PRESET_CARD)
            .onLast()  // Selects the first node in the list
            .performClick()
        composeRule.awaitIdle()
        Truth.assertThat(playerManager.state.value.isPlaying).isFalse()
        Truth.assertThat(playerManager.state.value.currentPosition).isEqualTo(0)
    }


}