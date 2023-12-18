package com.github.erotourtes.drawing

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView

@Composable
fun CanvasView(
    modifier: Modifier = Modifier
) {
    val colorScheme = Colors(
        MaterialTheme.colorScheme.primary.toArgb(),
        MaterialTheme.colorScheme.background.toArgb(),
        MaterialTheme.colorScheme.onBackground.toArgb(),
    )
    AndroidView(
        factory = { context ->
            CanvasViewNativeView(context).apply {
                setColors(colorScheme)
            }
        },
        modifier = modifier
    )
}

@Preview(
    showBackground = true
)
@Composable
fun Preview() {
    CanvasView()
}