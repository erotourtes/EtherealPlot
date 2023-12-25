package com.github.erotourtes.ui.screen.canvas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.erotourtes.ui.screen.canvas.drawing.CanvasView
import com.github.erotourtes.model.PlotUIState
import com.github.erotourtes.model.PlotViewModel
import com.github.erotourtes.model.mockPlots
import com.github.erotourtes.ui.theme.AppTheme
import com.github.erotourtes.ui.theme.spacing


@Composable
fun CanvasScreen(
    plotViewModel: PlotViewModel
) {
    // TODO: brainstorm this
    val plotState by plotViewModel.plotUIState.collectAsState()

    CanvasLayout(plotState = plotState,
        onPlotFormulaChange = plotViewModel::changePlotFormulaSync,
        onPlotHideStateChange = plotViewModel::changeHideState,
        onPlotRemove = plotViewModel::removePlotSync,
        onPlotColorChange = plotViewModel::changeColor,
        onPlotNotValid = { plotViewModel.changePlotValidity(it, false) },
        onPlotCreate = { plotViewModel.createNewSync() })
}

val BOTTOM_SHEET_SCAFFOLD_HEIGHT = 56.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasLayout(
    plotState: List<PlotUIState>,
    onPlotFormulaChange: (PlotUIState, String) -> Unit,
    onPlotHideStateChange: (PlotUIState, Boolean) -> Unit,
    onPlotRemove: (PlotUIState) -> Unit,
    onPlotColorChange: (PlotUIState, Color) -> Unit,
    onPlotNotValid: (PlotUIState) -> Unit,
    onPlotCreate: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        sheetPeekHeight = BOTTOM_SHEET_SCAFFOLD_HEIGHT,
        scaffoldState = scaffoldState,
        sheetContent = {
            PlotsView(
                fns = plotState,
                onPlotFormulaChange = onPlotFormulaChange,
                onPlotVisibilityChange = onPlotHideStateChange,
                onPlotRemove = onPlotRemove,
                onPlotColorChange = onPlotColorChange,
                onPlotCreate = onPlotCreate,
                modifier = Modifier
                    .padding(MaterialTheme.spacing.medium)
                    .defaultMinSize(minHeight = 600.dp)
            )
        },
        sheetShape = MaterialTheme.shapes.large.copy(bottomStart = CornerSize(0), bottomEnd = CornerSize(0)),
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        CanvasView(plotState, onPlotNotValid, modifier.padding(bottom = BOTTOM_SHEET_SCAFFOLD_HEIGHT))
    }
}

@Preview(
    showBackground = true, name = "Home Preview"
)
@Composable
private fun CanvasLayoutPreview() {
    AppTheme {
        CanvasLayout(plotState = mockPlots,
            onPlotFormulaChange = { _, _ -> },
            onPlotHideStateChange = { _, _ -> },
            onPlotRemove = { },
            onPlotColorChange = { _, _ -> },
            onPlotNotValid = { _ -> },
            onPlotCreate = {})
    }
}