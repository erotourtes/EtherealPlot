package com.github.erotourtes.ui

import androidx.compose.runtime.Composable
import com.github.erotourtes.ui.screen.canvas.CanvasScreen
import com.github.erotourtes.ui.theme.AppTheme

@Composable
fun EtherealPlotApp() {
    AppTheme {
        CanvasScreen()
    }
}