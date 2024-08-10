package com.example.naturewhispers.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

sealed class Screens(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    data object Auth: Screens("auth_screen", "Auth")
    data object Main: Screens("main_screen", "Main", Icons.Rounded.Home)
    data object Calendar: Screens("calendar_screen", "Calendar", Icons.Rounded.DateRange)
    data object Profile: Screens("profile_screen", "Profile", Icons.Rounded.PersonOutline)
    data object Preset: Screens("preset_screen", "Preset") {
        const val presetIdArg = "presetId"
        const val presetTitleArg = "presetTitle"
        val routeWithArgs = "$route/{$presetIdArg}{$presetTitleArg}"
        val arguments = listOf(
            navArgument(presetIdArg) { type = NavType.IntType },
            navArgument(presetTitleArg) { type = NavType.StringType }
        )

        fun uri(presetId: Int = 0, presetTitle: String = "No title found") = "$route/$presetId$presetTitle"

    }
    data object AddPreset : Screens("add_preset_screen", "Add preset") {
        const val presetIdArg = "presetId"
        val routeWithArgs = "$route/{$presetIdArg}"
        val arguments = listOf(
            navArgument(presetIdArg) { type = NavType.IntType }
        )

        fun uri(presetId: Int = 0) = "$route/$presetId"
    }

    companion object {
        val all = listOf(
            Main, Preset, AddPreset, Calendar, Profile, Auth
        )
    }
}

class Actions(navController: NavHostController) {

    val navigateToPreset: (Int, String) -> Unit = { id, title ->
        navController.navigate(Screens.Preset.uri(id, title))
    }
    val navigateToAddPreset: (Int) -> Unit = {
        navController.navigate(Screens.AddPreset.uri(it))
    }
    val navigateBack: () -> Unit = {
        navController.popBackStack()
    }
    val navigateToMain: () -> Unit = {
        navController.navigate(Screens.Main.route)
    }
}