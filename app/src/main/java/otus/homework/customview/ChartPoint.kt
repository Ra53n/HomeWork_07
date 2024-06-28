package otus.homework.customview

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChartPoint(
    val time: Long,
    var price: Int = 0,
) : Parcelable
