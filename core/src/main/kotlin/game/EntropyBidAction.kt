package game

data class EntropyBidAction(
    override val playerName: String,
    override val blind: Boolean,
    val amount: Int,
    val suit: Suit,
) : BidAction<EntropyBidAction>() {

    override fun overAchievementThreshold() = amount >= 5

    override fun isPerfect(cards: List<String>, settings: GameSettings): Boolean {
        val perfectAmount = perfectBidAmount(cards, settings.jokerValue)
        val perfectSuit = perfectBidSuit(cards, settings.jokerValue, settings.includeStars)

        return amount == perfectAmount && suit == perfectSuit
    }

    override fun isOverbid(cards: List<String>, settings: GameSettings) =
        amount > countSuit(suit, cards, settings.jokerValue)

    override fun higherThan(other: EntropyBidAction): Boolean {
        if (amount > other.amount) {
            return true
        }

        return amount == other.amount && suit > other.suit
    }

    override fun plainString() = "$amount ${suit.getDescription(amount == 1)}"

    override fun htmlString() =
        "<font color=\"${suit.getColourHex()}\" face=\"Segoe UI Symbol\">$amount${suit.unicodeStr}</font>"
}
