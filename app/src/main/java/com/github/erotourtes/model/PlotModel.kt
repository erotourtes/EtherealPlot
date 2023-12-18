package com.github.erotourtes.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

data class PlotUIState(
    val color: Color,
    val function: String,
    val isVisible: Boolean = true,
)

class PlotViewModel : ViewModel() {
    private val _plotUIState = MutableStateFlow<MutableList<PlotUIState>>(mutableListOf())

    val plotUIState = _plotUIState

    init {
        _plotUIState.value = dumpPlotUIStates.toMutableStateList()
    }

    fun changePlotFormula(oldState: PlotUIState, newValue: String) {
        Log.i("PlotViewModel", "changePlotFormula: $newValue")
        val newState = oldState.copy(function = newValue)
        val index = _plotUIState.value.indexOf(oldState)
        _plotUIState.value = _plotUIState.value.apply {
            set(index, newState)
        }
    }

    fun changeHideState(plotUIState: PlotUIState, isVisible: Boolean) {
        Log.i("PlotViewModel", "changeHideState: $isVisible")
        val index = _plotUIState.value.indexOf(plotUIState)
        _plotUIState.value = _plotUIState.value.apply {
            set(index, plotUIState.copy(isVisible = isVisible))
        }
    }

    fun removePlot(plotUIState: PlotUIState) {
        Log.i("PlotViewModel", "removePlot: $plotUIState")
        _plotUIState.value = _plotUIState.value.apply {
            remove(plotUIState)
        }
    }

    fun changeColor(plotUIState: PlotUIState, color: Int) {
        Log.i("PlotViewModel", "changeColor: $color")
        val index = _plotUIState.value.indexOf(plotUIState)
        _plotUIState.value = _plotUIState.value.apply {
            set(index, plotUIState.copy(color = Color(color)))
        }
    }
}