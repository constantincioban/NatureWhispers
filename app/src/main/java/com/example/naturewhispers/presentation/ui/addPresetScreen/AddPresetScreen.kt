package com.example.naturewhispers.presentation.ui.addPresetScreen

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.MediaMetadataRetriever
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.FileOpen
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.naturewhispers.data.utils.formatSecondsToMMss
import com.example.naturewhispers.data.utils.getDisplayNameFromUri
import com.example.naturewhispers.data.utils.observeWithLifecycle
import com.example.naturewhispers.navigation.Screens
import com.example.naturewhispers.presentation.redux.ContentType
import com.example.naturewhispers.presentation.ui.PlayerEvents
import com.example.naturewhispers.presentation.ui.PlayerState
import com.example.naturewhispers.presentation.ui.SharedViewModel
import com.example.naturewhispers.presentation.ui.addPresetScreen.components.DeleteDialog
import com.example.naturewhispers.presentation.ui.addPresetScreen.components.SoundsListDialogContent
import com.example.naturewhispers.presentation.ui.mainScreen.components.RoundedIcon
import com.example.naturewhispers.presentation.ui.theme.NatureWhispersTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AddPresetScreen(
    presetId: Int,
    navigateTo: (route: String, params: List<Any>) -> Unit,
    viewModel: AddPresetViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    sharedViewModel: SharedViewModel,
) {

    viewModel.eventChannel.observeWithLifecycle {
        snackbarHostState.showSnackbar(message = it)
    }

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
    ) {
        Content(
            viewModel.uiState.value,
            viewModel::sendEvent,
            navigateTo,
            sharedViewModel::dispatchEvent,
            sharedViewModel.state.value,
        )
    }

}

@SuppressLint("WrongConstant")
@Composable
fun Content(
    uiState: AddPresetState,
    sendEvent: (AddPresetEvents) -> Unit,
    navigateTo: (route: String, params: List<Any>) -> Unit,
    sendPlayerEvent: (PlayerEvents) -> Unit,
    playerState: PlayerState,

    ) {

    val sendEventStable: (AddPresetEvents) -> Unit = remember { sendEvent }
    val navigateToStable: (route: String, params: List<Any>) -> Unit = remember { navigateTo }
    val sendPlayerEventStable: (PlayerEvents) -> Unit = remember { sendPlayerEvent }

    val presetId by remember(uiState.presetId) {
        derivedStateOf {uiState.presetId}
    }

    LaunchedEffect(key1 = uiState.presetAddedSuccessfully) {
        if (uiState.presetAddedSuccessfully)
            navigateTo(Screens.Main.route, listOf())
    }

    val context = LocalContext.current as Activity
    val audioFilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { result ->
            val uri = result.data?.data ?: return@rememberLauncherForActivityResult
            val audioFileName = getDisplayNameFromUri(uri, context)
            sendEventStable(AddPresetEvents.OnUpdateChosenPreliminarySound(audioFileName))
            sendEventStable(AddPresetEvents.OnChosenSoundChanged)
            sendEventStable(AddPresetEvents.OnUpdateFileUri(uri.toString()))
            sendEventStable(AddPresetEvents.OnUpdateMaxDuration(getAudioFileDuration(uri.toString(), context) / 1000f))
        }
    )

    if (uiState.showSoundListDialog)
        SoundsListDialogContent(sendEventStable, sendPlayerEventStable, state = uiState, playerState)

    if (uiState.showDeleteDialog)
        DeleteDialog(sendEvent = sendEventStable, state = uiState, navigateTo = navigateToStable)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundedIcon(icon = Icons.Rounded.ArrowBackIosNew, onClick = {navigateTo(Screens.Main.route, listOf())})
            Text(text = "Add Preset", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            RoundedIcon(
                icon = Icons.Rounded.DeleteForever,
                onClick = {
                    if (presetId != 0)
                        sendEventStable(AddPresetEvents.OnToggleShowDeleteDialog)
                },
            )

        }
        Column(Modifier
            .padding(horizontal = 30.dp),
) {
            Spacer(modifier = Modifier.height(50.dp))
            Text(
                text = "Preset's title",
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                value = uiState.title,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1,
                singleLine = true,
                placeholder = { Text(text = "Title...") },
                onValueChange = {
                    sendEventStable(AddPresetEvents.OnTitleChanged(it))
                },
            )
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Sound duration",
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = formatSecondsToMMss(uiState.duration.toInt() * 60),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Slider(
                value = uiState.duration,
                onValueChange = { sendEventStable(AddPresetEvents.OnDurationChanged(it)) },
                modifier = Modifier
                    .fillMaxWidth(),
                valueRange = 0f..uiState.maxDuration / 60f,
                steps = (uiState.maxDuration % 60).toInt()
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Nature's sound",
                fontSize = 18.sp,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(0.6f),
                    value = uiState.chosenSound, placeholder = {
                        Text(text = "Pick your sound", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }, onValueChange = {}, enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                )
                Button(
                    modifier = Modifier
                        .width(54.dp)
                        .height(54.dp),
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = { sendEventStable(AddPresetEvents.OnToggleShowSoundListDialog) }) {
                    Icon(
                        modifier = Modifier
                            .size(30.dp), imageVector = Icons.Rounded.MusicNote,
                        contentDescription = "Pick"
                    )
                }
                Button(
                    modifier = Modifier
                        .width(54.dp)
                        .height(54.dp),
                    shape = RoundedCornerShape(5.dp),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        sendEventStable(AddPresetEvents.OnUpdateContentType(ContentType.AUDIO))
                        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "audio/*"
                        }
                        audioFilePicker.launch(intent)
                    }) {
                    Icon(
                        modifier = Modifier
                            .size(30.dp), imageVector = Icons.Rounded.FileOpen,
                        contentDescription = "Pick"
                    )
                }
            }
            Spacer(modifier = Modifier.fillMaxHeight(0.6f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.8f),
                    onClick = { sendEventStable(AddPresetEvents.OnAddPreset) },
                ) {
                    Text(text = "Save", fontSize = 20.sp)
                }
            }
        }



    }
}


fun getAudioFileDuration(uri: String, context: Context): Long {
    val retriever = MediaMetadataRetriever()
    retriever.setDataSource(context, uri.toUri())

    val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
    val durationMillis = durationStr?.toLongOrNull() ?: 0

    retriever.release()

    return durationMillis
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(showBackground = true)
@Composable
fun PresetScreenPreview() {
    NatureWhispersTheme {
        Content(
            uiState = AddPresetState(),
            sendEvent = {},
            navigateTo = { _, _ -> },
            sendPlayerEvent = {},
            playerState = PlayerState(),
        )

    }
}