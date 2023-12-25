package com.github.erotourtes.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.github.erotourtes.model.PlotViewModel
import com.github.erotourtes.ui.screen.main.MainScreen
import com.github.erotourtes.ui.screen.canvas.CanvasScreen

sealed class Screen(val route: String) {
    object MainScreen : Screen("main")
    object CanvasScreen : Screen("canvas")
}

@Composable
fun NavGraph(
    plotViewModel: PlotViewModel
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {
        composable(Screen.MainScreen.route) {
            MainScreen(
                plotViewModel = plotViewModel,
                navController = navController
            )
        }

        composable(Screen.CanvasScreen.route) {
            CanvasScreen(
                plotViewModel = plotViewModel,
                navController = navController
            )
        }
    }
}