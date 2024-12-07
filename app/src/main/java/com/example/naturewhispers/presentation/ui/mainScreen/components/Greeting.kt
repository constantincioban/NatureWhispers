package com.example.naturewhispers.presentation.ui.mainScreen.components

import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.imageLoader
import coil.util.DebugLogger
import com.example.naturewhispers.TestTags
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.utils.getCurrentDate
import com.example.naturewhispers.presentation.redux.ContentType
import com.example.naturewhispers.presentation.ui.mainScreen.MainEvents

@Composable
fun Greeting(
    modifier: Modifier = Modifier,
    name: String,
    profilePicUri: String = "",
    sendEvent: (MainEvents) -> Unit,
) {


    var selectedImageUri by remember {
        mutableStateOf<Uri?>(null)
    }
    LaunchedEffect(key1 = profilePicUri) {
        selectedImageUri = profilePicUri.toUri()
    }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            if (uri == null) return@rememberLauncherForActivityResult
            selectedImageUri = uri
            sendEvent(MainEvents.OnUpdateProfilePic(uri.toString()))
        }
    )

    Row(
        modifier = modifier
            .wrapContentHeight()
            .padding(horizontal = 20.dp)
            .testTag(TestTags.GREETING_CARD),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (selectedImageUri != null && selectedImageUri.toString().isNotEmpty()) {
            val imageLoader = LocalContext.current.imageLoader.newBuilder()
                .logger(DebugLogger())
                .build()
            AsyncImage(
                model = selectedImageUri,
                imageLoader = imageLoader,
                contentDescription = "avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .padding(0.dp)
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .clickable {
                        sendEvent(MainEvents.OnUpdateContentType(ContentType.IMAGE))
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
        } else
            Image(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = "avatar",
                contentScale = ContentScale.Crop,            // crop the image if it's not a square
                modifier = Modifier
                    .padding(0.dp)
                    .size(64.dp)
                    .clip(CircleShape)
                    .clickable {
                        sendEvent(MainEvents.OnUpdateContentType(ContentType.IMAGE))
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )

        Column() {
            Text(
                text = "Hi, $name",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = getCurrentDate(),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }


    }
}