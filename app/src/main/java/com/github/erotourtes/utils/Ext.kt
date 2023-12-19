package com.github.erotourtes.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.graphics.withSave

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