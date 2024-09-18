import kotlin.math.log2
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

fun Long.formatAsFileSize(): String = log2(coerceAtLeast(1).toDouble()).toInt().div(10).let {
    val precision = when (it) {
        0 -> 0; else -> 1;
    }
    val prefix = arrayOf("", "K", "M", "G", "T", "P", "E", "Z", "Y")
    String.format("%.${precision}f ${prefix[it]}B", toDouble() / 2.0.pow(it * 10.0))
}