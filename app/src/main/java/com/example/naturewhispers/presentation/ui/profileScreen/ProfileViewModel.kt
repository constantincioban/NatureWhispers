package com.example.naturewhispers.presentation.ui.profileScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.naturewhispers.data.auth.IAuthHelper
import com.example.naturewhispers.data.local.entities.Preset
import com.example.naturewhispers.data.local.entities.Stat
import com.example.naturewhispers.data.cloud.CollectionPath
import com.example.naturewhispers.data.cloud.IFirestoreHelper
import com.example.naturewhispers.data.local.db.PresetDao
import com.example.naturewhispers.data.local.db.StatDao
import com.example.naturewhispers.data.local.preferences.SettingsManager
import com.example.naturewhispers.data.local.preferences.SettingsManager.AuthPreference
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val store: Store<AppState>,
    private val firestoreHelper: IFirestoreHelper,
    private val presetDao: PresetDao,
    private val statDao: StatDao,
    private val googleAuthHelper: IAuthHelper,
): ViewModel() {


    private var _state = mutableStateOf(ProfileState())
    val uiState: State<ProfileState> = _state

    var isLoggedOut = mutableStateOf(false)
        private set

    private var messageIsActive = false
    private val _eventChannel = Channel<String>()
    val eventChannel = _eventChannel.receiveAsFlow()


    init {
        viewModelScope.launch {
            val isLoggedIn = store.state.value.isLoggedIn
            val username = store.state.value.username
            val dailyGoal = store.state.value.dailyGoal
            val darkTheme = store.state.value.darkTheme
            _state.value = _state.value.copy(
                username = username,
                usernamePreliminary = username,
                dailyGoal = dailyGoal,
                dailyGoalPreliminary = dailyGoal,
                darkTheme = darkTheme.toBoolean(),
                isLoggedIn = isLoggedIn
            )
        }
    }


    private fun sendUserEvent(toast: ProfileScreenMessages) = viewModelScope.launch {
        if (!messageIsActive) {
            _eventChannel.send(toast.message)
            messageIsActive = true
            delay(3500)
            messageIsActive = false
        }
    }

    fun sendEvent(event: ProfileEvents) {
        when (event) {
            is ProfileEvents.OnUpdateDailyGoal -> updateDailyGoal(event.dailyGoal)
            is ProfileEvents.OnUpdateUsername -> updateUsername(event.username)
            is ProfileEvents.OnSave -> saveProfileChanges()
            is ProfileEvents.OnUpdateTheme -> updateTheme(event.darkTheme)
            is ProfileEvents.OnUpdateBackupKey -> updateBackupKey(event.backupKey)
            ProfileEvents.OnBackupAction -> processBackupData()
            ProfileEvents.OnToggleIsLoading -> toggleIsLoading()
            ProfileEvents.OnRestoreAction -> processRestoreData()
            is ProfileEvents.OnUpdateRestoreKey -> updateRestoreKey(event.restoreKey)
            ProfileEvents.OnLogout -> logout()
            ProfileEvents.OnBackupWithKey -> backupDataWithKey()
            ProfileEvents.OnRestoreWithKey -> restoreDataWithKey()
            ProfileEvents.OnToggleSyncWithGuestDialog -> toggleSyncWithGuestDialog()
            ProfileEvents.OnSyncWithGuest -> syncWithGuest()
        }
    }

    private fun toggleSyncWithGuestDialog() {
        _state.value = _state.value.copy(showSyncWithGuestDialog = !_state.value.showSyncWithGuestDialog)
    }

    private fun syncWithGuest() = viewModelScope.launch {
        combine(presetDao.getPresets(),statDao.getStats()) { presets, stats ->
            val statsFilteredMapped = stats.filter { it.userId.isEmpty() }
                .map { it.copy(userId = store.state.value.userEmail) }
            val presetsFilteredMapped = presets.filter { it.userId.isEmpty() }
                .map { it.copy(userId = store.state.value.userEmail) }
            statDao.insertStats(statsFilteredMapped)
            presetDao.insertPresets(presetsFilteredMapped)
            toggleSyncWithGuestDialog()
            sendUserEvent(ProfileScreenMessages.DataSyncedSuccessfully)
        }.collect { this.cancel() }
    }

    private fun processBackupData() {
        if (store.state.value.isLoggedIn)
            backupWithUserId()
        else toggleBackupDialog()
    }

    private fun processRestoreData() {
        if (store.state.value.isLoggedIn)
            restoreWithUserId()
        else toggleRestoreDialog()
    }

    private fun backupWithUserId() = viewModelScope.launch {
        _state.value = _state.value.copy(showBackupLoadingDialog = true)

        backUpData(store.state.value.userEmail, afterBackup = {
            _state.value = _state.value.copy(showBackupLoadingDialog = false)
        })
    }

    private fun restoreWithUserId() = viewModelScope.launch {
        _state.value = _state.value.copy(showRestoreLoadingDialog = true)

        retrieveData(store.state.value.userEmail)

        _state.value = _state.value.copy(showRestoreLoadingDialog = false)
    }

    private fun logout() = viewModelScope.launch {
        if (store.state.value.isLoggedIn)
            googleAuthHelper.logout()
        settingsManager.saveUserDetails("", AuthPreference.NONE)
        store.update { it.copy(isLoggedIn = false, userEmail = "") }
        isLoggedOut.value = true
    }

    private fun restoreDataWithKey() = viewModelScope.launch {
        if (!validateRestore()) return@launch

        toggleIsLoading()

        retrieveData(_state.value.restoreKey)

        _state.value = _state.value.copy(
            showRestoreDialog = false,
            isLoading = false,
            restoreKey = "",
        )
    }

    private suspend fun retrieveData(key: String) {
        val restoredPresetsResponse = firestoreHelper.getData(key, CollectionPath.PRESETS, Preset::class.java)
        val restoredStatsResponse = firestoreHelper.getData(key, CollectionPath.STATS, Stat::class.java)

        processResponse(restoredPresetsResponse) { presets ->
            val presetsFiltered = presets.filter { it.userId == store.state.value.userEmail }
            presetDao.insertPresets(presetsFiltered)
        }
        processResponse(restoredStatsResponse) { stats -> statDao.insertStats(stats) }

    }

    private fun validateRestore(): Boolean {
        if (_state.value.restoreKey.isEmpty()) {
            sendUserEvent(ProfileScreenMessages.UserKeyCannotBeEmpty)
            return false
        }
        return true
    }

    private suspend fun <T: Any> processResponse(
        response: IFirestoreHelper.Response<List<T>>,
        actionBlock: suspend (List<T>) -> Unit
    ) {
        when (response) {
            is IFirestoreHelper.Response.Success -> {
                actionBlock(response.data)
                sendUserEvent(ProfileScreenMessages.RestoreSuccessful)
            }
            is IFirestoreHelper.Response.Error -> {
                val errorMessage = response.message
                sendUserEvent(ProfileScreenMessages.RestoreError(errorMessage))
            }
        }
    }


    private fun toggleRestoreDialog() {
        _state.value = _state.value.copy(showRestoreDialog = !_state.value.showRestoreDialog)
    }

    private fun updateRestoreKey(restoreKey: String) {
        _state.value = _state.value.copy(restoreKey = restoreKey)
    }

    private fun toggleIsLoading() {
        _state.value = _state.value.copy(isLoading = !_state.value.isLoading)
    }

    private fun backupDataWithKey() = viewModelScope.launch {
        if (!validateBackup()) return@launch

        toggleIsLoading()

        backUpData(
            key = _state.value.backupKey,
            afterBackup = {
                _state.value = _state.value.copy(
                    showBackupDialog = false,
                    isLoading = false,
                    backupKey = "",
                )
            }
        )
    }

    private fun backUpData(key: String, afterBackup: () -> Unit) {
        viewModelScope.launch {
            combine(statDao.getStats(), presetDao.getPresets()) { stats, presets ->
                val presetsFiltered = presets.filter { it.userId == store.state.value.userEmail }
                val statsFiltered = stats.filter { it.userId == store.state.value.userEmail }
                firestoreHelper.saveData(key, statsFiltered, CollectionPath.STATS)
                firestoreHelper.saveData(key, presetsFiltered, CollectionPath.PRESETS)
                sendUserEvent(ProfileScreenMessages.BackupSuccessful)
                afterBackup()
            }.collect {
                // Cancel the job after `afterBackup` is executed.
                this.cancel()
            }
        }
    }

    private fun validateBackup(): Boolean {
        if (_state.value.backupKey.isEmpty()) {
            sendUserEvent(ProfileScreenMessages.UserKeyCannotBeEmpty)
            return false
        }
        return true
    }


    private fun toggleBackupDialog() {
        _state.value = _state.value.copy(
            showBackupDialog = !_state.value.showBackupDialog
        )
    }

    private fun updateBackupKey(backupKey: String) {
        _state.value = _state.value.copy(backupKey = backupKey)
    }

    private fun updateTheme(darkTheme: Boolean) = viewModelScope.launch {
        store.update { it.copy(darkTheme = darkTheme.toString()) }
        _state.value = _state.value.copy(darkTheme = darkTheme)
        settingsManager.saveStringSetting(SettingsManager.DARK_THEME, darkTheme.toString())
    }

    private fun saveProfileChanges() {
        val dailyGoal = _state.value.dailyGoalPreliminary.ifEmpty { "1" }
        val username = _state.value.usernamePreliminary
        _state.value = _state.value.copy(
            username = username,
            dailyGoal = dailyGoal,
        )

        viewModelScope.launch {
            settingsManager.saveIntSetting(SettingsManager.DAILY_GOAL, dailyGoal.toInt())
            settingsManager.saveStringSetting(SettingsManager.USERNAME, username)
            store.update { it.copy(username = username, dailyGoal = dailyGoal) }
        }

    }

    private fun updateDailyGoal(dailyGoal: String) {
        if ((dailyGoal.length < 4 && dailyGoal.all { it.isDigit() }) || dailyGoal.isEmpty())
            _state.value = _state.value.copy(dailyGoalPreliminary = dailyGoal.trim())
    }

    private fun updateUsername(username: String) {
        _state.value = _state.value.copy(usernamePreliminary = username)
    }

}

data class ProfileState(
    val username: String = "",
    val dailyGoal: String = "",
    val usernamePreliminary: String = "",
    val dailyGoalPreliminary: String = "",
    val darkTheme: Boolean = false,
    val backupKey: String = "",
    val showBackupDialog: Boolean = false,
    val restoreKey: String = "",
    val showRestoreDialog: Boolean = false,
    val isLoading: Boolean = false,
    val showBackupLoadingDialog: Boolean = false,
    val showRestoreLoadingDialog: Boolean = false,
    val showSyncWithGuestDialog: Boolean = false,
    val isLoggedIn: Boolean = false,
)

sealed interface ProfileEvents{
    data class OnUpdateUsername(val username: String): ProfileEvents
    data class OnUpdateDailyGoal(val dailyGoal: String): ProfileEvents
    data class OnUpdateTheme(val darkTheme: Boolean): ProfileEvents
    data object OnSave: ProfileEvents
    data class OnUpdateBackupKey(val backupKey: String): ProfileEvents
    data object OnBackupAction: ProfileEvents
    data class OnUpdateRestoreKey(val restoreKey: String): ProfileEvents
    data object OnRestoreAction: ProfileEvents
    data object OnToggleIsLoading: ProfileEvents
    data object OnLogout: ProfileEvents
    data object OnBackupWithKey: ProfileEvents
    data object OnRestoreWithKey: ProfileEvents
    data object OnToggleSyncWithGuestDialog: ProfileEvents
    data object OnSyncWithGuest: ProfileEvents
}


sealed class ProfileScreenMessages(val message: String) {
    object BackupSuccessful : ProfileScreenMessages("Backup successful")
    object RestoreSuccessful : ProfileScreenMessages("Restore successful")
    object UserKeyCannotBeEmpty : ProfileScreenMessages("User key cannot be empty")
    object DataSyncedSuccessfully : ProfileScreenMessages("Data synced successfully")

    class RestoreError(customMessage: String) : ProfileScreenMessages(customMessage)
}