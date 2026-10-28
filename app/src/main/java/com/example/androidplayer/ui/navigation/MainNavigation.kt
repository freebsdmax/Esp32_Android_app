package com.example.androidplayer.ui.navigation

import com.example.androidplayer.ui.i18n.tr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.androidplayer.data.ble.BlePlayerClient
import com.example.androidplayer.ui.screens.*
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import com.example.androidplayer.service.PlaybackService
import com.example.androidplayer.ui.notify.AppNotify
import com.example.androidplayer.ui.i18n.Loc

@EntryPoint
@InstallIn(SingletonComponent::class)
interface BleEntryPoint {
    fun bleClient(): BlePlayerClient
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    val items = listOf(
        NavigationItem(tr("Плеєр"), Icons.Default.PlayArrow, NavRoutes.PLAYER),
        NavigationItem(tr("Плейлист"), Icons.Default.QueueMusic, NavRoutes.PLAYLIST),
        NavigationItem("OTA", Icons.Default.Update, NavRoutes.OTA),
        NavigationItem(tr("Логи"), Icons.Default.Terminal, NavRoutes.SYSTEM_MONITOR)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentTitle = items.firstOrNull { item ->
        currentDestination?.hierarchy?.any { it.route == item.route } == true
    }?.title ?: when (currentDestination?.route) {
        NavRoutes.SETTINGS -> tr("Налаштування")
        NavRoutes.BLE_DISCOVERY -> tr("BLE пристрої")
        else -> tr("Ford Player")
    }

    val context = LocalContext.current
    val ble = remember {
        EntryPointAccessors.fromApplication(context.applicationContext, BleEntryPoint::class.java).bleClient()
    }
    val bleConnected by ble.isConnected.collectAsState()

    // Mini player lives while the device is connected; connection changes are
    // mirrored into the notification shade as well.
    var firstConnectionState by remember { mutableStateOf(true) }
    LaunchedEffect(bleConnected) {
        if (bleConnected) PlaybackService.start(context) else PlaybackService.stop(context)
        if (!firstConnectionState) {
            if (bleConnected) AppNotify.info(Loc.notifConnected())
            else AppNotify.error(Loc.notifDisconnected())
        }
        firstConnectionState = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    ConnectionBadge(bleConnected)
                },
                title = {
                    Text(
                        text = currentTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(NavRoutes.BLE_DISCOVERY) { launchSingleTop = true }
                    }) {
                        Icon(Icons.Default.Bluetooth, contentDescription = "BLE")
                    }
                    IconButton(onClick = {
                        navController.navigate(NavRoutes.SETTINGS) { launchSingleTop = true }
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = tr("Налаштування"))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title, modifier = Modifier.size(20.dp)) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            // A tab must always end up showing its own screen, even when a
                            // full-screen route (settings / BLE discovery) is on top.
                            val alreadyThere = navController.currentDestination?.route == screen.route
                            if (!alreadyThere) {
                                val popped = navController.popBackStack(screen.route, inclusive = false)
                                if (!popped) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.id) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
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
            composable(NavRoutes.PLAYER) { PlayerScreen() }
            composable(NavRoutes.PLAYLIST) { PlaylistScreen() }
            composable(NavRoutes.OTA) { OtaScreen() }
            composable(NavRoutes.SYSTEM_MONITOR) { SystemMonitorScreen() }
            composable(NavRoutes.SETTINGS) {
                SettingsScreen(
                    ble = ble,
                    onOpenBle = { navController.navigate(NavRoutes.BLE_DISCOVERY) { launchSingleTop = true } }
                )
            }
            composable(NavRoutes.BLE_DISCOVERY) {
                BleDiscoveryScreen(
                    onDeviceSelected = { },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
private fun ConnectionBadge(bleConnected: Boolean) {
    Row(
        modifier = Modifier.padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(9.dp)
                .clip(CircleShape)
                .background(if (bleConnected) Color(0xFF22C55E) else Color(0xFFEF4444))
        )
        Text(
            text = if (bleConnected) "On" else "Off",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class NavigationItem(
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val route: String
)
