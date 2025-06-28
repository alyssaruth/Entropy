package game

import java.awt.Color
import util.Registry
import utils.COLOUR_SUIT_GOLD
import utils.COLOUR_SUIT_GREEN
import utils.COLOUR_SUIT_PURPLE
import utils.toHexCode

const val CLUBS_SYMBOL = "\u2663"
const val DIAMONDS_SYMBOL = "\u2666"
const val HEARTS_SYMBOL = "\u2665"
const val MOONS_SYMBOL = "\uD83C\uDF19"
const val SPADES_SYMBOL = "\u2660"
const val STARS_SYMBOL = "\u2605"

enum class Suit(
    val twoColour: Color,
    val fourColour: Color,
    val unicodeStr: String,
    val letter: Char,
) {
    Clubs(Color.black, COLOUR_SUIT_GREEN, CLUBS_SYMBOL, 'c'),
    Diamonds(Color.red, Color.blue, DIAMONDS_SYMBOL, 'd'),
    Hearts(Color.red, Color.red, HEARTS_SYMBOL, 'h'),
    Moons(COLOUR_SUIT_GOLD, COLOUR_SUIT_PURPLE, MOONS_SYMBOL, 'm'),
    Spades(Color.black, Color.black, SPADES_SYMBOL, 's'),
    Stars(COLOUR_SUIT_GOLD, COLOUR_SUIT_GOLD, STARS_SYMBOL, 'x');

    fun getDescription(amount: Int): String {
        val singular = amount == 1
        val lower = name.lowercase()
        return if (singular) lower.dropLast(1) else lower
    }

    fun getColour(): Color {
        val numberOfColoursStr =
            Registry.prefs[Registry.PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS]
        return if (numberOfColoursStr == Registry.FOUR_COLOURS) fourColour else twoColour
    }

    fun getColourHex() = getColour().toHexCode()

    fun lessThan(other: Suit) = this < other

    fun next(includeMoons: Boolean, includeStars: Boolean): Suit {
        val filtered = filter(includeMoons, includeStars)
        val nextIx = (filtered.indexOf(this) + 1) % filtered.size
        return filtered[nextIx]
    }

    companion object {
        @JvmStatic
        fun filter(includeMoons: Boolean, includeStars: Boolean) =
            Suit.entries.filter { (it != Moons || includeMoons) && (it != Stars || includeStars) }

        @JvmStatic
        fun random(includeMoons: Boolean, includeStars: Boolean) =
            filter(includeMoons, includeStars).random()
    }
}
