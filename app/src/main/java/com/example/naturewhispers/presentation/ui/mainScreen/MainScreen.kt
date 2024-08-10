package com.example.naturewhispers.presentation.ui.mainScreen

import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.naturewhispers.data.local.models.Audio
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import com.example.naturewhispers.presentation.ui.PlayerEvents
import com.example.naturewhispers.presentation.ui.PlayerState
import com.example.naturewhispers.presentation.ui.SharedViewModel
import com.example.naturewhispers.presentation.ui.mainScreen.components.BarChartComponent
import com.example.naturewhispers.presentation.ui.mainScreen.components.BottomSheetPreset
import com.example.naturewhispers.presentation.ui.mainScreen.components.Greeting
import com.example.naturewhispers.presentation.ui.mainScreen.components.PresetsSection
import com.example.naturewhispers.presentation.ui.mainScreen.components.StatsPanel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navigateToAddPreset: (Int) -> Unit,
    viewModel: MainViewModel = hiltViewModel(),
    sharedViewModel: SharedViewModel,
) {
    val navigateToAddPresetStable: (Int) -> Unit = remember { navigateToAddPreset }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 47.dp),
                onClick = { navigateToAddPresetStable(0) },
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add"
                )
            }
        }
    ) {
        Content(
            modifier = Modifier,
            sendEvent = viewModel::sendEvent,
            uiState = viewModel.uiState.value,
            playerState = sharedViewModel.state.value,
            sendPlayerEvent = sharedViewModel::dispatchEvent,
            navigateToAddPreset = navigateToAddPresetStable
        )
    }

}

@Composable
fun Content(
    modifier: Modifier = Modifier,
    sendEvent: (MainEvents) -> Unit,
    sendPlayerEvent: (PlayerEvents) -> Unit,
    uiState: MainState,
    playerState: PlayerState,
    navigateToAddPreset: (Int) -> Unit = {}
) {
    val sendEventStable: (MainEvents) -> Unit = remember { sendEvent }
    val sendPlayerEventStable: (PlayerEvents) -> Unit = remember { sendPlayerEvent }

    val activity = LocalContext.current as Activity


    if (uiState.isBottomSheetShown && uiState.currentPreset != null) {
        BottomSheetPreset(
            preset = uiState.currentPreset,
            onDismiss = {
                sendPlayerEventStable(PlayerEvents.OnStopPlayer)
                sendEventStable(MainEvents.ToggleBottomSheet(0))
                sendEventStable(MainEvents.LogStat)
            },
            sendPlayerEvent = sendPlayerEventStable,
            sendEvent = sendEventStable,
            playerState = playerState,
            navigateToAddPresetScreen = { id -> navigateToAddPreset(id) },
        )
    } else activity.window.navigationBarColor = MaterialTheme.colorScheme.surfaceVariant.toArgb()

    Column(
        modifier = modifier,
    ) {

        Spacer(modifier = Modifier.height(5.dp))
        Greeting(
            name = uiState.username,
            profilePicUri = uiState.profilePicUri,
            sendEvent = sendEventStable,
        )
        Text(
            text = "Your Activity",
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp, start = 20.dp),
            fontWeight = FontWeight.Bold
        )
        StatsPanel(stats = uiState.stats, dailyGoal = uiState.dailyGoal)
        Text(
            text = "Favorites",
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp, start = 20.dp),
            fontWeight = FontWeight.Bold
        )
        PresetsSection(
            presets = uiState.presets
        ) { id ->
            sendEventStable(MainEvents.ToggleBottomSheet(id))
            sendPlayerEventStable(PlayerEvents.OnPreparePlayer(id))
        }
        Text(
            text = "Statistics",
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp, start = 20.dp),
            fontWeight = FontWeight.Bold
        )
        BarChartComponent(stats = uiState.stats)

    }
}

@Preview(showBackground = true)
@Composable
fun ContentPreview() {
    Content(
        sendEvent = {},
        sendPlayerEvent = {},
        uiState = MainState(),
        playerState = PlayerState()
    )
}