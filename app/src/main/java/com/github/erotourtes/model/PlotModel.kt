package com.github.erotourtes.model

import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.erotourtes.utils.random
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.coroutineContext

data class PlotUIState(
    val color: Color,
    val function: String,
    val isVisible: Boolean = true,
    val isValid: Boolean = true,
    val uuid: String = java.util.UUID.randomUUID().toString()
)

class PlotViewModel : ViewModel() {
    // Jetpack compose doesn't detect changes if list is modified in place
    private val _plotUIState = MutableStateFlow<List<PlotUIState>>(mutableListOf())
    val plotUIState = _plotUIState.asStateFlow()

    init {
        _plotUIState.value = dumpPlotUIStates.toMutableStateList()
    }

    fun changePlotFormula(oldState: PlotUIState, newValue: String) {
        Log.i("PlotViewModel", "changePlotFormula: $newValue")
        updateProperty(oldState) {
            copy(function = newValue, isValid = true)
        }
    }

    fun changeHideState(plotUIState: PlotUIState, isVisible: Boolean) {
        Log.i("PlotViewModel", "changeHideState: $isVisible")
        updateProperty(plotUIState) {
            copy(isVisible = isVisible)
        }
    }

    fun removePlot(plotUIState: PlotUIState) {
        Log.i("PlotViewModel", "removePlot: $plotUIState")
        _plotUIState.value = _plotUIState
            .value
            .toMutableList()
            .apply { remove(plotUIState) }
    }

    fun changeColor(plotUIState: PlotUIState, color: Color) {
        Log.i("PlotViewModel", "changeColor: $color")
        updateProperty(plotUIState) {
            copy(color = color)
        }
    }

    fun changePlotValidity(plotUIState: PlotUIState, isValid: Boolean) {
        Log.i("PlotViewModel", "changePlotValidity: $isValid ${Thread.currentThread().name}")
        updateProperty(plotUIState) {
            copy(isValid = isValid)
        }
    }

    fun createNew(color: Color = Color.random()) {
        _plotUIState.update { list ->
            val updated = list.toMutableList()
            updated.add(
                PlotUIState(
                    color = color,
                    function = "",
                    isVisible = true,
                    isValid = true,
                )
            )
            updated
        }
    }

    private fun isInvalidIndex(index: Int): Boolean {
        if (index == -1) {
            Log.e(
                "PlotViewModel", "Severe: changePlotFormula: index == -1; happens on rapid typing with slow validation"
            )
            return true
        }
        return false
    }


    private fun updateProperty(
        plotUIState: PlotUIState,
        update: PlotUIState.() -> PlotUIState
    ) {
        val index = _plotUIState.value.indexOf(plotUIState)
        if (isInvalidIndex(index)) return
        _plotUIState.update { list ->
            val updated = list.toMutableList()
            updated[index] = plotUIState.update()
            updated
        }
    }
}