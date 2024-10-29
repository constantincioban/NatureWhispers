package com.example.naturewhispers.presentation.ui.mainScreen.components

import android.app.Activity
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.naturewhispers.TestTags
import com.example.naturewhispers.TestTags.BOTTOM_SHEET_PRESET
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.entities.Preset
import com.example.naturewhispers.data.local.models.Audio
import com.example.naturewhispers.data.utils.formatDuration
import com.example.naturewhispers.data.utils.formatDurationFromMillis
import com.example.naturewhispers.presentation.ui.PlayerEvents
import com.example.naturewhispers.presentation.ui.PlayerState
import com.example.naturewhispers.presentation.ui.mainScreen.MainEvents
import com.linc.audiowaveform.AudioWaveform

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetPreset(
    modifier: Modifier = Modifier,
    preset: Preset,
    onDismiss: () -> Unit,
    playerState: PlayerState,
    sendPlayerEvent: (PlayerEvents) -> Unit,
    sendEvent: (MainEvents) -> Unit,
    navigateToAddPresetScreen: (Int) -> Unit,
) {
    val activity = LocalContext.current as Activity
    activity.window.navigationBarColor = MaterialTheme.colorScheme.background.toArgb()
    val sheetState = rememberModalBottomSheetState()

    val sendEventStable: (MainEvents) -> Unit = remember { sendEvent }
    val sendPlayerEventStable: (PlayerEvents) -> Unit = remember { sendPlayerEvent }
    val onDismissStable: () -> Unit = remember { onDismiss }
    val navigateToAddPresetScreenStable: (Int) -> Unit = remember { navigateToAddPresetScreen }


    val currentTime = playerState.currentPosition.toFloat()  / preset.duration
    var playWasPressed by remember {
        mutableStateOf(false)
    }

    val isLoading by remember(playerState.isLoading) {
        derivedStateOf { playerState.isLoading }
    }

    val playPauseIcon = remember(playerState.isPlaying) {
        if (playerState.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow
    }

    val staticGradientBrush =
        Brush.linearGradient(colors = listOf(Color(0xff22c1c3), Color(0xfffdbb2d)))
    val solidColor = SolidColor(MaterialTheme.colorScheme.secondary)
    val waveformBrush = remember(playWasPressed, playerState.isPlaying) {
        if (playWasPressed || playerState.isPlaying)
            solidColor else staticGradientBrush
    }
    val progressBrush = remember(playWasPressed, playerState.isPlaying) {
        if (playWasPressed || playerState.isPlaying)
            staticGradientBrush else solidColor
    }

    LaunchedEffect(playerState.currentPosition) {
        Log.i(TAG, "BottomSheetPreset: ${preset.duration} == ${playerState.currentPosition.toInt()}")
        if (preset.duration == playerState.currentPosition.toInt()) {
            sendEvent(MainEvents.LogStat)
            sendPlayerEventStable(PlayerEvents.OnStopPlayer)
            sendPlayerEventStable(PlayerEvents.OnSeekTo(0))
            playWasPressed = false
        }
    }

    ModalBottomSheet(
        onDismissRequest = { onDismissStable() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier.testTag(BOTTOM_SHEET_PRESET)

        ) {

        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp, top = 16.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                RoundedIcon(
                    icon = Icons.Outlined.Edit,
                    onClick = {
                        navigateToAddPresetScreenStable(preset.id)
                        onDismissStable()
                    }
                )

            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(horizontal = 50.dp)
                    .padding(top = 20.dp, bottom = 80.dp)
            ) {

                Text(text = preset.title, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(30.dp))
                AudioWaveform(
                    modifier = Modifier.fillMaxWidth(),
                    amplitudes = playerState.amplitudes,
                    progress = currentTime,

                    onProgressChange = { },
                    waveformBrush = waveformBrush,
                    progressBrush = progressBrush
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(top = 40.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = formatDuration(playerState.currentPosition))

                        Text(text = formatDurationFromMillis(preset.duration * 1000L))

                    }
                    Button(
                        modifier = Modifier
                            .size(52.dp),
                        shape = CircleShape,
                        contentPadding = PaddingValues(3.dp),
                        onClick = {
                            if (isLoading) return@Button
                            playWasPressed = true
                            if (playerState.isPlaying)
                                sendEventStable(MainEvents.LogPreliminaryDuration)
                            else
                                sendEventStable(MainEvents.SetStartDuration)
                            sendPlayerEventStable(PlayerEvents.OnTogglePlayPause)
                        }) {

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.width(40.dp),
                                color = MaterialTheme.colorScheme.secondary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            )
                        } else
                            Icon(
                                modifier = Modifier
                                    .size(40.dp)
                                    .testTag(TestTags.PLAY_PAUSE),
                                imageVector = playPauseIcon,
                                contentDescription = "Play / Pause"
                            )
                    }

                }

            }
        }

    }


}

@Composable
fun RoundedIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Image(
        imageVector = icon,
        contentDescription = "Edit",
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .size(50.dp)
            .padding(12.dp),
        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
    )
}