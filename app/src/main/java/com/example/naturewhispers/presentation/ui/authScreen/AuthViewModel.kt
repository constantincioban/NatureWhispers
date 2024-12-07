package com.example.naturewhispers.presentation.ui.authScreen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturewhispers.data.auth.GoogleAuthHelper
import com.example.naturewhispers.data.local.preferences.SettingsManager
import com.example.naturewhispers.data.local.preferences.SettingsManager.AuthPreference
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val store: Store<AppState>,
    private val settingsManager: SettingsManager,
    private val googleAuthHelper: GoogleAuthHelper,

) : ViewModel() {

    var loginSuccessful = mutableStateOf(false)
        private set
    var continueAsGuest = mutableStateOf(false)
        private set

    fun sendEvent(event: AuthEvents) {
        when (event) {
            is AuthEvents.OnLogin -> login()
            AuthEvents.OnContinueAsGuest -> continueAsGuest()
        }
    }

    private fun continueAsGuest() = viewModelScope.launch {
        continueAsGuest.value = true
        settingsManager.saveUserDetails("", AuthPreference.GUEST)
    }

    private fun login() = viewModelScope.launch {
        val user = googleAuthHelper.login() ?: return@launch

        val username = settingsManager.readStringSetting(SettingsManager.USERNAME)
        if (username.isEmpty())
            settingsManager.saveStringSetting(SettingsManager.USERNAME, user.name)

        settingsManager.saveUserDetails(user.email, AuthPreference.USER)
        store.update { it.copy(isLoggedIn = true, userEmail = user.email) }
        loginSuccessful.value = true
    }

}

sealed class AuthEvents{
    data object OnLogin: AuthEvents()
    data object OnContinueAsGuest: AuthEvents()
}

