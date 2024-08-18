package com.example.naturewhispers.presentation.ui.bottomNavigation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.naturewhispers.data.di.TAG
import com.example.naturewhispers.data.utils.ImmutableList
import com.example.naturewhispers.navigation.Screens
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import com.example.naturewhispers.presentation.ui.theme.GrayLighter
import kotlinx.coroutines.launch

@Composable
fun BottomBar(
    navigateTo: (route: String, params: List<Any>) -> Unit,
) {

    val screens = ImmutableList(Screens.all.filter { it.icon != null })
    val navigateToStable: (route: String, params: List<Any>) -> Unit = remember { navigateTo }
    var selected by remember {
        mutableStateOf<Screens>(Screens.Main)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.windowInsetsPadding(WindowInsets(0, 0, 0, 42))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            screens.forEachIndexed() { _, screen ->
                AddItem(
                    screen = screen,
                    navigateTo = navigateToStable,
                    selectedScreen = selected,
                    updateSelected = { selected = it }
                )
            }
        }
    }
}

@Composable
fun AddItem(
    screen: Screens,
    navigateTo: (route: String, params: List<Any>) -> Unit,
    selectedScreen: Screens = Screens.Main,
    updateSelected: (Screens) -> Unit = {},
) {
    Log.i(TAG, "AddItem: ${screen.title}")
    val selected = screen.route == selectedScreen.route
    val background =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor =
        if (selected) Color.White else Color.Black
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .clickable {
                navigateTo(screen.route, listOf())
                updateSelected(screen)
            }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = screen.icon!!,
                contentDescription = "icon",
                tint = contentColor
            )
            AnimatedVisibility(visible = selected) {
                Text(
                    text = screen.title,
                    color = contentColor,
                    fontSize = 18.sp
                )
            }
        }
    }

}

