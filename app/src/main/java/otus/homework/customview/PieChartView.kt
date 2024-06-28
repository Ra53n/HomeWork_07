package otus.homework.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min
import kotlin.random.Random


class PieChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {

    private var total = 0

    private val categories = mutableMapOf<String, Sector>()

    private val textPaint = Paint().apply {
        textSize = 64f
        color = Color.BLACK
    }

    private val sectorPaint = Paint().apply {
        strokeWidth = 30f
        style = Paint.Style.STROKE
    }

    private val textBoundsRect = Rect()

    private var sectorClickListener: (String) -> Unit = {}

    fun setPayloads(payloads: List<Payload>) {
        payloads.forEach {
            total += it.amount
            if (!categories.containsKey(it.category)) {
                categories[it.category] = Sector(category = it.category)
            }
            val currentSector = categories[it.category]!!
            currentSector.amount += it.amount
            if (currentSector.color == 0) {
                val color = Color.argb(
                    255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)
                )
                currentSector.color = color
            }
        }
        var startAngle = 0f
        var endAngle = 0f

        categories.values.forEach {
            endAngle = 360f * it.amount / total
            it.startAngle = startAngle
            it.endAngle = endAngle
            startAngle += endAngle
            it.color = Color.argb(
                255, Random.nextInt(256), Random.nextInt(256), Random.nextInt(256)
            )
        }
    }

    fun setSectorClickListener(sectorClickListener: (String) -> Unit) {
        this.sectorClickListener = sectorClickListener
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)

        var resultWidth = MIN_SIZE
        var resultHeight = MIN_SIZE

        when (wMode) {
            MeasureSpec.EXACTLY -> {
                resultWidth = wSize
            }

            MeasureSpec.AT_MOST -> {
                resultWidth = min(wSize, MIN_SIZE)
            }
        }

        when (hMode) {
            MeasureSpec.EXACTLY -> {
                resultHeight = hSize
            }

            MeasureSpec.AT_MOST -> {
                resultHeight = min(hSize, MIN_SIZE)
            }
        }

        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelableArrayList(SECTORS_STATE, ArrayList(categories.values))
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        val sectorsArray = bundle.getParcelableArrayList<Sector>(SECTORS_STATE)?.toList()
        sectorsArray?.forEach {
            categories[it.category] = it
        }
        super.onRestoreInstanceState(bundle.getParcelable(SUPER_STATE))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = 300f
        val left = centerX - radius
        val top = centerY - radius
        val right = centerX + radius
        val bottom = centerY + radius

        categories.values.forEach {
            val path = it.getPath()
            sectorPaint.color = it.color
            path.reset()
            path.arcTo(
                left, top, right, bottom, it.startAngle, it.endAngle, true
            )
            canvas.drawPath(path, sectorPaint)
        }

        val totalText = "Total: $total"
        textPaint.getTextBounds(totalText, 0, totalText.length, textBoundsRect)
        canvas.drawText(
            totalText,
            centerX - textBoundsRect.width() / 2,
            centerY + textBoundsRect.height() / 2,
            textPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                categories.entries.forEach {
                    val sectorBounds = RectF()
                    it.value.getPath().computeBounds(sectorBounds, true)
                    if (sectorBounds.contains(x, y)) {
                        sectorClickListener(it.key)
                        return true
                    }
                }
            }
        }
        return super.onTouchEvent(event)
    }

    companion object {
        private const val MIN_SIZE = 700

        private const val SUPER_STATE = "SUPER_STATE"
        private const val SECTORS_STATE = "SECTORS_STATE"
    }
}
