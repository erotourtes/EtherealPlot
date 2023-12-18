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
    modifier: Modifier = Modifier,
    onNameChanged: (PlotUIState, String) -> Unit = { _, _ -> },
) {
    LazyColumn(modifier = modifier.fillMaxWidth()) {
        items(fns) { fn ->
            PlotView(
                fn = fn,
                modifier = Modifier.clip(MaterialTheme.shapes.medium),
                onNameChanged = { onNameChanged(fn, it) },
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
    onNameChanged: (String) -> Unit = {},
    onColorChanged: (Color) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.height(MATERIAL_INPUT_HEIGHT).fillMaxWidth(),
    ) {
        IconButton(
            onClick = { /*TODO*/ }, modifier = Modifier.background(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
        TextField(
            value = fn.function,
            onValueChange = onNameChanged,
            textStyle = MaterialTheme.typography.bodyMedium,
            label = { Text("Function") },
            modifier = Modifier.fillMaxWidth().weight(1f),
            singleLine = true,
        )
        ColorPicker(
            initialColor = fn.color,
            onColorSelected = onColorChanged,
        )
    }
}

@Composable
fun ColorPicker(initialColor: Color, onColorSelected: (Color) -> Unit, modifier: Modifier = Modifier) {
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
            )
        )
    }
}