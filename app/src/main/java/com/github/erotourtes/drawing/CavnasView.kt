package com.github.erotourtes.drawing

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
    onPlotNotValid: (PlotUIState) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // TODO: brainstorm this
    val colorScheme = Colors(
        MaterialTheme.colorScheme.primary.toArgb(),
        MaterialTheme.colorScheme.background.toArgb(),
        MaterialTheme.colorScheme.onBackground.toArgb(),
    )
    AndroidView(
        factory = { context -> CanvasViewNativeView(context) },
        modifier = modifier
    ) { view ->
//        Log.i("CanvasView", "CanvasView: ${plotState.joinToString()}")
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
    CanvasView(dumpPlotUIStates)
}