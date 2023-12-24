package com.github.erotourtes.model

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.erotourtes.room.plot.Plot
import com.github.erotourtes.room.plot.PlotDao
import com.github.erotourtes.utils.random
import com.github.erotourtes.utils.toPlotUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlotUIState(
    val color: Color,
    val function: String,
    val isVisible: Boolean = true,
    val isValid: Boolean = true,
    val id: Long,
) {

    fun toPlot(): Plot {
        return Plot(
            color = color.toArgb(),
            function = function,
            isVisible = isVisible,
            isValid = isValid,
        ).apply {
            id = this@PlotUIState.id
        }
    }
}

class PlotViewModel(
    private val dao: PlotDao,
) : ViewModel() {
    // Jetpack compose doesn't detect changes if list is modified in place
    private val _plotUIState = MutableStateFlow<List<PlotUIState>>(listOf())
    val plotUIState = _plotUIState.asStateFlow()

    fun loadStateSync() {
        if (_plotUIState.value.isNotEmpty()) return

        viewModelScope.launch {
            _plotUIState.value = dao.getPreviousPlots().map { it.toPlotUIState() }
            Log.i("PlotViewModel", "loadState in plotViewModel ${_plotUIState.value}")
        }
    }

    fun saveStateSync() {
        viewModelScope.launch {
            Log.i("PlotViewModel", "saveState")
            dao.savePlots(_plotUIState.value.map { it.toPlot() })
        }
    }

    fun changePlotFormulaSync(oldState: PlotUIState, newValue: String) {
        Log.i("PlotViewModel", "changePlotFormula: $newValue")
        val updated = oldState.copy(function = newValue, isValid = true)
        viewModelScope.launch {
            dao.savePlot(updated.toPlot())
        }

        _plotUIState.value = _plotUIState.value.toMutableList().apply {
            set(indexOfFirst { it.id == oldState.id }, updated)
        }
        Log.i("PlotViewModel", "changePlotFormula: ${_plotUIState.value}")
    }

    fun removePlotSync(plotUIState: PlotUIState) {
        viewModelScope.launch {
            val plot = plotUIState.toPlot()
            Log.i("PlotViewModel", "removePlot: ${plot.function} ${plot.id}")
            dao.deletePlot(plot)
        }

        _plotUIState.value = _plotUIState.value.toMutableList().apply { remove(plotUIState) }
    }

    fun changeColor(plotUIState: PlotUIState, color: Color) {
        Log.i("PlotViewModel", "changeColor: $color")
        updateProperty(plotUIState) {
            copy(color = color)
        }
    }

    fun changeHideState(plotUIState: PlotUIState, isVisible: Boolean) {
        Log.i("PlotViewModel", "changeHideState: $isVisible")
        updateProperty(plotUIState) {
            copy(isVisible = isVisible)
        }
    }

    fun changePlotValidity(plotUIState: PlotUIState, isValid: Boolean) {
        Log.i("PlotViewModel", "changePlotValidity: $isValid ${Thread.currentThread().name}")
        updateProperty(plotUIState) {
            copy(isValid = isValid)
        }
    }

    fun createNewSync(color: Color = Color.random()) {
        Log.i("PlotViewModel", "createNew: $color")
        viewModelScope.launch {
            val plot = Plot(
                color = color.toArgb(),
                function = "",
                isVisible = true,
                isValid = true,
            )
            val id = dao.savePlot(plot)
            Log.i("PlotViewModel", "createNew: $id")
            _plotUIState.update { list ->
                val updated = list.toMutableList()
                updated.add(
                    PlotUIState(
                        color = color, function = "", isVisible = true, isValid = true, id = id
                    )
                )
                updated
            }
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
        plotUIState: PlotUIState, update: PlotUIState.() -> PlotUIState
    ) {
        val index = _plotUIState.value.indexOfFirst { it.id == plotUIState.id }
        if (isInvalidIndex(index)) return
        _plotUIState.update { list ->
            val updated = list.toMutableList()
            updated[index] = updated[index].update()
            updated
        }
    }
}