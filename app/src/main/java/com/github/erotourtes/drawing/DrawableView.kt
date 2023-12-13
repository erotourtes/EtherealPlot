package com.github.erotourtes.drawing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.util.Log
import androidx.core.graphics.*

fun Canvas.drawTextInRightDirection(text: String, x: Float, y: Float, paint: Paint) {
    withSave {
        val textBoundaries = Rect().apply { paint.getTextBounds(text, 0, text.length, this) }
        translate(x - textBoundaries.width() / 2, y - textBoundaries.height() / 2)
        scale(1f, -1f)
        drawText(text, 0f, 0f, paint)
    }
}

class DrawableView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val matrixCamera = Matrix()
    private val matrixCartesian = Matrix()

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 10f
        textSize = 50f
    }

    init {
        updateCameraMatrixByTouch()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        matrixCartesian.apply {
            postScale(1f, -1f) // invert Y axis
            postTranslate(w / 2f, h / 2f) // center the origin
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.withSave {
            concat(matrixCamera)
            concat(matrixCartesian)

//            drawCircle(0f, 0f, 100f, paint)
//            drawLine(0f, 0f, 100f, 100f, paint)
            drawAxis(this)
        }
    }

    private fun drawAxis(canvas: Canvas) {
        val (left, top, right, bottom) = canvas.clipBounds

        canvas.drawLine(left.toFloat(), 0f, right.toFloat(), 0f, paint)
        canvas.drawLine(0f, top.toFloat(), 0f, bottom.toFloat(), paint)

        drawMarksOnAxis(canvas)
    }

    private fun drawMarksOnAxis(canvas: Canvas) {
        val (left, top, right, bottom) = canvas.clipBounds
        val marksScale = 100
        val markHalfH = 15f

        for (i in (left - left % marksScale)..right step marksScale) {
            if (i == 0) continue
            val iF = i.toFloat()
            canvas.drawLine(iF, -markHalfH, iF, markHalfH, paint)
            canvas.drawTextInRightDirection(i.toString(), iF, -markHalfH * 2, paint)
        }

        for (i in (top - top % marksScale)..bottom step marksScale) {
            if (i == 0) continue
            val iF = i.toFloat()
            canvas.drawLine(-markHalfH, iF, markHalfH, iF, paint)
            canvas.drawTextInRightDirection(i.toString(), markHalfH * 2, iF, paint)
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun updateCameraMatrixByTouch() {
        var startPoint = PointF(0f, 0f)
        var endPoint: PointF

        setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startPoint = PointF(event.x, event.y)
                    Log.i("DrawableView", "startPoint: $startPoint")
                }

                MotionEvent.ACTION_MOVE -> {
                    endPoint = PointF(event.x, event.y)
                    val dx = endPoint.x - startPoint.x
                    val dy = endPoint.y - startPoint.y
                    matrixCamera.postTranslate(dx, dy)
                    // update start point for next move if not updated then the camera will apply wrong dx, dy (it would accumulate)
                    startPoint.set(endPoint)
                    invalidate()
                }
            }

            true
        }
    }
}