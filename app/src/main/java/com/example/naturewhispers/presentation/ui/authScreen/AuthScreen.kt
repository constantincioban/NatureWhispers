package com.example.naturewhispers.presentation.ui.authScreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.naturewhispers.R
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.navigation.Screens
import com.example.naturewhispers.presentation.ui.theme.NatureWhispersTheme
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = hiltViewModel(),
    navigateTo: (route: String, params: List<Any>) -> Unit,

) {

    LaunchedEffect(viewModel.continueAsGuest.value, viewModel.loginSuccessful.value) {
        if (viewModel.continueAsGuest.value || viewModel.loginSuccessful.value) {
            navigateTo(Screens.Main.route, listOf())
        }
    }

    Content(
        modifier = modifier,
        viewModel::sendEvent,
    )
}

@Composable
fun Content(
    modifier: Modifier,
    sendEvent: (AuthEvents) -> Unit) {

    Column(
        modifier = modifier
            .windowInsetsPadding(WindowInsets(0, 0, 0, 0))
            .fillMaxSize()
            .paint(
                painterResource(id = R.drawable.nw_bg),
                contentScale = ContentScale.FillBounds
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) { 
        Box(modifier = Modifier.weight(0.8f),
            contentAlignment = Alignment.Center) {
            Button(onClick = { sendEvent(AuthEvents.OnLogin) }) {
                Text(text = "Sign in with Google")
            }
        }
        Box(modifier = Modifier.weight(0.1f),
            contentAlignment = Alignment.Center) {
            Text(text = "Continue as guest", color = Color.Cyan, modifier = Modifier
                .clickable { sendEvent(AuthEvents.OnContinueAsGuest) })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ContentPreview() {
    NatureWhispersTheme {
        Content(modifier = Modifier, sendEvent = {})
    }
}
