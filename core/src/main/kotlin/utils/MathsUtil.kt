import kotlin.math.log
import kotlin.math.pow

fun getPercentage(count: Number, total: Number, digits: Int = 1) =
    getPercentage(count.toDouble(), total.toDouble(), digits)

fun getPercentage(count: Double, total: Double, digits: Int = 1): Double {
    return if (count == 0.0) {
        0.0
    } else {
        round(100 * count / total, digits)
    }
}

private fun round(number: Double, decimalPlaces: Int): Double {
    val powerOfTen = 10.0.pow(decimalPlaces.toDouble())

    val rounded = Math.round(powerOfTen * number)

    return rounded / powerOfTen
}

fun Long.formatAsFileSize(): String {
    if (this == 0L) return "0 B"

    val magnitudeIndex = log(toDouble(), 1024.0).toInt()
    val precision = if (magnitudeIndex == 0) 0 else 1
    val units = listOf("B", "KB", "MB", "GB", "TB")

    return String.format(
        "%.${precision}f ${units[magnitudeIndex]}",
        toDouble() / 1024.0.pow(magnitudeIndex),
    )
}
