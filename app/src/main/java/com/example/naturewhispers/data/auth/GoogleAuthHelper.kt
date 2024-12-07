package com.example.naturewhispers.data.auth

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import com.example.naturewhispers.BuildConfig
//import com.example.naturewhispers.BuildConfig
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.local.models.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.*

class GoogleAuthHelper(private val context: Context) {

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private val credentialManager: CredentialManager by lazy {
        CredentialManager.create(context)
    }

    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(rawNonce.toByteArray())
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    suspend fun login(): User? {
        val hashedNonce = generateNonce()
        val googleClientId = BuildConfig.GOOGLE_CLIENT_ID
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(googleClientId)
            .setNonce(hashedNonce)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        // Coroutine scope for background operations
        val result = coroutineScope.async {
            try {
                val result = credentialManager.getCredential(context, request)
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                val googleIdToken = googleIdTokenCredential.idToken

                Log.i(TAG, "[Auth]: displayName = ${googleIdTokenCredential.displayName}")
                Log.i(TAG, "[Auth]: googleIdTokenCredential.id = ${googleIdTokenCredential.id}")

                Log.i(TAG, "[Auth]: googleIdToken = $googleIdToken")
                Toast.makeText(context, "You are signed in!", Toast.LENGTH_SHORT).show()
                return@async User(
                    name = googleIdTokenCredential.displayName ?: "",
                    email = googleIdTokenCredential.id
                )

            } catch (e: GoogleIdTokenParsingException) {
                Log.e(TAG, "[Auth]: Error parsing Google ID Token", e)
                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                return@async null
            } catch (e: Exception) {
                Log.e(TAG, "[Auth]: Login failed", e)
                return@async null
            }
        }
        return result.await()
    }

    fun logout() {
        coroutineScope.launch {
            try {
                // Clear stored credentials for the user
                val request = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(request)
                Toast.makeText(context, "You are signed out!", Toast.LENGTH_SHORT).show()
                Log.i(TAG, "[Auth]: User signed out successfully")
            } catch (e: Exception) {
                Log.e(TAG, "[Auth]: Logout failed", e)
                Toast.makeText(context, "Logout failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
