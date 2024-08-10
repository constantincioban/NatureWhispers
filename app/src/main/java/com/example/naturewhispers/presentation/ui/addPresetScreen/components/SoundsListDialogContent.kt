package com.example.naturewhispers.presentation.ui.addPresetScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.naturewhispers.data.local.predefined.LocalData
import com.example.naturewhispers.presentation.ui.PlayerEvents
import com.example.naturewhispers.presentation.ui.PlayerState
import com.example.naturewhispers.presentation.ui.addPresetScreen.AddPresetEvents
import com.example.naturewhispers.presentation.ui.addPresetScreen.AddPresetState


@Composable
fun SoundsListDialogContent(
    sendEvent: (AddPresetEvents) -> Unit,
    sendPlayerEvent: (PlayerEvents) -> Unit,
    state: AddPresetState,
    playerState: PlayerState,
) {

    SoundsListDialogWrapper(
        title = "Nature's Sounds",
        onOk = {
            sendEvent(AddPresetEvents.OnToggleShowSoundListDialog)
            sendEvent(AddPresetEvents.OnChosenSoundChanged)
            sendEvent(AddPresetEvents.OnPlayingSoundChanged(""))
            sendPlayerEvent(PlayerEvents.OnStopPlayer)

        },
        onDismiss = {
            sendEvent(AddPresetEvents.OnToggleShowSoundListDialog)
            sendEvent(AddPresetEvents.OnUpdateChosenPreliminarySound(state.chosenSound))
            sendEvent(AddPresetEvents.OnPlayingSoundChanged(""))
            sendPlayerEvent(PlayerEvents.OnStopPlayer)
        },
    ) {

        LazyColumn(
            modifier = Modifier
                .padding(top = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 72.dp),
        ) {
            items(
                items = LocalData.meditationSounds.map { it.key.title }.toList(),
                key = { sound -> sound }
            ) { sound ->

                SoundMiniCard(
                    sound = sound,
                    sendEvent = sendEvent,
                    sendPlayerEvent = sendPlayerEvent,
                    state = state,
                    playerState = playerState
                )
            }
        }
    }
}
