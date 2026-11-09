package com.filex.studio.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material.icons.rounded.FolderOpen
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Storage
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.filex.studio.AppContainer
import com.filex.studio.ui.screens.BrowseScreen
import com.filex.studio.ui.screens.HomeScreen
import com.filex.studio.ui.screens.SettingsScreen
import com.filex.studio.ui.screens.StorageScreen
import com.filex.studio.ui.screens.ToolsScreen

enum class FxDestination(val route: String, val label: String, val icon: ImageVector) {
    HOME("home", "Home", Icons.Rounded.Home),
    BROWSE("browse", "Browse", Icons.Rounded.FolderOpen),
    STORAGE("storage", "Storage", Icons.Rounded.Storage),
    TOOLS("tools", "Tools", Icons.Rounded.Construction),
    SETTINGS("settings", "Settings", Icons.Rounded.Settings),
}

@Composable
fun FxApp(container: AppContainer) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                FxDestination.entries.forEach { dest ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == dest.route } == true,
                        onClick = {
                            navController.navigate(dest.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(dest.icon, contentDescription = dest.label) },
                        label = { Text(dest.label) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = FxDestination.HOME.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(FxDestination.HOME.route) { HomeScreen(container) }
            composable(FxDestination.BROWSE.route) { BrowseScreen(container) }
            composable(FxDestination.STORAGE.route) { StorageScreen(container) }
            composable(FxDestination.TOOLS.route) { ToolsScreen(container) }
            composable(FxDestination.SETTINGS.route) { SettingsScreen(container) }
        }
    }
}
