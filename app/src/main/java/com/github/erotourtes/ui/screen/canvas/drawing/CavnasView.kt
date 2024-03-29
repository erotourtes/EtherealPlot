package com.github.erotourtes.ui.screen.canvas.drawing

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.github.erotourtes.model.PlotUIState

@Composable
fun CanvasView(
    plotState: List<PlotUIState>, onPlotNotValid: (PlotUIState) -> Unit, modifier: Modifier = Modifier
) {
    val colorScheme = Colors(
        MaterialTheme.colorScheme.primary.toArgb(),
        MaterialTheme.colorScheme.background.toArgb(),
        MaterialTheme.colorScheme.onBackground.toArgb(),
    )
    AndroidView(
        factory = { context -> Log.i("CanvasView", "Creating new native view"); CanvasViewNativeView(context) },
        modifier = modifier.clipToBounds()
    ) { view ->
        view.set(colorScheme, plotState, onPlotNotValid)
    }
}

@Preview(
    showBackground = true
)
@Composable
private fun Preview() {
    CanvasView(plotState = listOf(
        PlotUIState(
            color = androidx.compose.ui.graphics.Color.Red,
            function = "sin(x)",
            isVisible = true,
            isValid = true,
            id = 0,
        ),
        PlotUIState(
            color = androidx.compose.ui.graphics.Color.Blue,
            function = "tan(x), [0; 1.57079633]",
            isVisible = true,
            isValid = true,
            id = 1,
        ),
    ), {})
}
