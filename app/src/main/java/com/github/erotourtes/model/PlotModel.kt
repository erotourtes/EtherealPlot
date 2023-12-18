package com.github.erotourtes.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    var selectedPlot by mutableStateOf(0)
        private set

    init {
        _plotUIState.value = dumpPlotUIStates.toMutableStateList()
    }

    fun changeName(oldState: PlotUIState, newValue: String) {
        val newState = oldState.copy(function = newValue)
        val index = _plotUIState.value.indexOf(oldState)
        _plotUIState.value = _plotUIState.value.apply {
            set(index, newState)
        }
    }
}