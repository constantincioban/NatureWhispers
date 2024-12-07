package com.example.naturewhispers.presentation.ui.addPresetScreen.components

import android.content.res.Configuration
import android.util.Log
import androidx.compose.animation.core.EaseInBounce
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.mediaPlayer.PlayerEvents
import com.example.naturewhispers.data.mediaPlayer.PlayerState
import com.example.naturewhispers.presentation.ui.addPresetScreen.AddPresetEvents
import com.example.naturewhispers.presentation.ui.addPresetScreen.AddPresetState
import com.example.naturewhispers.presentation.ui.theme.NatureWhispersTheme

@Composable
fun SoundMiniCard(
    modifier: Modifier = Modifier,
    sound: String,
    sendEvent: (AddPresetEvents) -> Unit,
    sendPlayerEvent: (PlayerEvents) -> Unit,
    state: AddPresetState,
    playerState: PlayerState,
    ) {

    val color =
        if (state.chosenPreliminarySound == sound) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.surfaceVariant

    val isLoading = playerState.isLoading && (state.playingSound == sound)
    var isPlaying by remember {
        mutableStateOf(false)
    }
    var isPrepared by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(key1 = state.playingSound) {
        if (state.playingSound != sound) {
            Log.i(TAG, "SoundMiniCard: Reset to false")
            isPlaying = false
            isPrepared = false
        }
    }

    LaunchedEffect(key1 = playerState.isLoading) {
        if (isPlaying && !isLoading && !isPrepared) {
            isPrepared = true
            Log.i(TAG, "SoundMiniCard: Loaded")
            sendPlayerEvent(PlayerEvents.OnTogglePlayPause)
        }
    }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                sendEvent(AddPresetEvents.OnUpdateChosenPreliminarySound(sound))
                if (isLoading) return@clickable

                isPlaying = !isPlaying
                sendEvent(AddPresetEvents.OnPlayingSoundChanged(sound))
                if (!isPrepared) {
                    sendPlayerEvent(PlayerEvents.OnStopPlayer)
                    sendPlayerEvent(PlayerEvents.OnPreparePlayer(sound = sound))
                } else sendPlayerEvent(PlayerEvents.OnTogglePlayPause)
            },
        border = BorderStroke(3.dp, color),
        shape = RoundedCornerShape(5.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 26.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = sound, fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = MaterialTheme.colorScheme.inverseSurface
            )
            if (state.chosenPreliminarySound == sound && !isLoading)
                MiniWaveform()
            if (isLoading)
                CircularProgressIndicator(
                    modifier = Modifier.size(30.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )
        }
    }
}

@Composable
fun MiniWaveform(
    modifier: Modifier = Modifier,
) {

    val transition = rememberInfiniteTransition()


    val firstBarHeight by transition.animateFloat(
        initialValue = 5f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable<Float>(
            animation = tween(durationMillis = 300, easing = EaseInBounce),
            repeatMode = RepeatMode.Reverse
        )
    )

    val middleBarHeight by transition.animateFloat(
        initialValue = 5f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable<Float>(
            animation = tween(durationMillis = 300, easing = EaseInBounce, delayMillis = 100),
            repeatMode = RepeatMode.Reverse
        )
    )

    val lastBarHeight by transition.animateFloat(
        initialValue = 5f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable<Float>(
            animation = tween(durationMillis = 300, easing = EaseInBounce, delayMillis = 200),
            repeatMode = RepeatMode.Reverse
        )
    )

    Row(
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = modifier
            .height(20.dp)
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp))
                .width(7.dp)
                .height(firstBarHeight.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {}
        Box(
            Modifier
                .clip(RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp))
                .width(7.dp)
                .height(middleBarHeight.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {}
        Box(
            Modifier
                .clip(RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp))
                .width(7.dp)
                .height(lastBarHeight.dp)
                .background(MaterialTheme.colorScheme.primary)
        ) {}
    }

}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SoundMiniCardPreview() {
    NatureWhispersTheme {
        SoundMiniCard(
            sound = "Bonfire",
            sendEvent = {},
            sendPlayerEvent = {
            },
            state = AddPresetState(),
            playerState = PlayerState(),
        )
    }
}
