package com.github.erotourtes.ui.screen.plot

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.github.erotourtes.model.PlotUIState
import com.github.erotourtes.ui.theme.AppTheme
import com.github.erotourtes.ui.theme.spacing
import com.github.erotourtes.ui.utils.DragAnchors
import com.github.erotourtes.ui.utils.ExpandableCard
import com.github.erotourtes.ui.utils.SwapToReveal

@Composable
fun PlotsView(
    fns: List<PlotUIState>,
    onPlotFormulaChange: (PlotUIState, String) -> Unit,
    onPlotVisibilityChange: (PlotUIState, Boolean) -> Unit,
    onPlotRemove: (PlotUIState) -> Unit,
    onPlotColorChange: (PlotUIState, Color) -> Unit,
    onPlotCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box {
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(fns, PlotUIState::uuid) { fn ->
                OpenablePlotView(
                    fn = fn,
                    onPlotRemove = onPlotRemove,
                    onPlotVisibilityChange = onPlotVisibilityChange,
                    onPlotFormulaChange = onPlotFormulaChange,
                    onPlotColorChange = onPlotColorChange,
                )
            }

            item {
                Button(onClick = onPlotCreate, modifier = Modifier.fillMaxWidth()) {
                    Text("Add plot")
                }
            }
        }
    }
}

@Composable
private fun OpenablePlotView(
    fn: PlotUIState,
    onPlotRemove: (PlotUIState) -> Unit,
    onPlotVisibilityChange: (PlotUIState, Boolean) -> Unit,
    onPlotFormulaChange: (PlotUIState, String) -> Unit,
    onPlotColorChange: (PlotUIState, Color) -> Unit,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isRemoving by remember { mutableStateOf(false) }
    var prevColor by remember { mutableStateOf(fn.color) }

    val height by animateDpAsState(
        targetValue = if (isRemoving) 0.dp else Dp.Unspecified,
        label = "Height animation",
        finishedListener = {
            onPlotRemove(fn)
        })

    ExpandableCard(
        modifier = Modifier.height(height),
        expandableContent = {
            ColorPickerScreen(
                initialColor = fn.color,
                onColorChange = {
                    prevColor = fn.color
                    onPlotColorChange(fn, it)
                },
                onBackPress = {
                    onPlotColorChange(fn, prevColor)
                    isExpanded = !isExpanded
                },
                modifier = Modifier.padding(horizontal = MaterialTheme.spacing.medium)
            )
        }, expanded = isExpanded
    ) {
        SwappablePlotView(
            fn = fn,
            onPlotRemove = { isExpanded = !isExpanded; isRemoving = true },
            onPlotVisibilityChange = onPlotVisibilityChange,
            onPlotFormulaChange = onPlotFormulaChange,
            onPlotColorChangeRequest = { isExpanded = !isExpanded },
        )
    }
    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
}

@Composable
private fun SwappablePlotView(
    fn: PlotUIState,
    onPlotRemove: (PlotUIState) -> Unit,
    onPlotVisibilityChange: (PlotUIState, Boolean) -> Unit,
    onPlotFormulaChange: (PlotUIState, String) -> Unit,
    onPlotColorChangeRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Another solution would be to use SwipeToDismiss
    SwapToReveal(
        onRemove = { onPlotRemove(fn)},
        hiddenContent = {
            PlotControls(
                isVisible = fn.isVisible,
                onPlotRemove = { onPlotRemove(fn) },
                onPlotVisibilityChange = { onPlotVisibilityChange(fn, it) },
            )
        },
    ) {
        PlotView(
            fn = fn,
            onPlotFormulaChange = { onPlotFormulaChange(fn, it) },
            onPlotColorChangeRequest = onPlotColorChangeRequest,
            modifier = modifier,
        )
    }
}

private val MATERIAL_INPUT_HEIGHT = 50.dp
private val MATERIAL_COLOR_PICKER_WIDTH = 30.dp

@Composable
fun PlotView(
    fn: PlotUIState,
    onPlotFormulaChange: (String) -> Unit,
    onPlotColorChangeRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(MATERIAL_INPUT_HEIGHT)
            .fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .size(MATERIAL_INPUT_HEIGHT)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Swipe to reveal",
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
        TextField(
            value = fn.function,
            onValueChange = onPlotFormulaChange,
            textStyle = MaterialTheme.typography.bodyMedium,
            label = { Text("Function") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            singleLine = true,
        )
        Box(modifier = Modifier
            .fillMaxHeight()
            .width(MATERIAL_COLOR_PICKER_WIDTH)
            .background(fn.color)
            .clickable { onPlotColorChangeRequest() })
    }
}

@Composable
fun PlotControls(
    isVisible: Boolean,
    onPlotRemove: () -> Unit,
    onPlotVisibilityChange: (Boolean) -> Unit,
) {
    Row {
        IconButton(
            onClick = onPlotRemove,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
                .height(MATERIAL_INPUT_HEIGHT)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onPrimary,
            )
        }
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
        Switch(
            checked = isVisible, onCheckedChange = onPlotVisibilityChange, modifier = Modifier
                .scale(0.8f)
                .rotate(-90f)
        )
        Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
    }
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
            onPlotFormulaChange = { _, _ -> },
            onPlotVisibilityChange = { _, _ -> },
            onPlotRemove = { },
            onPlotColorChange = { _, _ -> },
            onPlotCreate = { },
        )
    }
}