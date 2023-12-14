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
import com.github.erotourtes.utils.MathParser
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

    private var curStepMultiplier = 1f
    private var prevScaleFactor = 1f

    private var fns = listOf(
        MathParser("x"),
        MathParser("x^2"),
        MathParser("x^3"),
    )

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

            drawFns(this)
            drawGrid(this)
            drawAxis(this)
        }
    }

    private fun drawFns(canvas: Canvas) {
        fns.forEach { drawFn(canvas, it) }
    }

    private fun drawFn(canvas: Canvas, fn: MathParser) {
        val (left, _, right, _) = canvas.clipBounds

        val step = 1f * curStepMultiplier
        var xCur = left.toDouble()
        val xEnd = right.toDouble()

        while (xCur < xEnd) {
            val xNext = xCur + step

            fn.setVariable("x", xCur / PIXELS_PER_UNIT)
            val yCur = fn.eval()
            fn.setVariable("x", xNext / PIXELS_PER_UNIT)
            val yNext = fn.eval()

            canvas.drawLine(
                xCur.toFloat(),
                yCur.toFloat(),
                xNext.toFloat(),
                yNext.toFloat(),
                paint
            )

            xCur = xNext
        }
    }

    private fun drawGrid(canvas: Canvas) {
        recalculateGridStep()

        val (left, top, right, bottom) = canvas.clipBounds
        val gridStep = 1 * PIXELS_PER_UNIT

        val gridScale = gridStep * curStepMultiplier

        var x = left - left % gridScale + gridScale
        while (x < right) {
            val isMain = x.absoluteValue % (gridScale * 5) == 0f
            paint.forGrid(isMain) {
                canvas.drawLine(x, top.toFloat(), x, bottom.toFloat(), this)
                if (isMain) writeTextXAxis(canvas, x)
            }
            x += gridScale
        }

        var y = top - top % gridScale + gridScale
        while (y < bottom) {
            val isMain = y.absoluteValue % (gridScale * 5) == 0f
            paint.forGrid(isMain) {
                canvas.drawLine(left.toFloat(), y, right.toFloat(), y, this)
                if (isMain) writeTextYAxis(canvas, y)
            }
            y += gridScale
        }
    }

    private fun recalculateGridStep() {
        val gridScale = 1 * PIXELS_PER_UNIT * curStepMultiplier

        val prevOrigWidth = gridScale * prevScaleFactor
        val curOrigWidth = gridScale * scaleFactor
        val isZoomedIn = prevScaleFactor < scaleFactor

        if (isZoomedIn && prevOrigWidth * 2 < curOrigWidth) {
            curStepMultiplier /= 2
            prevScaleFactor = scaleFactor
        }

        if (!isZoomedIn && prevOrigWidth / 2 > curOrigWidth) {
            curStepMultiplier *= 2
            prevScaleFactor = scaleFactor
        }
    }

    private fun formatFloatTextForAxis(text: String): String {
        // if is integer then remove .0
        val isInteger = text.endsWith(".0")
        if (isInteger) return text.substring(0, text.length - 2)

        // if is float with 2 digits leave it
        val digitsAfterDot = text.substringAfter(".").length
        if (digitsAfterDot == 2) return text

        // if is float with more than 2 digits write it in scientific notation
        return text.toDouble().toBigDecimal().toEngineeringString()
    }

    private fun writeTextXAxis(canvas: Canvas, curX: Float) {
        val text = formatFloatTextForAxis((curX / PIXELS_PER_UNIT).toString())
        val textBound = Rect().apply { paint.getTextBounds(text, 0, text.length, this) }
        val textX = curX - textBound.width()
        val textY = -textBound.height().toFloat()
        canvas.drawTextInRightDirection(text, textX, textY, paint)
    }

    private fun writeTextYAxis(canvas: Canvas, curY: Float) {
        if (curY == 0f) return
        val text = formatFloatTextForAxis((curY / PIXELS_PER_UNIT).toString())
        val textBound = Rect().apply { paint.getTextBounds(text, 0, text.length, this) }
        val textX = -textBound.width().toFloat()
        val textY = curY + textBound.height()
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