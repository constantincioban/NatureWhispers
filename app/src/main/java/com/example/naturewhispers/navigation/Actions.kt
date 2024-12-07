package com.example.naturewhispers.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.naturewhispers.data.utils.toNavigationPath

sealed class Screens(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    data object Auth: Screens("auth_screen", "Auth", null)
    data object Main: Screens("main_screen", "Main", Icons.Rounded.Home)
    data object Calendar: Screens("calendar_screen", "Calendar", Icons.Rounded.DateRange)
    data object Profile: Screens("profile_screen", "Profile", Icons.Rounded.PersonOutline)
    data object AddPreset : Screens("add_preset_screen", "Add preset") {
        const val presetIdArg = "presetId"
        val routeWithArgs = "$route/{$presetIdArg}"
        val arguments = listOf(
            navArgument(presetIdArg) { type = NavType.IntType }
        )

        fun uri(presetId: Int = 0) = "$route/$presetId"
    }

    companion object {
        val all by lazy {   listOf(
            Main, AddPreset, Calendar, Profile, Auth
        )}
    }
}

class Actions(private val navController: NavHostController) {

    /**
     * Navigates to the specified route with the given parameters.
     *
     * If the route equals "back", this function will pop the back stack.
     *
     * @param route The route to navigate to. If empty or "back", navigates back.
     * @param params A list of parameters to be appended to the route as part of the URI path.
     */
    fun navigateTo(route: String, params: List<Any>) {
        if (route.isEmpty() || route == "back") {
            navController.popBackStack()
        } else {
            val uri = route + params.toNavigationPath()
            navController.navigate(uri)
        }
    }

    fun navigateAndClearStack(route: String, params: List<Any>) {
        if (route.isNotEmpty()) {
            val uri = route + params.toNavigationPath()
            navController.navigate(uri) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }
}