package com.example.naturewhispers.presentation.ui.mainScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.naturewhispers.R
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.local.preferences.SettingsManager
import com.example.naturewhispers.data.mediaPlayer.PlayerEvents
import com.example.naturewhispers.data.mediaPlayer.PlayerManager
import com.example.naturewhispers.data.mediaPlayer.PlayerState
import com.example.naturewhispers.data.permission.NOTIFICATION_PERMISSION
import com.example.naturewhispers.data.permission.PermissionDialog
import com.example.naturewhispers.data.permission.ShowPermissionRationale
import com.example.naturewhispers.data.permission.isPermissionGranted
import com.example.naturewhispers.data.permission.permissionLauncher
import com.example.naturewhispers.data.utils.openAppSettings
import com.example.naturewhispers.navigation.Screens
import com.example.naturewhispers.presentation.ui.mainScreen.components.BarChartComponent
import com.example.naturewhispers.presentation.ui.mainScreen.components.BottomSheetPreset
import com.example.naturewhispers.presentation.ui.mainScreen.components.Greeting
import com.example.naturewhispers.presentation.ui.mainScreen.components.PresetsSection
import com.example.naturewhispers.presentation.ui.mainScreen.components.StatsPanel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel(),
    playerManager: PlayerManager,
    navigateTo: (route: String, params: List<Any>) -> Unit,
) {

    val navigateToStable: (route: String, params: List<Any>) -> Unit = remember { navigateTo }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding(),
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.padding(bottom = 47.dp),
                onClick = { navigateToStable(Screens.AddPreset.route, listOf(0)) },
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
            playerState = playerManager.state.value,
            sendPlayerEvent = playerManager::dispatchEvent,
            navigateTo = navigateToStable
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
    navigateTo: (route: String, params: List<Any>) -> Unit,
) {
    val sendEventStable: (MainEvents) -> Unit = remember { sendEvent }
    val sendPlayerEventStable: (PlayerEvents) -> Unit = remember { sendPlayerEvent }

    val activity = LocalContext.current as Activity
    var showPermissionDialog by remember { mutableStateOf(false) }

    if (!isPermissionGranted(activity, NOTIFICATION_PERMISSION))
        showPermissionDialog = true

    ShowPermissionRationale(
        context = activity,
        showPermissionDialog = showPermissionDialog,
        permission = NOTIFICATION_PERMISSION,
        description = stringResource(R.string.post_notifications_permission_description),
        permanentlyDeclinedDescription = stringResource(R.string.post_notifications_permanently_declined_description)
    )


    if (uiState.isBottomSheetShown && uiState.currentPreset != null) {

        BottomSheetPreset(
            preset = uiState.currentPreset,
            onDismiss = {
                sendPlayerEventStable(PlayerEvents.OnStopPlayer)
                sendEventStable(MainEvents.LogStat)
                sendEventStable(MainEvents.OnPresetSelected(-1))
            },
            sendPlayerEvent = sendPlayerEventStable,
            sendEvent = sendEventStable,
            playerState = playerState,
            navigateToAddPresetScreen = { id -> navigateTo(Screens.AddPreset.route, listOf(id)) },
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
        StatsPanel(dailyGoal = uiState.dailyGoal, todaysTime = uiState.todaysTime, streak = uiState.streak)
        Text(
            text = "Favorites",
            modifier = Modifier.padding(top = 20.dp, bottom = 8.dp, start = 20.dp),
            fontWeight = FontWeight.Bold
        )
        PresetsSection(
            presets = uiState.presets
        ) { id ->
            sendEventStable(MainEvents.OnPresetSelected(id))
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
        playerState = PlayerState(),
        navigateTo = { _, _ -> }
    )
}