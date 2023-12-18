package com.github.erotourtes.ui.screen.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.erotourtes.model.PlotUIState
import com.github.erotourtes.ui.theme.AppTheme
import com.github.erotourtes.ui.theme.spacing

@Composable
fun PlotsView(
    fns: List<PlotUIState>,
    onPlotFormulaChange: (PlotUIState, String) -> Unit,
    onPlotVisibilityChange: (PlotUIState, Boolean) -> Unit,
    onPlotRemove: (PlotUIState) -> Unit,
    onPlotColorChanged: (PlotUIState, Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(fns) { fn ->
            PlotView(
                fn = fn,
                onPlotFormulaChange = { onPlotFormulaChange(fn, it) },
                onPlotVisibilityChange = { onPlotVisibilityChange(fn, it) },
                onPlotRemove = { onPlotRemove(fn) },
                onPlotColorChange = { onPlotColorChanged(fn, it) },
                modifier = Modifier.clip(MaterialTheme.shapes.medium),
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
        }
    }
}

private val MATERIAL_INPUT_HEIGHT = 50.dp
private val MATERIAL_COLOR_PICKER_WIDTH = 30.dp


@Composable
fun PlotView(
    fn: PlotUIState,
    onPlotFormulaChange: (String) -> Unit,
    onPlotVisibilityChange: (Boolean) -> Unit,
    onPlotRemove: () -> Unit,
    onPlotColorChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(MATERIAL_INPUT_HEIGHT).fillMaxWidth(),
    ) {
        IconButton(
            onClick = onPlotRemove, modifier = Modifier.background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
        TextField(
            value = fn.function,
            onValueChange = onPlotFormulaChange,
            textStyle = MaterialTheme.typography.bodyMedium,
            label = { Text("Function") },
            modifier = Modifier.fillMaxWidth().weight(1f),
            singleLine = true,
        )
        ColorPicker(
            initialColor = fn.color,
            onColorSelect = onPlotColorChange,
        )
    }
}

@Composable
fun ColorPicker(initialColor: Color, onColorSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier.fillMaxHeight().width(MATERIAL_COLOR_PICKER_WIDTH).background(initialColor)
    ) {}
}

@Preview(
    showBackground = true, name = "PlotView Preview"
)
@Composable
fun PlotViewPreview() {
    AppTheme {
        PlotsView(
            fns = listOf(
                PlotUIState(MaterialTheme.colorScheme.primary, "x^2"),
                PlotUIState(MaterialTheme.colorScheme.secondary, "sin(5x) + x^2"),
                PlotUIState(MaterialTheme.colorScheme.tertiary, "2x^2"),
            ),
            onPlotColorChanged = { _, _ -> },
            onPlotFormulaChange = { _, _ -> },
            onPlotVisibilityChange = { _, _ -> },
            onPlotRemove = { },
        )
    }
}