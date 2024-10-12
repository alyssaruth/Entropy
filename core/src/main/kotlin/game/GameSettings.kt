package game

data class GameSettings(
    val mode: GameMode,
    val jokerQuantity: Int,
    val jokerValue: Int,
    val includeMoons: Boolean,
    val includeStars: Boolean,
    val negativeJacks: Boolean,
    val cardReveal: Boolean,
    val illegalAllowed: Boolean
)
