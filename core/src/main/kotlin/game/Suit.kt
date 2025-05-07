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
) {
    Clubs(Color.black, COLOUR_SUIT_GREEN, CLUBS_SYMBOL, 'c'),
    Diamonds(Color.red, Color.blue, DIAMONDS_SYMBOL, 'd'),
    Hearts(Color.red, Color.red, HEARTS_SYMBOL, 'h'),
    Moons(COLOUR_SUIT_GOLD, COLOUR_SUIT_PURPLE, MOONS_SYMBOL, 'm'),
    Spades(Color.black, Color.black, SPADES_SYMBOL, 's'),
    Stars(COLOUR_SUIT_GOLD, COLOUR_SUIT_GOLD, STARS_SYMBOL, 'x');

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
        val nextIx = (Suit.entries.indexOf(this) + 1) % Suit.entries.size
        val next = Suit.entries[nextIx]

        return if ((next == Moons && !includeMoons) || next == Stars && !includeStars) {
            next.next(includeMoons, includeStars)
        } else {
            next
        }
    }
}
