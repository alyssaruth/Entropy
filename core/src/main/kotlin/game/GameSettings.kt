package game

import java.util.prefs.Preferences
import util.Registry.SHARED_BOOLEAN_CARD_REVEAL
import util.Registry.SHARED_BOOLEAN_INCLUDE_MOONS
import util.Registry.SHARED_BOOLEAN_INCLUDE_STARS
import util.Registry.SHARED_BOOLEAN_NEGATIVE_JACKS
import util.Registry.SHARED_INT_JOKER_QUANTITY
import util.Registry.SHARED_INT_JOKER_VALUE

data class GameSettings(
    val mode: GameMode,
    val jokerQuantity: Int = 0,
    val jokerValue: Int = 0,
    val includeMoons: Boolean = false,
    val includeStars: Boolean = false,
    val negativeJacks: Boolean = false,
    val cardReveal: Boolean = false,
    val illegalAllowed: Boolean = false,
) {
    companion object {
        @JvmStatic
        fun fromRegistry(node: Preferences, mode: GameMode) =
            GameSettings(
                mode,
                jokerQuantity = node.getInt(SHARED_INT_JOKER_QUANTITY, 2),
                jokerValue = node.getInt(SHARED_INT_JOKER_VALUE, 2),
                includeMoons = node.getBoolean(SHARED_BOOLEAN_INCLUDE_MOONS, false),
                includeStars = node.getBoolean(SHARED_BOOLEAN_INCLUDE_STARS, false),
                negativeJacks = node.getBoolean(SHARED_BOOLEAN_NEGATIVE_JACKS, false),
                cardReveal = node.getBoolean(SHARED_BOOLEAN_CARD_REVEAL, false),
                illegalAllowed = true,
            )
    }
}
