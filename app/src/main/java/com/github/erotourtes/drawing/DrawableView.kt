package com.github.erotourtes.drawing

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

data class Point(val x: Float, val y: Float) {
    companion object {
        fun cartesianToCanvas(cartesian: Point, canvas: Canvas): Point =
            Point(cartesian.x, canvas.height - cartesian.y)
    }
}

fun Canvas.cartesianDrawLine(start: Point, end: Point, paint: Paint) {
    val c1 = Point.cartesianToCanvas(start, this)
    val c2 = Point.cartesianToCanvas(end, this)

    drawLine(c1.x, c1.y, c2.x, c2.y, paint)
}

fun Canvas.cartesianDrawText(text: String, point: Point, paint: Paint) {
    val c = Point.cartesianToCanvas(point, this)
    drawText(text, c.x, c.y, paint)
}

class DrawableView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var userCoords: Point? = null
        set(value) {
            field = value
            invalidate()
        }

    var scale = 1f
        set(value) {
            field = value
            invalidate()
        }

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 10f
        textSize = 50f
    }

    private val gridPaint = Paint().apply {
        color = Color.GRAY
        strokeWidth = 1f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawAxis(canvas)
        grid(canvas, gridPaint)
        drawFunction(canvas)
        userCoords?.let {
            canvas.drawCircle(it.x, it.y, 50f, paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        userCoords = Point(event.x, event.y)
        Log.i("DrawableView", "User touched at $userCoords")
        return true
    }

    private fun drawAxis(canvas: Canvas) {
        canvas.cartesianDrawLine(Point(0f, 0f), Point(0f, height.toFloat()), paint)
        canvas.cartesianDrawLine(Point(0f, 0f), Point(width.toFloat(), 0f), paint)

        val step = scale * 100
        val xOffset = 50f
        val yOffset = 50f
        for (i in 0..width step step.toInt()) {
            canvas.cartesianDrawText(i.toString(), Point(i.toFloat(), yOffset), paint)
        }

        for (i in 0..height step step.toInt()) {
            canvas.cartesianDrawText(i.toString(), Point(xOffset, i.toFloat()), paint)
        }
    }

    private fun grid(canvas: Canvas, paint: Paint) {
        val step = scale * 100
        for (i in 0..width step step.toInt()) {
            canvas.cartesianDrawLine(Point(i.toFloat(), 0f), Point(i.toFloat(), height.toFloat()), paint)
        }
        for (i in 0..height step step.toInt()) {
            canvas.cartesianDrawLine(Point(0f, i.toFloat()), Point(width.toFloat(), i.toFloat()), paint)
        }
    }

    private fun linear(x: Float): Float = 2 * x + 1

    private fun drawFunction(canvas: Canvas) {
        val step = scale * 100
        for (i in 0..width step step.toInt()) {
            val x = i.toFloat()
            val y = linear(x)
            canvas.cartesianDrawLine(Point(0f, 0f), Point(x, y), paint)
        }
    }
}