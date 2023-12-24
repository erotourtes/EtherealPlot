package com.github.erotourtes.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.compose.ui.graphics.Color
import androidx.core.graphics.withSave
import com.github.erotourtes.model.PlotUIState
import com.github.erotourtes.room.plot.Plot
import kotlin.random.Random

/**
 * Draws text by reflecting it in the X axis.
 * Useful for drawing text in the Cartesian coordinate system.
 */
fun Canvas.drawTextInRightDirection(text: String, x: Float, y: Float, paint: Paint) {
    withSave {
        val textBoundaries = Rect().apply { paint.getTextBounds(text, 0, text.length, this) }
        translate(x - textBoundaries.width() / 2, y - textBoundaries.height() / 2)
        scale(1f, -1f)
        drawText(text, 0f, 0f, paint)
    }
}

inline fun Paint.withColor(c: Int, block: Paint.() -> Unit) {
    color = color.also {
        color = c
        block()
    }
}

fun Color.Companion.random(): Color {
    return Color(
        red = Random.nextFloat(),
        green = Random.nextFloat(),
        blue = Random.nextFloat(),
        alpha = 1f
    )
}

fun Plot.toPlotUIState() = PlotUIState(
    color = Color(color),
    function = function,
    isVisible = isVisible,
    isValid = isValid,
    id = id,
)