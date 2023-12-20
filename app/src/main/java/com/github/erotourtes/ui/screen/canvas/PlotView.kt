package com.github.erotourtes.ui.screen.canvas

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.erotourtes.model.PlotUIState
import com.github.erotourtes.ui.theme.AppTheme
import com.github.erotourtes.ui.theme.spacing

@OptIn(ExperimentalFoundationApi::class)
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
    var selectedFn by remember { mutableStateOf<PlotUIState?>(null) }

    Box {
        // Another solution would be to use SwipeToDismiss
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(fns) { fn ->
                key(fn.uuid) {
                    SwapToReveal(
                        onAnchorChanged = { anchor ->
                            Log.i("PlotsView", "onAnchorChanged: $anchor for $fn")
                            if (anchor == DragAnchors.End) {
                                onPlotRemove(fn)
                            }
                        },
                        hiddenContent = {
                            PlotControls(
                                isVisible = fn.isVisible,
                                onPlotRemove = { onPlotRemove(fn) },
                                onPlotVisibilityChange = { onPlotVisibilityChange(fn, it) },
                            )
                        },
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.medium)
                            .animateItemPlacement(),
                    ) {
                        PlotView(
                            fn = fn,
                            onPlotFormulaChange = { onPlotFormulaChange(fn, it) },
                            onPlotColorChangeRequest = { selectedFn = fn },
                        )
                    }
                    Spacer(modifier = Modifier.height(MaterialTheme.spacing.medium))
                }
            }

            item {
                Button(onClick = onPlotCreate) {
                    Text("Add plot")
                }
            }
        }

        AnimatedVisibility(
            visible = selectedFn != null,
            enter = slideInVertically(
                initialOffsetY = { it }, animationSpec = tween(300)
            ),
            exit = slideOutVertically(
                targetOffsetY = { it }, animationSpec = tween(300)
            ),
        ) {
            if (selectedFn != null) ColorPickerScreen(
                initialColor = selectedFn!!.color,
                onColorChange = {
                    onPlotColorChange(selectedFn!!, it)
                },
                onBackPress = { selectedFn = null },
            )
        }
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
            .clickable { onPlotColorChangeRequest() }) {}
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