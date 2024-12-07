package com.example.naturewhispers.presentation.ui.profileScreen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.naturewhispers.presentation.ui.profileScreen.ProfileEvents
import com.example.naturewhispers.presentation.ui.profileScreen.ProfileState


@Composable
fun DataSyncDialog(
    modifier: Modifier = Modifier,
    state: ProfileState,
    title: String,
    actionButtonText: String,
    keyValue: String = "",
    onDismiss: () -> Unit,
    onUpdateKey: (String) -> Unit,
    onAction: () -> Unit,
) {

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Dialog(
        onDismissRequest = { onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {

        Column(
            modifier = modifier
                .fillMaxWidth(0.8f)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        modifier = Modifier.focusRequester(focusRequester),
                        value = keyValue,
                        maxLines = 1,
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedBorderColor = if (state.isLoading) MaterialTheme.colorScheme.onSurfaceVariant
                            else MaterialTheme.colorScheme.primary
                        ),
                        onValueChange = {
                            if (!state.isLoading)
                                onUpdateKey(it)
                        }
                    )
                    Spacer(modifier = Modifier.height(44.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 22.dp)
                    ) {
                        TextButton(
                            onClick = { onDismiss() },
                            enabled = !state.isLoading,
                        ) {
                            Text(
                                text = "Cancel",
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground,  // Customize color as needed
                                fontWeight = FontWeight.Normal,
                            )
                        }
                        TextButton(
                            onClick = { onAction() },
                            enabled = !state.isLoading,
                        ) {
                            Text(
                                text = actionButtonText,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.onBackground,  // Customize color as needed
                                fontWeight = FontWeight.Normal,
                            )
                        }
                    }
                }
                // Overlay loading spinner
                if (state.isLoading) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.3f))
                            .matchParentSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
        }

        }
    }
}
