package com.github.erotourtes.model

import androidx.compose.ui.graphics.Color

val mockPlots = listOf(
    PlotUIState(
        color = Color.Red,
        function = "sin(x)",
        isVisible = true,
        isValid = true,
        id = 0,
    ),
    PlotUIState(
        color = Color.Blue,
        function = "cos(x)",
        isVisible = true,
        isValid = true,
        id = 1,
    ),
)
