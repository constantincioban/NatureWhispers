package com.example.naturewhispers.presentation.ui.bottomNavigation

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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.naturewhispers.navigation.Screens
import com.example.naturewhispers.presentation.redux.AppState
import com.example.naturewhispers.presentation.redux.Store
import com.example.naturewhispers.presentation.ui.theme.GrayLighter
import kotlinx.coroutines.launch

@Composable
fun BottomBar(
    navController: NavHostController,
    store: Store<AppState>
) {

    val navStackBackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navStackBackEntry?.destination

    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.windowInsetsPadding(WindowInsets(0,0,0,42))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Screens.all.forEachIndexed() { _, screen ->
                if (screen.icon == null) return@forEachIndexed
                AddItem(
                    screen = screen,
                    currentDestination = currentDestination,
                    navController = navController,
                    store = store
                )
            }
        }
    }
}

@Composable
fun AddItem(
    screen: Screens,
    currentDestination: NavDestination?,
    navController: NavHostController,
    store: Store<AppState>
) {
    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
    val background =
        if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor =
        if (selected) Color.White else Color.Black
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .clickable {
                scope.launch {
                    store.update { it.copy(topBarTitle = screen.title) }
                }
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            }
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector =  screen.icon!!,
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

