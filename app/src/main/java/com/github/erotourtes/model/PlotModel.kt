package com.github.erotourtes.model

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

data class PlotUIState(
    val color: Color,
    val function: String,
    val isVisible: Boolean = true,
    val isValid: Boolean = true,
)

class PlotViewModel : ViewModel() {
    private val _plotUIState = MutableStateFlow<MutableList<PlotUIState>>(mutableListOf())

    val plotUIState = _plotUIState

    init {
        _plotUIState.value = dumpPlotUIStates.toMutableStateList()
    }

    fun changePlotFormula(oldState: PlotUIState, newValue: String) {
        Log.i("PlotViewModel", "changePlotFormula: $newValue")
        val newState = oldState.copy(function = newValue, isValid = true)
        val index = _plotUIState.value.indexOf(oldState)

        // TODO: Brainstorm this
        if (isInvalidIndex(index)) return
        _plotUIState.value = _plotUIState.value.apply {
            set(index, newState)
        }
    }

    fun changeHideState(plotUIState: PlotUIState, isVisible: Boolean) {
        Log.i("PlotViewModel", "changeHideState: $isVisible")
        val index = _plotUIState.value.indexOf(plotUIState)
        if (isInvalidIndex(index)) return
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

    fun changeColor(plotUIState: PlotUIState, color: Color) {
        Log.i("PlotViewModel", "changeColor: $color")
        val index = _plotUIState.value.indexOf(plotUIState)
        if (isInvalidIndex(index)) return
        _plotUIState.value = _plotUIState.value.apply {
            set(index, plotUIState.copy(color = Color(color.toArgb())))
        }
    }

    fun changePlotValidity(plotUIState: PlotUIState, isValid: Boolean) {
        Log.i("PlotViewModel", "changePlotValidity: $isValid ${Thread.currentThread().name}")
        val index = _plotUIState.value.indexOf(plotUIState)
        if (isInvalidIndex(index)) return
        _plotUIState.value = _plotUIState.value.apply {
            set(index, plotUIState.copy(isValid = isValid))
        }
    }

    private fun isInvalidIndex(index: Int): Boolean {
        if (index == -1) {
            Log.e(
                "PlotViewModel",
                "Severe: changePlotFormula: index == -1; happens on rapid typing with slow validation"
            )
            return true
        }
        return false
    }
}