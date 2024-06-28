package otus.homework.customview

import android.graphics.Path
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class Sector(
    val category: String,
    var amount: Int = 0,
    var startAngle: Float = 0f,
    var endAngle: Float = 0f,
    var color: Int = 0,
) : Parcelable {
    @IgnoredOnParcel
    private var path: Path? = null

    fun getPath(): Path {
        if (path == null) {
            path = Path()
        }
        return path!!
    }
}
