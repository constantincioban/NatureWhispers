package com.example.naturewhispers.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import com.example.naturewhispers.presentation.ui.SharedViewModel
import com.example.naturewhispers.presentation.ui.calendarScreen.CalendarScreen
import com.example.naturewhispers.presentation.ui.mainScreen.MainScreen
import com.example.naturewhispers.presentation.ui.addPresetScreen.AddPresetScreen
import com.example.naturewhispers.presentation.ui.authScreen.AuthScreen
import com.example.naturewhispers.presentation.ui.profileScreen.ProfileScreen

@Composable
fun Navigation(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    actions: Actions,
    store: Store<AppState>,
    sharedViewModel: SharedViewModel,
    ) {
//    val actions = remember(navController) { Actions(navController, store) }
    val storeState = store.state.collectAsState()
    val startDestination =
//        if (storeState.value.isLoggedIn)
            Screens.Main.route
//    else Screens.Auth.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {

        composable(
            route = Screens.Auth.route,
        ) {
            AuthScreen(store = store)
        }

        composable(
            route = Screens.Main.route,
        ) {
            MainScreen(
                navigateToAddPreset = actions.navigateToAddPreset,
                sharedViewModel = sharedViewModel,
            )
        }

        composable(
            route = Screens.AddPreset.routeWithArgs,
            arguments = Screens.AddPreset.arguments
        ) { backStackEntry ->
            AddPresetScreen(
                presetId = backStackEntry.arguments?.getInt(Screens.Preset.presetIdArg) ?: 0,
                navigateToMain = actions.navigateToMain,
                snackbarHostState = snackbarHostState,
                sharedViewModel = sharedViewModel,
            )
        }

        composable(
            route = Screens.Calendar.route
        ) {
            CalendarScreen()
        }

        composable(
            route = Screens.Profile.route
        ) {
            ProfileScreen()
        }

    }

}