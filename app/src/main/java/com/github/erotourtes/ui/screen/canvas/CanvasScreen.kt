package com.github.erotourtes.ui.screen.canvas

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
fun CavnasScreen(
    plotViewModel: PlotViewModel = viewModel(),
) {
    // TODO: brainstorm this
    val plotState by plotViewModel.plotUIState.collectAsState()
    CanvasLayout(
        plotState = plotState,
        onNameChanged = plotViewModel::changeName,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CanvasLayout(
    plotState: List<PlotUIState>,
    onNameChanged: (PlotUIState, String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scaffoldState = rememberBottomSheetScaffoldState()
    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetContent = {
            PlotsView(
                fns = plotState,
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                onNameChanged = onNameChanged
            )
        },
        sheetShape = MaterialTheme.shapes.large,
        sheetContentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        CanvasView(plotState, modifier)
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
            CavnasScreen()
        }
    }
}


