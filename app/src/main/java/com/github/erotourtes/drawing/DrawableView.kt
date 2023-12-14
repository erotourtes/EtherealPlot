package com.github.erotourtes.drawing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.View.OnTouchListener
import androidx.core.graphics.*
import kotlin.math.absoluteValue


const val PIXELS_PER_UNIT = 100

fun Canvas.drawTextInRightDirection(text: String, x: Float, y: Float, paint: Paint) {
    withSave {
        val textBoundaries = Rect().apply { paint.getTextBounds(text, 0, text.length, this) }
        translate(x - textBoundaries.width() / 2, y - textBoundaries.height() / 2)
        scale(1f, -1f)
        drawText(text, 0f, 0f, paint)
    }
}

@SuppressLint("ClickableViewAccessibility")
class DrawableView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val matrixCamera = Matrix()
    private val matrixCartesian = Matrix()

    private var scaleFactor = 1f

    private val paint = Paint().apply {
        isAntiAlias = true
        color = Color.RED
        style = Paint.Style.FILL
        strokeWidth = 10f
        textSize = 50f
    }

    private val scaleGestureDetector = initScaleGestureDetector(context)

    init {
        initScaleGestureDetector(context)
    }

    init {
        val listeners = listOf(translateCameraListener(), scaleCameraListener())

        setOnTouchListener { _, event ->
            listeners.forEach { it.onTouch(this, event) }
            true
        }
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
            scale(scaleFactor, scaleFactor)

//            drawCircle(0f, 0f, 100f, paint)
//            drawLine(0f, 0f, 100f, 100f, paint)
            drawGrid(this)
            drawAxis(this)
        }
    }

    private fun drawGrid(canvas: Canvas) {
        val (left, top, right, bottom) = canvas.clipBounds
        val baseGridScale = 1 * PIXELS_PER_UNIT

        for (i in (left - left % baseGridScale)..right step baseGridScale) {
            val isMain = i.absoluteValue % (baseGridScale * 5) == 0
            paint.forGrid(isMain) {
                canvas.drawLine(i.toFloat(), top.toFloat(), i.toFloat(), bottom.toFloat(), this)
                if (isMain) writeTextXAxis(canvas, i)
            }
        }

        for (i in (top - top % baseGridScale)..bottom step baseGridScale) {
            val isMain = i.absoluteValue % (baseGridScale * 5) == 0
            paint.forGrid(isMain) {
                canvas.drawLine(left.toFloat(), i.toFloat(), right.toFloat(), i.toFloat(), this)
                if (isMain) writeTextYAxis(canvas, i)
            }
        }
    }

    private fun writeTextXAxis(canvas: Canvas, curX: Int) {
        val text = (curX / PIXELS_PER_UNIT).toString()
        val textBound = Rect().apply { paint.getTextBounds(text, 0, text.length, this) }
        val textX = curX.toFloat() - textBound.width()
        val textY = -textBound.height().toFloat()
        canvas.drawTextInRightDirection(text, textX, textY, paint)
    }

    private fun writeTextYAxis(canvas: Canvas, curY: Int) {
        if (curY == 0) return
        val text = (curY / PIXELS_PER_UNIT).toString()
        val textBound = Rect().apply { paint.getTextBounds(text, 0, text.length, this) }
        val textX = -textBound.width().toFloat()
        val textY = curY.toFloat() + textBound.height()
        canvas.drawTextInRightDirection(text, textX, textY, paint)
    }

    private fun Paint.forGrid(isMain: Boolean, block: Paint.() -> Unit) {
        val oldStrokeWidth = this.strokeWidth
        val oldColor = this.color
        this.strokeWidth /= if (isMain) 2f else 4f
        block()
        this.strokeWidth = oldStrokeWidth
        this.color = oldColor
    }

    private fun drawAxis(canvas: Canvas) {
        val (left, top, right, bottom) = canvas.clipBounds

        canvas.drawLine(left.toFloat(), 0f, right.toFloat(), 0f, paint)
        canvas.drawLine(0f, top.toFloat(), 0f, bottom.toFloat(), paint)
    }

    private fun drawMarksOnAxis(canvas: Canvas) {
        val (left, top, right, bottom) = canvas.clipBounds
        val marksScale = 1 * PIXELS_PER_UNIT
        val markHalfH = 15f
        val textOffsetMultiplier = 3

        for (i in (left - left % marksScale)..right step marksScale) {
            if (i == 0) continue
            val iF = i.toFloat()
            canvas.drawLine(iF, -markHalfH, iF, markHalfH, paint)
            canvas.drawTextInRightDirection(
                (i / PIXELS_PER_UNIT).toString(), iF, -markHalfH * textOffsetMultiplier, paint
            )
        }

        for (i in (top - top % marksScale)..bottom step marksScale) {
            if (i == 0) continue
            val iF = i.toFloat()
            canvas.drawLine(-markHalfH, iF, markHalfH, iF, paint)
            canvas.drawTextInRightDirection(
                (i / PIXELS_PER_UNIT).toString(), markHalfH * textOffsetMultiplier, iF, paint
            )
        }
    }

    private fun translateCameraListener(): OnTouchListener {
        var startPoint = PointF(0f, 0f)
        var endPoint: PointF

        return OnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> startPoint = PointF(event.x, event.y)
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

    private fun scaleCameraListener(): OnTouchListener = OnTouchListener { _, event ->
        scaleGestureDetector.onTouchEvent(event)
        true
    }

    private fun initScaleGestureDetector(context: Context): ScaleGestureDetector =
        ScaleGestureDetector(context, object : SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                val scaleFactor = detector.scaleFactor
//                matrixCamera.postScale(scaleFactor, scaleFactor, detector.focusX, detector.focusY)
                updateScaleFactor(scaleFactor)
                invalidate()
                return true
            }
        })

    private fun updateScaleFactor(scaleFactor: Float) {
        this.scaleFactor *= scaleFactor
        paint.textSize /= scaleFactor
        paint.strokeWidth /= scaleFactor
        invalidate()
    }
}