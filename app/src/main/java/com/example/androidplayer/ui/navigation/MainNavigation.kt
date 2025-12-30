package com.example.androidplayer.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.androidplayer.ui.screens.*

/**
 * Главная навигация приложения
 */
@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    
    val items = listOf(
        NavigationItem(
            title = "Плеер",
            icon = Icons.Default.PlayArrow,
            route = NavRoutes.PLAYER
        ),
        NavigationItem(
            title = "Настройки",
            icon = Icons.Default.Settings,
            route = NavRoutes.SETTINGS
        ),
        NavigationItem(
            title = "Wi-Fi",
            icon = Icons.Default.Wifi,
            route = NavRoutes.WIFI_SETTINGS
        ),
        NavigationItem(
            title = "OTA",
            icon = Icons.Default.Update,
            route = NavRoutes.OTA
        ),
        NavigationItem(
            title = "Логи",
            icon = Icons.Default.Terminal,
            route = NavRoutes.SYSTEM_MONITOR
        )
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                // Pop up to the start destination of the graph to
                                // avoid building up a large stack of destinations
                                // on the back stack as users select items
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                // Avoid multiple copies of the same destination when
                                // reselecting the same item
                                launchSingleTop = true
                                // Restore state when reselecting a previously selected item
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.PLAYER,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoutes.PLAYER) {
                PlayerScreen()
            }
            composable(NavRoutes.SETTINGS) {
                SettingsScreen()
            }
            composable(NavRoutes.WIFI_SETTINGS) {
                WifiSettingsScreen()
            }
            composable(NavRoutes.OTA) {
                OtaScreen()
            }
            composable(NavRoutes.SYSTEM_MONITOR) {
                SystemMonitorScreen()
            }
        }
    }
}

/**
 * Элемент навигации
 */
data class NavigationItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)