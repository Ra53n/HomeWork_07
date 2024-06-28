package otus.homework.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.min

class ChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : View(context, attrs) {
    private var points = mutableListOf<ChartPoint>()
    private var maxAmount = 0
    private var minAmount = 0

    private val path = Path()
    private val paint = Paint().apply {
        color = Color.RED
        strokeWidth = 8f
        style = Paint.Style.STROKE
        pathEffect = CornerPathEffect(10f)
    }

    fun setPayloads(payloads: List<Payload>) {
        val pointsTemp = mutableMapOf<Long, Int>()

        payloads.forEach {
            val day =
                LocalDateTime.ofInstant(Instant.ofEpochSecond(it.time), ZoneId.systemDefault())
                    .toLocalDate().toEpochDay()
            if (!pointsTemp.containsKey(day)) {
                pointsTemp[day] = 0
            }
            pointsTemp[day] = it.amount + pointsTemp[day]!!
        }

        points = pointsTemp.toSortedMap().map { ChartPoint(time = it.key, price = it.value) }
            .toMutableList()

        minAmount = pointsTemp.values.min()
        maxAmount = pointsTemp.values.max()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val wMode = MeasureSpec.getMode(widthMeasureSpec)
        val wSize = MeasureSpec.getSize(widthMeasureSpec)
        val hMode = MeasureSpec.getMode(heightMeasureSpec)
        val hSize = MeasureSpec.getSize(heightMeasureSpec)

        var resultWidth = MIN_WIDTH
        var resultHeight = MIN_HEIGHT

        when (wMode) {
            MeasureSpec.EXACTLY -> {
                resultWidth = wSize
            }

            MeasureSpec.AT_MOST -> {
                resultWidth = min(wSize, MIN_WIDTH)
            }
        }

        when (hMode) {
            MeasureSpec.EXACTLY -> {
                resultHeight = hSize
            }

            MeasureSpec.AT_MOST -> {
                resultHeight = min(hSize, MIN_HEIGHT)
            }
        }

        setMeasuredDimension(resultWidth, resultHeight)
    }

    override fun onSaveInstanceState(): Parcelable {
        return Bundle().apply {
            putParcelableArrayList(POINTS_STATE, ArrayList(points))
            putParcelable(SUPER_STATE, super.onSaveInstanceState())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val bundle = state as Bundle
        val pointsArray = bundle.getParcelableArrayList<ChartPoint>(POINTS_STATE)?.toMutableList()
        pointsArray?.let {
            points = pointsArray
        }
        super.onRestoreInstanceState(bundle.getParcelable(SUPER_STATE))
    }

    override fun onDraw(canvas: Canvas) {
        val wStep = width.toFloat() / points.size
        val hStep = height.toFloat() / (maxAmount + MAX_VALUE_OFFSET).toFloat()

        path.reset()
        path.moveTo(0f, height.toFloat())
        var x = wStep
        var y = 0f

        points.forEach {
            y = it.price.toFloat() * hStep
            path.lineTo(x, y)
            x += wStep
        }

        canvas.drawPath(path, paint)
    }

    companion object {
        private const val MIN_WIDTH = 600
        private const val MIN_HEIGHT = 400
        private const val MAX_VALUE_OFFSET = 1000

        private const val SUPER_STATE = "SUPER_STATE"
        private const val POINTS_STATE = "POINTS_STATE"
    }
}
