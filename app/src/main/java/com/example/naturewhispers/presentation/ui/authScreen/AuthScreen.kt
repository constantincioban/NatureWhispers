package com.example.naturewhispers.presentation.ui.authScreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.naturewhispers.R
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
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
    store: Store<AppState>,
) {
    Content(store = store)
}

@Composable
fun Content(store: Store<AppState>,) {

    Column(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets(0,0,0,0))
            .fillMaxSize()
            .paint(painterResource(id = R.drawable.nw_bg),
                contentScale = ContentScale.FillBounds),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        GoogleSignInButton(store = store)
    }
}

@Preview(showBackground = true)
@Composable
fun ContentPreview() {
    NatureWhispersTheme {
        Content(store = Store(AppState()))
    }
}

@Composable
fun GoogleSignInButton(store: Store<AppState>,) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val onClick: () -> Unit = {
        val oauth = "188861013678-38kd55m6vui7rvl296peh19kdp6bkram.apps.googleusercontent.com"
        val credentialManager = CredentialManager.create(context)

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("188861013678-rvb2l1vm1v5tml3ld17dooibvke9a64q.apps.googleusercontent.com")
            .setNonce(hashedNonce)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        scope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = context
                )

                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)

                val googleIdToken = googleIdTokenCredential.idToken


                store.update { it.copy(
                    username = googleIdTokenCredential.displayName ?: "User",
//                    userId = googleIdTokenCredential.id,
//                    isLoggedIn = true
                ) }
                Log.i(TAG, "[Auth]: googleIdToken = $googleIdToken")

                Toast.makeText(context, "You are signed in!", Toast.LENGTH_SHORT).show()

                Log.i(TAG, "[Auth]: googleIdTokenCredential.id = " + googleIdTokenCredential.id)

            } catch (e: GoogleIdTokenParsingException) {
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    Button(onClick = { onClick() }) {
        Text(text = "Sign in with Google")
    }
}