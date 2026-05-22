package com.example.studyfocus

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.studyfocus.data.SettingsManager
import com.example.studyfocus.ui.main.GoalsScreen
import com.example.studyfocus.ui.main.MainScreen
import com.example.studyfocus.ui.main.MainScreenViewModel
import com.example.studyfocus.ui.main.SettingsScreen
import com.example.studyfocus.ui.main.StatsScreen
import com.example.studyfocus.ui.main.TimerScreen

@Composable
fun AppNavigation(viewModel: MainScreenViewModel) {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                MainScreen(viewModel = viewModel)
            }
            composable("goals") {
                GoalsScreen(viewModel = viewModel)
            }
            composable("timer") {
                TimerScreen()
            }
            composable("stats") {
                StatsScreen()
            }
            composable("settings") {
                val context = LocalContext.current
                val settingsManager = SettingsManager(context)
                SettingsScreen(settingsManager = settingsManager)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf("home", "goals", "timer", "stats", "settings")
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            val icon = when (screen) {
                "home" -> Icons.Default.Home
                "goals" -> Icons.Default.List
                "timer" -> Icons.Default.PlayArrow
                "stats" -> Icons.Default.Info
                "settings" -> Icons.Default.Settings
                else -> Icons.Default.Home
            }
            val labelStr = when (screen) {
                "home" -> "홈"
                "goals" -> "목표"
                "timer" -> "타이머"
                "stats" -> "통계"
                "settings" -> "설정"
                else -> screen
            }
            NavigationBarItem(
                icon = { Icon(icon, contentDescription = labelStr) },
                label = { Text(labelStr) },
                selected = currentRoute == screen,
                onClick = {
                    navController.navigate(screen) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
