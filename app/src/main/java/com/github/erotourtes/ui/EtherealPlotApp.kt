package com.github.erotourtes.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.erotourtes.data.AppContainer
import com.github.erotourtes.model.PlotViewModel
import com.github.erotourtes.ui.theme.AppTheme

@Composable
fun EtherealPlotApp(
    appContainer: AppContainer,
) {
    val plotViewModel: PlotViewModel = viewModel(factory = PlotViewModel.provideFactory(appContainer.plotRepository))

    AppTheme {
        NavGraph(plotViewModel = plotViewModel)
    }
}