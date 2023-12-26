package com.github.erotourtes.ui.screen.main.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.erotourtes.model.PlotUIState

data class QuickFunction(
    val name: String,
    val formula: String,
    val icon: ImageVector,
)

val quickFunctionList = listOf(
    QuickFunction(
        name = "Linear",
        formula = "x",
        icon = Icons.Rounded.Search
    ),
    QuickFunction(
        name = "Quadratic",
        formula = "x^2",
        icon = Icons.Rounded.Search
    ),
    QuickFunction(
        name = "Cubic",
        formula = "x^3",
        icon = Icons.Rounded.Search
    ),
    QuickFunction(
        name = "Sine",
        formula = "sin(x)",
        icon = Icons.Rounded.Search
    ),
    QuickFunction(
        name = "Cosine",
        formula = "cos(x)",
        icon = Icons.Rounded.Search
    ),
    QuickFunction(
        name = "Tangent",
        formula = "tan(x)",
        icon = Icons.Rounded.Search
    ),
    QuickFunction(
        name = "Logarithmic",
        formula = "ln(x)",
        icon = Icons.Rounded.Search
    ),
    QuickFunction(
        name = "Square Root",
        formula = "sqrt(x)",
        icon = Icons.Rounded.Search
    ),
    QuickFunction(
        name = "Exponential",
        formula = "x^x",
        icon = Icons.Rounded.Search
    )
)