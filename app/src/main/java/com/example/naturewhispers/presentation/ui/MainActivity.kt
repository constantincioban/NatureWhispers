package com.example.naturewhispers.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.foregroundService.PlayerService
import com.example.naturewhispers.data.utils.observeWithLifecycle
import com.example.naturewhispers.navigation.Actions
import com.example.naturewhispers.navigation.Navigation
import com.example.naturewhispers.navigation.Screens
import com.example.naturewhispers.presentation.components.NWCustomTheme
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.ContentType
import com.example.naturewhispers.presentation.redux.Store
import com.example.naturewhispers.presentation.ui.bottomNavigation.BottomBar
import com.example.naturewhispers.presentation.ui.theme.NatureWhispersTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var store: Store<AppState>
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        launchIsPlayingObserver()
        enableEdgeToEdge()
        setContent {
            val isSystemInDarkTheme = isSystemInDarkTheme()
            val darkTheme = store.state.map { it.darkTheme }.collectAsState(initial = isSystemInDarkTheme)
            NWCustomTheme(darkTheme = darkTheme.value == "true") {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val actions = remember(navController) { Actions(navController) }

                val navigationBarColor = MaterialTheme.colorScheme.surfaceVariant
                val backgroundColor = MaterialTheme.colorScheme.background
                Surface(
                    modifier = Modifier,
                ) {
                    Scaffold(
                        bottomBar = {
                            if (currentRoute(navController = navController) == null ||
                                (currentRoute(navController = navController) != Screens.AddPreset.routeWithArgs &&
                                        currentRoute(navController = navController) != Screens.Auth.route)
                            ) {
                                BottomBar(navController = navController, store = store)
                                window.navigationBarColor = navigationBarColor.toArgb()
                            } else if (currentRoute(navController = navController) == Screens.AddPreset.routeWithArgs) {
                                window.navigationBarColor = backgroundColor.toArgb()
                            } else if (currentRoute(navController = navController) == Screens.Auth.route)
                                window.navigationBarColor = Color.Transparent.toArgb()

                        },
                        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },

                        ) {

                        Navigation(
                            navController = navController,
                            snackbarHostState = snackbarHostState,
                            actions = actions,
                            store = store,
                            sharedViewModel = sharedViewModel,
                        )
                    }

                }
            }
        }
    }


    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.Main) {
            if (store.state.value.isPlaying) {
                Log.i(TAG, "onStart: ")
                val intent = Intent(this@MainActivity, PlayerService::class.java)
                intent.action = PlayerService.ACTION_STOP
                startForegroundService(intent)

            }
        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch(Dispatchers.Main) {
            if (store.state.value.isPlaying) {
                Log.i(TAG, "onStop ")
                val intent = Intent(this@MainActivity, PlayerService::class.java)
                intent.action = PlayerService.ACTION_START
                startForegroundService(intent)

            }
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
    private fun launchIsPlayingObserver() {
        /*lifecycleScope.launch(Dispatchers.Main) {
            store.state.collect { state ->
                if (!state.isPlaying) {
                    sharedViewModel.dispatchEvent(PlayerEvents.OnStopPlayer)
                    delay(2000)
                    Log.i(TAG, "launchIsPlayingObserver: 0------------------------")
                    Intent(applicationContext, PlayerService::class.java).also {
                        stopService(it)
                    }
                }
            }
        }*/

    }

}


@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    Log.i(TAG, "currentRoute: " + navBackStackEntry?.destination?.route)
    return navBackStackEntry?.destination?.route
}
