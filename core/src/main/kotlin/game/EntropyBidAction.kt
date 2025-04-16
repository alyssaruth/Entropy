package game

data class EntropyBidAction(
    override val playerName: String,
    override val cardToReveal: String,
    override val blind: Boolean,
    val amount: Int,
    val suit: Suit,
) : BidAction<EntropyBidAction>() {

    override fun overAchievementThreshold() = amount >= 5

    override fun isPerfect(hands: List<List<String>>, settings: GameSettings): Boolean {
        TODO("Not yet implemented")
    }

    override fun isOverbid(hands: List<List<String>>, settings: GameSettings): Boolean {
        TODO("Not yet implemented")
    }

    override fun higherThan(other: EntropyBidAction): Boolean {
        if (amount > other.amount) {
            return true
        }

        return amount == other.amount && suit > other.suit
    }

    override fun htmlString() =
        "<font color=\"${suit.getColour()}\" face=\"Segoe UI Symbol\">$amount${suit.unicodeStr}</font>"
}
