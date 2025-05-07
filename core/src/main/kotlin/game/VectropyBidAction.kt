package game

data class VectropyBidAction(
    override val playerName: String,
    override val cardToReveal: String,
    override val blind: Boolean,
    val clubs: Int,
    val diamonds: Int,
    val hearts: Int,
    val moons: Int?,
    val spades: Int,
    val stars: Int?,
) : BidAction<VectropyBidAction>() {

    fun getTotal() = clubs + diamonds + hearts + spades + (moons ?: 0) + (stars ?: 0)

    override fun higherThan(other: VectropyBidAction) =
        getTotal() > other.getTotal() &&
            isAtLeast(other, VectropyBidAction::clubs) &&
            isAtLeast(other, VectropyBidAction::diamonds) &&
            isAtLeast(other, VectropyBidAction::hearts) &&
            isAtLeast(other, VectropyBidAction::moons) &&
            isAtLeast(other, VectropyBidAction::spades) &&
            isAtLeast(other, VectropyBidAction::stars)

    private fun isAtLeast(other: VectropyBidAction, suitSelector: (VectropyBidAction) -> Int?) =
        suitSelector(this)?.let { it >= suitSelector(other)!! } ?: true

    override fun overAchievementThreshold() = getTotal() >= 5

    override fun isPerfect(cards: List<String>, settings: GameSettings): Boolean =
        evaluateAllSuits(cards, settings, ::isPerfect).all { it }

    private fun isPerfect(cards: List<String>, amount: Int?, suit: Suit, settings: GameSettings) =
        amount?.let { amount == countSuit(suit, cards, settings.jokerValue) } ?: true

    override fun isOverbid(cards: List<String>, settings: GameSettings): Boolean =
        evaluateAllSuits(cards, settings, ::isOverbid).none { it }

    private fun isOverbid(cards: List<String>, amount: Int?, suit: Suit, settings: GameSettings) =
        amount?.let { amount > countSuit(suit, cards, settings.jokerValue) } ?: false

    private fun evaluateAllSuits(
        cards: List<String>,
        settings: GameSettings,
        condition: (List<String>, Int?, Suit, GameSettings) -> Boolean,
    ) =
        listOf(
            condition(cards, clubs, Suit.Clubs, settings),
            condition(cards, diamonds, Suit.Diamonds, settings),
            condition(cards, hearts, Suit.Hearts, settings),
            condition(cards, moons, Suit.Moons, settings),
            condition(cards, spades, Suit.Spades, settings),
            condition(cards, stars, Suit.Stars, settings),
        )

    override fun plainString(): String {
        fun optional(amount: Int?) = if (amount == null) " " else " $amount,"

        return "($clubs, $diamonds, $hearts,${optional(moons)} $spades,${optional(stars)})"
    }
}
