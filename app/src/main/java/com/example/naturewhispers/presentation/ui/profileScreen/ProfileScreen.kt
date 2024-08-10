package com.example.naturewhispers.presentation.ui.profileScreen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.isDigitsOnly
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.presentation.ui.theme.NatureWhispersTheme

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    Scaffold(
        modifier = Modifier.safeDrawingPadding(),

        ) {
        Content(viewModel.uiState.value, viewModel::sendEvent)
    }
}

@Composable
fun Content(
    state: ProfileState,
    sendEvent: (ProfileEvents) -> Unit
) {

    val sendEventStable: (ProfileEvents) -> Unit = remember { sendEvent }

    var isSaveEnabled by remember {
        mutableStateOf(false)
    }

    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = state) {
        isSaveEnabled = state.username != state.usernamePreliminary ||
                state.dailyGoal != state.dailyGoalPreliminary
        Log.i(TAG, "After save: " + isSaveEnabled)
    }

    Column(
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 10.dp),
    ) {
        Text(
            text = "Profile Settings", fontSize = 28.sp, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text("Username", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.9f),
            value = state.usernamePreliminary,
            maxLines = 1,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            onValueChange = { sendEventStable(ProfileEvents.OnUpdateUsername(it)) },
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text("Daily goal", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(0.9f),
            value = state.dailyGoalPreliminary,
            onValueChange = { sendEventStable(ProfileEvents.OnUpdateDailyGoal(it)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            trailingIcon = {
                Text(
                    text = if (state.dailyGoalPreliminary.isDigitsOnly()
                        && state.dailyGoalPreliminary.ifEmpty { "0" }.toInt() > 1
                        ) "minutes" else "minute",
                    modifier = Modifier.padding(end = 10.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )

            },
            singleLine = true,
            maxLines = 1,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(0.9f)) {
            Text("Dark mode", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Switch(
                checked = state.darkTheme,
                onCheckedChange = { sendEventStable(ProfileEvents.OnUpdateTheme(it)) },
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Spacer(modifier = Modifier.fillMaxHeight(0.7f))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(0.8f),
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
