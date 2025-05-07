package utils

import java.awt.Color

val COLOUR_SUIT_PURPLE: Color = Color.getHSBColor(7f / 9, 1f, 1f)
val COLOUR_SUIT_GOLD: Color = Color.getHSBColor(5f / 36, 1f, 0.9.toFloat())
val COLOUR_SUIT_GREEN: Color = Color.getHSBColor(1f / 3, 1f, 0.5.toFloat())

fun Color.toHexCode() = String.format("#%08X", (rgb shl 8) or alpha)
