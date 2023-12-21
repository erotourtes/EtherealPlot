package com.github.erotourtes.ui

import androidx.compose.runtime.Composable
import com.github.erotourtes.ui.screen.plot.CanvasScreen
import com.github.erotourtes.ui.theme.AppTheme

@Composable
fun EtherealPlotApp() {
    AppTheme {
        CanvasScreen()
    }
}