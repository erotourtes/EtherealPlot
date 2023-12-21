package com.github.erotourtes.ui.screen.plot.drawing

import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.github.erotourtes.model.PlotUIState
import com.github.erotourtes.model.dumpPlotUIStates

@Composable
fun CanvasView(
    plotState: List<PlotUIState>,
    onPlotNotValid: (PlotUIState) -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = Colors(
        MaterialTheme.colorScheme.primary.toArgb(),
        MaterialTheme.colorScheme.background.toArgb(),
        MaterialTheme.colorScheme.onBackground.toArgb(),
    )
    AndroidView(
        factory = { context -> Log.i("CanvasView", "Creating new native view"); CanvasViewNativeView(context) },
        modifier = modifier
    ) { view ->
        view.setColors(colorScheme)
        view.setFns(plotState)
        view.setOnPlotNotValid(onPlotNotValid)
    }
}

@Preview(
    showBackground = true
)
@Composable
fun Preview() {
    CanvasView(dumpPlotUIStates, {})
}