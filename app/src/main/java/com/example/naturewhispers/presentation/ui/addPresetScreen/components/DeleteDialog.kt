package com.example.naturewhispers.presentation.ui.addPresetScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.naturewhispers.presentation.ui.addPresetScreen.AddPresetEvents
import com.example.naturewhispers.presentation.ui.addPresetScreen.AddPresetState


@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    sendEvent: (AddPresetEvents) -> Unit,
    state: AddPresetState,
    navigateToMain: () -> Unit = {}
) {
    Dialog(
        onDismissRequest = { sendEvent(AddPresetEvents.OnToggleShowDeleteDialog) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth(0.8f)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Are you sure?",
                style = MaterialTheme.typography.bodyLarge,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(44.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable { sendEvent(AddPresetEvents.OnToggleShowDeleteDialog) }
                        .padding(8.dp)
                )
                Text(
                    text = "Delete",
                    fontSize = 20.sp,
                    modifier = Modifier
                        .clickable {
                            sendEvent(AddPresetEvents.OnDeletePreset(state.presetId))
                            navigateToMain()
                        }
                        .padding(8.dp)
                )
            }
        }
    }
}
