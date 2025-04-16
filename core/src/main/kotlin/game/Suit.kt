package game

import util.CardsUtil.CLUBS_SYMBOL
import util.CardsUtil.DIAMONDS_SYMBOL
import util.CardsUtil.HEARTS_SYMBOL
import util.CardsUtil.MOONS_SYMBOL
import util.CardsUtil.SPADES_SYMBOL
import util.CardsUtil.STARS_SYMBOL
import util.Registry

enum class Suit(
    val twoColour: String,
    val fourColour: String,
    val unicodeStr: String,
    val letter: Char,
) {
    Clubs("black", "green", CLUBS_SYMBOL, 'c'),
    Diamonds("red", "blue", DIAMONDS_SYMBOL, 'd'),
    Hearts("red", "red", HEARTS_SYMBOL, 'h'),
    Moons("#E6B800", "purple", MOONS_SYMBOL, 'm'),
    Spades("black", "black", SPADES_SYMBOL, 's'),
    Stars("#E6B800", "#E6B800", STARS_SYMBOL, 'x');

    fun getDescription(singular: Boolean): String {
        val lower = name.lowercase()
        return if (singular) lower.dropLast(1) else lower
    }

    fun getColour(): String {
        // TODO - Should port preferences to the settings abstraction
        val numberOfColoursStr =
            Registry.prefs[Registry.PREFERENCES_STRING_NUMBER_OF_COLOURS, Registry.TWO_COLOURS]
        return if (numberOfColoursStr == Registry.FOUR_COLOURS) fourColour else twoColour
    }
}
