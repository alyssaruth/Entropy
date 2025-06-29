package strategy

import game.Suit
import game.countSuit
import `object`.EntropyBid

data class MarkStrategySuitWrapper(
    val applicableSuits: List<Suit>,
    val bestSuit: Suit,
    val worstSuit: Suit,
    val suitsPossibleToMinBid: List<Suit>,
) {
    fun getRandomMiddleSuit() =
        applicableSuits.filterNot { it == bestSuit || it == worstSuit }.random()

    fun randomSuitPossibleToMinBid() = suitsPossibleToMinBid.random()

    fun randomSuitNot(suitToExclude: Suit) =
        applicableSuits.filterNot { it == suitToExclude }.random()
}

fun factoryMarkStrategySuitWrapper(
    hand: List<String>,
    jokerValue: Int,
    includeMoons: Boolean,
    includeStars: Boolean,
    bid: EntropyBid?,
): MarkStrategySuitWrapper {
    val applicableSuits = Suit.filter(includeMoons, includeStars)

    val bestSuit = applicableSuits.reversed().maxBy { countSuit(it, hand, jokerValue) }
    val worstSuit = applicableSuits.minBy { countSuit(it, hand, jokerValue) }

    val suitsPossibleToMinBid: List<Suit> =
        if (bid == null) emptyList()
        else {
            applicableSuits.filter { suit ->
                val myCount = countSuit(suit, hand, jokerValue)
                myCount > bid.bidAmount || (myCount == bid.bidAmount && suit > bid.bidSuit)
            }
        }

    return MarkStrategySuitWrapper(applicableSuits, bestSuit, worstSuit, suitsPossibleToMinBid)
}
