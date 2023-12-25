package com.github.erotourtes.ui.screen.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.github.erotourtes.R
import com.github.erotourtes.model.PlotViewModel
import com.github.erotourtes.ui.Screen
import com.github.erotourtes.ui.screen.main.data.QuickFunction


@Composable
fun MainScreen(
    plotViewModel: PlotViewModel, navController: NavController
) {
    MainLayout(
        onGoToPlots = { navController.navigate(Screen.CanvasScreen.route) },
        onGoToPlotsPreviousSession = {
            plotViewModel.restorePreviousSession()
            navController.navigate(Screen.CanvasScreen.route)
        },
        onGoToPlotsWithPlot = {
            plotViewModel.createNewSync(function = it.formula)
            navController.navigate(Screen.CanvasScreen.route)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainLayout(
    onGoToPlots: () -> Unit,
    onGoToPlotsPreviousSession: () -> Unit,
    onGoToPlotsWithPlot: (QuickFunction) -> Unit,
) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(title = {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
            )
        })
    }, floatingActionButton = {
        FloatingActionButton(onClick = onGoToPlots, content = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Create new plots",
            )
        })
    }, content = {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(it)
        ) {
            QuickFunctionAction(
                onQuickFunctionClick = onGoToPlotsWithPlot
            )
            Button(onClick = onGoToPlotsPreviousSession) {
                Text(text = "Restore previous session")
            }
        }
    })
}

@Preview
@Composable
private fun Preview() {
    MainLayout({}, {}, {})
}
