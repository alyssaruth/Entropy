package game

import utils.CoreGlobals

data class VectropyBidAction(
    override val playerName: String,
    override val blind: Boolean,
    val amounts: Map<Suit, Int>,
) : BidAction<VectropyBidAction>() {

    constructor(
        playerName: String,
        blind: Boolean,
        clubs: Int,
        diamonds: Int,
        hearts: Int,
        moons: Int?,
        spades: Int,
        stars: Int?,
    ) : this(playerName, blind, constructMap(clubs, diamonds, hearts, moons, spades, stars))

    fun getTotal() = amounts.values.sum()

    fun getAmount(suit: Suit) = amounts[suit]

    override fun higherThan(other: VectropyBidAction) =
        getTotal() > other.getTotal() &&
            amounts.all { (suit, myAmount) -> myAmount >= other.getAmount(suit)!! }

    override fun overAchievementThreshold() = getTotal() >= 5

    override fun isPerfect(cards: List<String>, settings: GameSettings): Boolean =
        amounts.all { (suit, amount) -> amount == countSuit(suit, cards, settings.jokerValue) }

    override fun isOverbid(cards: List<String>, settings: GameSettings): Boolean =
        amounts.any { (suit, amount) -> amount > countSuit(suit, cards, settings.jokerValue) }

    override fun plainString(): String {
        val suits = amounts.keys.sorted().map(::getAmount)
        return "(${suits.joinToString()})"
    }

    fun incrementSuit(suit: Suit): VectropyBidAction {
        val current = amounts.getValue(suit)
        return copy(amounts = amounts + (suit to current + 1))
    }

    companion object {
        @JvmStatic
        fun fromJson(jsonString: String): VectropyBidAction =
            CoreGlobals.jsonMapper.readValue(jsonString, VectropyBidAction::class.java)
    }
}

private fun constructMap(
    clubs: Int,
    diamonds: Int,
    hearts: Int,
    moons: Int?,
    spades: Int,
    stars: Int?,
): Map<Suit, Int> {
    val map =
        mutableMapOf(
            Suit.Clubs to clubs,
            Suit.Diamonds to diamonds,
            Suit.Hearts to hearts,
            Suit.Spades to spades,
        )

    moons?.let { map[Suit.Moons] = moons }
    stars?.let { map[Suit.Stars] = stars }

    return map.toMap()
}
