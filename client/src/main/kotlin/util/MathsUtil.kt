package util

import kotlin.math.pow

object MathsUtil {
    fun round(number: Double, decimalPlaces: Int): Double {
        val powerOfTen = 10.0.pow(decimalPlaces.toDouble())

        val rounded = Math.round(powerOfTen * number)

        return rounded / powerOfTen
    }

    fun getPercentage(count: Number, total: Number, digits: Int = 1) =
        getPercentage(count.toDouble(), total.toDouble(), digits)

    fun getPercentage(count: Double, total: Double, digits: Int = 1): Double {
        return if (count == 0.0) {
            0.0
        } else round(100 * count / total, digits)
    }
}
