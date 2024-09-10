package com.example.naturewhispers.data.permission

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.utils.openAppSettings

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
const val NOTIFICATION_PERMISSION = Manifest.permission.POST_NOTIFICATIONS

fun isPermissionGranted(
    context: Context,
    permission: String
): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
}

@Composable
fun permissionLauncher(onResult: (Boolean) -> Unit): ManagedActivityResultLauncher<String, Boolean> {
    return rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onResult
    )
}

@Composable
fun ShowPermissionRationale(
    context: Activity,
    showPermissionDialog: Boolean,
    permission: String,
    description: String,
    permanentlyDeclinedDescription: String,
) {

    var showPermissionDialogLocal by remember { mutableStateOf(showPermissionDialog) }
    var isPermanentlyDeclined by remember { mutableStateOf(false) }
    val permissionLauncher = permissionLauncher { isGranted ->
        showPermissionDialogLocal = !isGranted
        isPermanentlyDeclined = !isGranted
    }
    if (showPermissionDialogLocal)
        PermissionDialog(
            description = description,
            permanentlyDeclinedDescription = permanentlyDeclinedDescription,
            isPermanentlyDeclined = isPermanentlyDeclined,
            onDismiss = {  },
            onOkClick = {
                showPermissionDialogLocal = false
                permissionLauncher.launch(permission)
            },
            onGoToAppSettingsClick = {
                showPermissionDialogLocal = false
                context.openAppSettings()
            }
        )
}

@Composable
fun PermissionDialog(
    description: String,
    permanentlyDeclinedDescription: String,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Log.i(TAG, "PermissionDialog: $isPermanentlyDeclined")
    Dialog(
        onDismissRequest = onDismiss,
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
                text = "Permission required",
                style = MaterialTheme.typography.headlineMedium,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = if (isPermanentlyDeclined)
                    permanentlyDeclinedDescription else description,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(30.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isPermanentlyDeclined) {
                    "Open Settings"
                } else {
                    "OK"
                },
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        if (isPermanentlyDeclined) {
                            onGoToAppSettingsClick()
                        } else {
                            onOkClick()
                        }
                    }
                    .padding(8.dp)
            )
        }
    }
}