package game

import java.awt.Color
import util.CardsUtil.CLUBS_SYMBOL
import util.CardsUtil.DIAMONDS_SYMBOL
import util.CardsUtil.HEARTS_SYMBOL
import util.CardsUtil.MOONS_SYMBOL
import util.CardsUtil.SPADES_SYMBOL
import util.CardsUtil.STARS_SYMBOL
import util.Registry
import utils.COLOUR_SUIT_GOLD
import utils.COLOUR_SUIT_GREEN
import utils.COLOUR_SUIT_PURPLE
import utils.toHexCode

enum class Suit(
    val twoColour: Color,
    val fourColour: Color,
    val unicodeStr: String,
    val letter: Char,
    @Deprecated("this should die") val legacyCode: Int,
) {
    Clubs(Color.black, COLOUR_SUIT_GREEN, CLUBS_SYMBOL, 'c', 0),
    Diamonds(Color.red, Color.blue, DIAMONDS_SYMBOL, 'd', 1),
    Hearts(Color.red, Color.red, HEARTS_SYMBOL, 'h', 2),
    Moons(COLOUR_SUIT_GOLD, COLOUR_SUIT_PURPLE, MOONS_SYMBOL, 'm', 3),
    Spades(Color.black, Color.black, SPADES_SYMBOL, 's', 4),
    Stars(COLOUR_SUIT_GOLD, COLOUR_SUIT_GOLD, STARS_SYMBOL, 'x', 5);

    fun getDescription(singular: Boolean): String {
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
        val filtered = Suit.filter(includeMoons, includeStars)
        val nextIx = (filtered.indexOf(this) + 1) % filtered.size
        return filtered[nextIx]
    }

    companion object {
        @JvmStatic
        fun filter(includeMoons: Boolean, includeStars: Boolean) =
            Suit.entries.filter {
                (it != Suit.Moons || includeMoons) && (it != Suit.Stars || includeStars)
            }

        @JvmStatic
        fun random(includeMoons: Boolean, includeStars: Boolean) =
            filter(includeMoons, includeStars).random()
    }
}
