package com.example.naturewhispers.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.naturewhispers.R
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.foregroundService.PlayerService
import com.example.naturewhispers.data.local.preferences.SettingsManager
import com.example.naturewhispers.data.mediaPlayer.PlayerManager
import com.example.naturewhispers.data.permission.NOTIFICATION_PERMISSION
import com.example.naturewhispers.data.permission.PermissionDialog
import com.example.naturewhispers.data.permission.isPermissionGranted
import com.example.naturewhispers.data.permission.permissionLauncher
import com.example.naturewhispers.data.utils.openAppSettings
import com.example.naturewhispers.navigation.Actions
import com.example.naturewhispers.navigation.Navigation
import com.example.naturewhispers.navigation.Screens
import com.example.naturewhispers.presentation.components.NWCustomTheme
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import com.example.naturewhispers.presentation.ui.bottomNavigation.BottomBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var store: Store<AppState>
    @Inject
    lateinit var playerManager: PlayerManager

    private val sharedViewModel: SharedViewModel by viewModels()

    @SuppressLint("WrongConstant")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val selectedUri: Uri = data?.data ?: return
            val takeFlags = data.flags and (Intent.FLAG_GRANT_READ_URI_PERMISSION)
            contentResolver.takePersistableUriPermission(selectedUri, takeFlags)
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val traceFile = File(getExternalFilesDir(null), "tracefile.trace")
//        Debug.startMethodTracing(traceFile.absolutePath)

        enableEdgeToEdge()
        setContent {


            val darkTheme = store.state.map { it.darkTheme }.collectAsState(initial = "")

            NWCustomTheme(darkTheme = if (darkTheme.value.isEmpty()) isSystemInDarkTheme() else darkTheme.value.toBoolean()) {





                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val actions = remember(navController) { Actions(navController) }
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                val currentRoute by remember {
                    mutableStateOf(navBackStackEntry?.destination?.route )
                }
                val navigationBarColor = MaterialTheme.colorScheme.surfaceVariant
                val backgroundColor = MaterialTheme.colorScheme.background
                Surface(
                    modifier = Modifier,
                ) {
                    Scaffold(
                        bottomBar = {
                            if (currentRoute != null && currentRoute == Screens.AddPreset.route)
                                window.navigationBarColor = backgroundColor.toArgb()
                            else {
                                BottomBar(navigateTo = { route, _ ->
                                    actions.navigateTo(route, listOf())
                                })
                                window.navigationBarColor = navigationBarColor.toArgb()
                            }
                        },
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

                        ) {

                        Navigation(
                            navController = navController,
                            snackbarHostState = snackbarHostState,
                            actions = actions,
                            playerManager = playerManager,
                        )

                    }

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (store.state.value.isPlaying) {
            val intent = Intent(this@MainActivity, PlayerService::class.java)
            intent.action = PlayerService.ACTION_STOP
            startForegroundService(intent)

        }
    }

    override fun onStop() {
        super.onStop()
        if (store.state.value.isPlaying) {
            val intent = Intent(this@MainActivity, PlayerService::class.java)
            intent.action = PlayerService.ACTION_START
            startForegroundService(intent)

        }
    }

    //    private fun launchLoginObserver(navController: NavHostController) {
//        lifecycleScope.launch(Dispatchers.Main) {
//            store.state.collect { state ->
//                if (!state.isLoggedIn) {
//                    navController.navigate(Screens.Auth.route) {
//                        popUpTo(0) // reset stack
//                    }
//                }
//            }
//        }
//    }

}
