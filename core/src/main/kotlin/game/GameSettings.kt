package game

data class GameSettings(
    val mode: GameMode,
    val jokerQuantity: Int = 0,
    val jokerValue: Int = 0,
    val includeMoons: Boolean = false,
    val includeStars: Boolean = false,
    val negativeJacks: Boolean = false,
    val cardReveal: Boolean = false,
    val illegalAllowed: Boolean = false
)
