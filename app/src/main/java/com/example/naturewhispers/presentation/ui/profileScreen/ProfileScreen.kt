package com.example.naturewhispers.presentation.ui.profileScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.naturewhispers.data.utils.observeWithLifecycle
import com.example.naturewhispers.navigation.Screens
import com.example.naturewhispers.presentation.ui.profileScreen.components.DataLoadingDialog
import com.example.naturewhispers.presentation.ui.profileScreen.components.DataSyncDialog
import com.example.naturewhispers.presentation.ui.profileScreen.components.SyncWithGuestDialog
import com.example.naturewhispers.presentation.ui.theme.NatureWhispersTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    snackbarHostState: SnackbarHostState,
    navigateAndClearStack: (route: String, params: List<Any>) -> Unit,
) {


    viewModel.eventChannel.observeWithLifecycle {
        snackbarHostState.showSnackbar(message = it)
    }

    LaunchedEffect(viewModel.isLoggedOut.value) {
        if (viewModel.isLoggedOut.value)
            navigateAndClearStack(Screens.Auth.route, listOf())
    }

    Scaffold(
        modifier = Modifier.safeDrawingPadding(),
    ) {
        Content(viewModel.uiState.value, viewModel::sendEvent)
    }
}

@Composable
fun Content(
    uiState: ProfileState,
    sendEvent: (ProfileEvents) -> Unit
) {

    val sendEventStable: (ProfileEvents) -> Unit = remember { sendEvent }

    var isSaveEnabled by remember {
        mutableStateOf(false)
    }

    val focusManager = LocalFocusManager.current



    LaunchedEffect(key1 = uiState, ) {
        isSaveEnabled = uiState.username != uiState.usernamePreliminary ||
                uiState.dailyGoal != uiState.dailyGoalPreliminary
    }

    if (uiState.showSyncWithGuestDialog)
        SyncWithGuestDialog(sendEvent = sendEventStable)

    if (uiState.showBackupLoadingDialog)
        DataLoadingDialog(text = "Backing up...")

    if (uiState.showRestoreLoadingDialog)
        DataLoadingDialog(text = "Restoring your data...")

    if (uiState.showBackupDialog)
        DataSyncDialog(
            state = uiState,
            title = "Set a unique key for your data backup",
            actionButtonText = "Backup",
            keyValue = uiState.backupKey,
            onDismiss = { sendEventStable(ProfileEvents.OnBackupAction) },
            onAction = { sendEventStable(ProfileEvents.OnBackupWithKey) },
            onUpdateKey = { sendEventStable(ProfileEvents.OnUpdateBackupKey(it)) }
        )

    if (uiState.showRestoreDialog)
        DataSyncDialog(
            state = uiState,
            title = "Enter your unique key",
            actionButtonText = "Restore",
            keyValue = uiState.restoreKey,
            onDismiss = { sendEventStable(ProfileEvents.OnRestoreAction) },
            onAction = { sendEventStable(ProfileEvents.OnRestoreWithKey) },
            onUpdateKey = { sendEventStable(ProfileEvents.OnUpdateRestoreKey(it)) }
        )


    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, top = 10.dp, end = 20.dp)
            ,
    ) {
        Text(
            text = "Profile Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.usernamePreliminary,
            onValueChange = { sendEventStable(ProfileEvents.OnUpdateUsername(it)) },
            label = {
                Text(text = "Username", fontSize = 16.sp)
            },
            maxLines = 1,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.dailyGoalPreliminary,
            onValueChange = { sendEventStable(ProfileEvents.OnUpdateDailyGoal(it)) },
            label = {
                Text(text = "Daily goal", fontSize = 16.sp)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            trailingIcon = {
                Text(
                    text = if (uiState.dailyGoalPreliminary.isDigitsOnly()
                        && uiState.dailyGoalPreliminary.ifEmpty { "0" }.toInt() > 1
                    ) "minutes" else "minute",
                    modifier = Modifier.padding(end = 10.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            },
            singleLine = true,
            maxLines = 1,
        )

        Spacer(modifier = Modifier.height(10.dp))
        Text("Data sync", fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
        Divider()
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Backup data", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Icon(
                imageVector = Icons.Filled.CloudUpload,
                contentDescription = "Backup data",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(34.dp)
                    .clickable { sendEventStable(ProfileEvents.OnBackupAction) }
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Restore data", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Icon(
                imageVector = Icons.Filled.CloudDownload,
                contentDescription = "Restore data",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(34.dp)
                    .clickable { sendEventStable(ProfileEvents.OnRestoreAction) }
            )
        }
        if (uiState.isLoggedIn)
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Sync with Guest", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                Icon(
                    imageVector = Icons.Filled.Sync,
                    contentDescription = "Sync with Guest",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(34.dp)
                        .clickable { sendEventStable(ProfileEvents.OnToggleSyncWithGuestDialog) }
                )
            }
        Spacer(modifier = Modifier.height(10.dp))
        Text("Theme", fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
        Divider()
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
        ) {
            Text("Dark mode", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Switch(
                checked = uiState.darkTheme,
                modifier = Modifier.size(32.dp),
                onCheckedChange = { sendEventStable(ProfileEvents.OnUpdateTheme(it)) },
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text("Auth", fontSize = 16.sp, fontFamily = FontFamily.SansSerif)
        Divider()
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Logout", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = "Restore data",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(34.dp)
                    .clickable { sendEventStable(ProfileEvents.OnLogout) }
            )
        }


        Spacer(modifier = Modifier.fillMaxHeight(0.2f))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                ,
                enabled = isSaveEnabled,
                colors = ButtonDefaults.buttonColors(
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(
                        alpha = 0.3f
                    )
                ),
                onClick = {
                    sendEventStable(ProfileEvents.OnSave)
                    focusManager.clearFocus()
                }) {
                Text(
                    text = "Save",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f)
                )
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun ContentPreview() {
    NatureWhispersTheme {
        Content(ProfileState(), sendEvent = {})
    }
}
