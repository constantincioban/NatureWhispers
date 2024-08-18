package com.example.naturewhispers.presentation.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Debug
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.io.File
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
//        val traceFile = File(getExternalFilesDir(null), "tracefile.trace")
//        Debug.startMethodTracing(traceFile.absolutePath)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }
        enableEdgeToEdge()
        setContent {
            val darkTheme = store.state.map { it.darkTheme }.filterNot { it.isEmpty() }
                .collectAsState(initial = isSystemInDarkTheme().toString())
            NWCustomTheme(darkTheme = darkTheme.value == true.toString()) {
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }
                val actions = remember(navController) { Actions(navController) }
                val navBackStackEntry by navController.currentBackStackEntryAsState()

                val currentRoute by remember {
                    mutableStateOf(navBackStackEntry?.destination?.route )
                }
                Log.i(TAG, "onCreate: currentRoute = $currentRoute")
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
                            sharedViewModel = sharedViewModel,
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


@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}
