package com.github.erotourtes.ui.screen.canvas

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.erotourtes.drawing.CanvasView
import com.github.erotourtes.model.PlotUIState
import com.github.erotourtes.model.PlotViewModel
import com.github.erotourtes.ui.theme.AppTheme
import com.github.erotourtes.ui.theme.spacing


@Composable
fun CanvasScreen(
    plotViewModel: PlotViewModel = viewModel(),
) {
    // TODO: brainstorm this
    val plotState by plotViewModel.plotUIState.collectAsState()

    // WTF: without this log, the canvas doesn't update
    Log.i("CanvasView", "Update is coming CanvasView: ${plotState.joinToString()}")

    CanvasLayout(
        plotState = plotState,
        onPlotFormulaChange = plotViewModel::changePlotFormula,
        onPlotHideStateChange = plotViewModel::changeHideState,
        onPlotRemove = plotViewModel::removePlot,
        onPlotColorChange = plotViewModel::changeColor,
        onPlotNotValid = { plotViewModel.changePlotValidity(it, false) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasLayout(
    plotState: List<PlotUIState>,
    onPlotFormulaChange: (PlotUIState, String) -> Unit,
    onPlotHideStateChange: (PlotUIState, Boolean) -> Unit,
    onPlotRemove: (PlotUIState) -> Unit,
    onPlotColorChange: (PlotUIState, Int) -> Unit,
    onPlotNotValid: (PlotUIState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            PlotsView(
                fns = plotState,
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                onPlotFormulaChange = onPlotFormulaChange,
                onPlotVisibilityChange = onPlotHideStateChange,
                onPlotRemove = onPlotRemove,
                onPlotColorChanged = onPlotColorChange,
            )
        },
        sheetShape = MaterialTheme.shapes.large,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        CanvasView(plotState, onPlotNotValid, modifier)
    }
}

@Preview(
    showBackground = true,
    name = "Home Preview"
)
@Composable
fun HomePreview() {
    AppTheme {
        Surface(color = MaterialTheme.colorScheme.error) {
            CanvasScreen()
        }
    }
}


