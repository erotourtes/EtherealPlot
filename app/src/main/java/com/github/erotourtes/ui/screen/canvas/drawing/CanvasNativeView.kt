package com.github.erotourtes.ui.screen.canvas.drawing

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener
import android.view.View
import android.view.View.OnTouchListener
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.*
import com.github.erotourtes.model.PlotUIState
import com.github.erotourtes.utils.MathParser
import com.github.erotourtes.utils.drawTextInRightDirection
import com.github.erotourtes.utils.withColor
import kotlin.math.absoluteValue

const val PIXELS_PER_UNIT = 100

data class Colors(
    val axes: Int,
    val bg: Int,
    val text: Int,
)

@SuppressLint("ClickableViewAccessibility")
class CanvasViewNativeView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val matrixCamera = Matrix()
    private val matrixCartesian = Matrix()

    private var scaleFactor = 1f

    private var curStepMultiplier = 1f
    private var prevScaleFactor = 1f

    private val mainEvery = 5 // every 5th line is main and has a text

    private lateinit var canvas: Canvas

    private lateinit var colors: Colors
    private var fns: List<PlotUIState> = emptyList()
    private val cachedParsers: MutableMap<String, MathParser> = HashMap()

    private var onPlotNotValid: ((PlotUIState) -> Unit)? = null

    // The default color of paint is Colors::axes
    private val paint = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.FILL
        strokeWidth = 10f
        textSize = 50f
    }

    private val scaleGestureDetector = initScaleGestureDetector(context)

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
        this.canvas = canvas

        canvas.withSave {
            concat(matrixCamera)
            concat(matrixCartesian)
            scale(scaleFactor, scaleFactor)

            drawBG()
            drawGrid()
            drawAxis()
            drawFns()
            drawAxesText()
        }
    }

    private fun drawBG() {
        paint.withColor(colors.bg) {
            canvas.drawRect(canvas.clipBounds, paint)
        }
    }

    private fun drawFns() {
        // TODO: optimise using coroutines
        val invalidatedFns = mutableListOf<PlotUIState>()
        fns.filter { it.isValid && it.isVisible }.forEach {
            val parser = cachedParsers.getOrPut(it.function) {
                MathParser(it.function)
            }
            paint.withColor(it.color.toArgb()) {
                val isSuccess =
                    drawFn(parser)/* Can't call onPlotNotValid directly because it will throw an exception */
                if (!isSuccess) invalidatedFns.add(it)
            }
        }

        invalidatedFns.forEach { onPlotNotValid?.invoke(it) }
    }

    /**
     * Draws a function on the canvas.
     * @param fn the function to draw
     * @return true if the function was drawn successfully, false otherwise
     * */
    private fun drawFn(fn: MathParser): Boolean {
        var (left, top, right, bottom) = canvas.clipBounds
        top = bottom.also { bottom = top }
        if (top < bottom) throw IllegalArgumentException("top < bottom")

        val step = 1f * curStepMultiplier
        var xCur = left.toDouble().coerceAtLeast(fn.boundaries.first * PIXELS_PER_UNIT)
        val xEnd = right.toDouble().coerceAtMost(fn.boundaries.second * PIXELS_PER_UNIT)

        var yCur = fn.evalInPixels(xCur) ?: return false

        while (xCur < xEnd) {
            val xNext = xCur + step

            val yNext = fn.evalInPixels(xNext) ?: return false
            val yNextNext = fn.evalInPixels(xNext + step) ?: return false

            // check for asymptote
            // TODO: optimise and make it work for all functions
            // Currently it has a bug with tan(x)
            val dy = yNext - yCur
            val tan = dy / step
            val isAsymptote = tan.absoluteValue > 1000

            val tanNext = (yNextNext - yNext) / (step)

            if (isAsymptote && tan * tanNext < 0) {
                xCur = xNext
                yCur = yNext
                continue
            } else if (isAsymptote) {
                val isToDown = tan > tanNext;
                val yAsymptote = if (isToDown) bottom else top

//                Log.i(
//                    "CanvasViewNativeView",
//                    "$tan $tanNext Asymptote at x = ${xCur / PIXELS_PER_UNIT}, y = ${yCur / PIXELS_PER_UNIT}"
//                )

                canvas.drawLine(
                    xCur.toFloat(), yCur.toFloat(), xCur.toFloat(), yAsymptote.toFloat(), paint
                )

                xCur = xNext
                yCur = yNext

                continue
            }

            canvas.drawLine(
                xCur.toFloat(), yCur.toFloat(), xNext.toFloat(), yNext.toFloat(), paint
            )

            xCur = xNext
            yCur = yNext
        }

        return true
    }


    private fun drawAxesText() {
        recalculateGridStep()
        val (left, top, right, bottom) = canvas.clipBounds
        val gridStep = 1 * PIXELS_PER_UNIT
        val gridScale = gridStep * curStepMultiplier

        withYGridStep(top, bottom, gridScale, mainEvery) { y, isMain ->
            paint.forGrid(isMain) {
                if (isMain) writeTextYAxis(y, left, right)
            }
        }

        // Needs to draw separately because otherwise y grid lines are over the text
        withXGridStep(left, right, gridScale, mainEvery) { x, isMain ->
            if (isMain) writeTextXAxis(x, bottom, top) // revere top and bottom because of inverted Y axis
        }
    }

    private fun drawGrid() {
        recalculateGridStep()
        val (left, top, right, bottom) = canvas.clipBounds
        val gridStep = 1 * PIXELS_PER_UNIT
        val gridScale = gridStep * curStepMultiplier

        withXGridStep(left, right, gridScale, mainEvery) { x, isMain ->
            paint.forGrid(isMain) {
                canvas.drawLine(x, top.toFloat(), x, bottom.toFloat(), this)
            }
        }

        withYGridStep(top, bottom, gridScale, mainEvery) { y, isMain ->
            paint.forGrid(isMain) {
                canvas.drawLine(left.toFloat(), y, right.toFloat(), y, this)
            }
        }
    }

    private inline fun withXGridStep(
        left: Int, right: Int, gridScale: Float, mainEvery: Int, block: (Float, Boolean) -> Unit
    ) {
        var x = left - left % gridScale
        while (x < right) {
            val isMain = x.absoluteValue % (gridScale * mainEvery) == 0f
            block(x, isMain)
            x += gridScale
        }
    }

    private inline fun withYGridStep(
        top: Int, bottom: Int, gridScale: Float, mainEvery: Int, block: (Float, Boolean) -> Unit
    ) {
        var y = top - top % gridScale
        while (y < bottom) {
            val isMain = y.absoluteValue % (gridScale * mainEvery) == 0f
            block(y, isMain)
            y += gridScale
        }
    }

    private fun recalculateGridStep() {
        val gridScale = 1 * PIXELS_PER_UNIT * curStepMultiplier

        val prevOrigWidth = gridScale * prevScaleFactor
        val curOrigWidth = gridScale * scaleFactor
        val isZoomedIn = prevScaleFactor < scaleFactor
        val largerTimes = 2

        if (isZoomedIn && prevOrigWidth * largerTimes < curOrigWidth) {
            curStepMultiplier /= largerTimes
            prevScaleFactor = scaleFactor
        }

        if (!isZoomedIn && prevOrigWidth / largerTimes > curOrigWidth) {
            curStepMultiplier *= largerTimes
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

    private fun writeTextXAxis(curX: Float, cameraTop: Int, cameraBottom: Int) {
        if (cameraTop < cameraBottom) throw IllegalArgumentException("cameraTop < cameraBottom")

        val text = formatFloatTextForAxis((curX / PIXELS_PER_UNIT).toString())
        val textBound = Rect().apply { paint.getTextBounds(text, 0, text.length, this) }
        val textX = curX - textBound.width()
        val textTopCorner = -textBound.height() * 0.1f
        val textBottomCorner = textTopCorner - textBound.height().toFloat()

        var textYOffset = 0f
        if (textTopCorner > cameraTop) textYOffset = -(textTopCorner - cameraTop) - textTopCorner * 1.5f
        if (textBottomCorner < cameraBottom) textYOffset = cameraBottom - textBottomCorner

        if (curX == 0f) textYOffset = 0f // don't move 0 to the left (it is already in the center)

        paint.withColor(colors.text) {
            canvas.drawTextInRightDirection(text, textX, textTopCorner - textBound.height() / 2f + textYOffset, paint)
        }
    }

    private fun writeTextYAxis(curY: Float, cameraLeft: Int, cameraRight: Int) {
        if (curY == 0f) return
        val text = formatFloatTextForAxis((curY / PIXELS_PER_UNIT).toString())
        val textBound = Rect().apply { paint.getTextBounds(text, 0, text.length, this) }
        val textY = curY + textBound.height()
        val leftTextCorner = -textBound.width().toFloat()
        val rightTextCorner = 0f

        var textXOffset = 0f
        if (leftTextCorner < cameraLeft) textXOffset = cameraLeft - leftTextCorner * 1.5f
        if (rightTextCorner > cameraRight) textXOffset = -(rightTextCorner - cameraRight)

        paint.withColor(colors.text) {
            canvas.drawTextInRightDirection(text, leftTextCorner + textXOffset, textY, paint)
        }
    }

    private fun Paint.forGrid(isMain: Boolean, block: Paint.() -> Unit) {
        val oldStrokeWidth = this.strokeWidth
        val oldColor = this.color
        this.strokeWidth /= if (isMain) 2f else 4f
        block()
        this.strokeWidth = oldStrokeWidth
        this.color = oldColor
    }

    private fun drawAxis() {
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
                    // update start point for next move if not updated then the camera will apply wrong dx, dy (it would accumulate)
                    startPoint.set(endPoint)

                    moveCamera(dx, dy)
                }
            }

            true
        }
    }

    private fun moveCamera(dx: Float, dy: Float) {
        matrixCamera.postTranslate(dx, dy)
        invalidate()
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
                return true
            }
        })

    private fun updateScaleFactor(scaleFactor: Float) {
        val scaled = this.scaleFactor * scaleFactor
        if (scaled > 30f) return // scale when text becomes unreadable, though it is scaled down

        this.scaleFactor = scaled
        paint.textSize /= scaleFactor
        paint.strokeWidth /= scaleFactor
        invalidate()
    }

    fun set(c: Colors, newFns: List<PlotUIState>, onPlotNotValid: (PlotUIState) -> Unit) {
        colors = c
        paint.color = c.axes

        fns = newFns

        this.onPlotNotValid = onPlotNotValid

        invalidate()
    }

    private fun MathParser.evalInPixels(x: Double): Double? =
        setVariable("x", x / PIXELS_PER_UNIT).evalOrNull { it * PIXELS_PER_UNIT }
}
