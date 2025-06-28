package game

import kotlin.math.ceil
import `object`.VectropyBid
import util.CardsUtil

fun getEvMap(
    visibleCards: List<String>,
    settings: GameSettings,
    cardsInPlay: Int,
): Map<Suit, Double> {
    val unknownCardsInPlay = cardsInPlay - visibleCards.size
    val remainingDeck =
        CardsUtil.createAndShuffleDeck(settings).filterNot { visibleCards.contains(it) }

    return Suit.filter(settings.includeMoons, settings.includeStars).associateWith { suit ->
        val known = countSuit(suit, visibleCards, settings.jokerValue)
        val possibleOthers =
            remainingDeck.sumOf { countContribution(suit, it, settings.jokerValue) }.toDouble()
        val extraEv =
            possibleOthers * (unknownCardsInPlay.toDouble() / remainingDeck.size.toDouble())

        extraEv + known
    }
}

fun getDifferenceMap(
    bid: VectropyBid,
    hand: List<String>,
    jokerValue: Int,
    includeMoons: Boolean,
    includeStars: Boolean,
): Map<Suit, Int> {
    val suits = Suit.filter(includeMoons, includeStars)
    return suits.associateWith { countSuit(it, hand, jokerValue) - bid.getAmount(it) }
}

fun <T : Comparable<T>> getSuitWithMostPositiveValue(map: Map<Suit, T>) = map.maxBy { it.value }.key

fun <T : Comparable<T>> getSuitsWithMostPositiveValue(map: Map<Suit, T>): List<Suit> {
    val max = map.maxOf { it.value }
    return map.filterValues { it == max }.keys.toList()
}

fun <T : Comparable<T>> getMaxValue(map: Map<Suit, T>) = map.maxOf { it.value }

fun allNonNegative(differenceMap: Map<Suit, Int>) = differenceMap.all { it.value >= 0 }

fun belowEvInAllSuits(map: Map<Suit, Double>) = map.all { it.value > 0.0 }

fun shouldAutoChallengeForIndividualSuit(differenceMap: Map<Suit, Int>, threshold: Int) =
    differenceMap.any { it.value < -threshold }

fun shouldAutoChallengeForOverall(differenceMap: Map<Suit, Int>, threshold: Int) =
    differenceMap.values.sum() < -threshold

fun bidIsSensible(differenceMap: Map<Suit, Int>, unseenCards: Int): Boolean {
    val total = differenceMap.values.sum()
    val comparison = (-ceil((unseenCards.toDouble() / 2))).toInt()
    return total >= comparison
}

fun computeEvDifferences(bid: VectropyBid, evMap: Map<Suit, Double>): Map<Suit, Double> =
    evMap.mapValues { (suit, ev) -> ev - bid.getAmount(suit) }

fun shouldAutoChallengeForEvDiffOfIndividualSuit(evDifferenceMap: Map<Suit, Double>) =
    evDifferenceMap.any { it.value < -0.5 }

fun shouldAutoChallengeForOverallEvDiff(evDifferenceMap: Map<Suit, Double>) =
    evDifferenceMap.values.sum() < 0

fun shouldAutoChallengeForMultipleSuitsOverEv(evDifferenceMap: Map<Suit, Double>) =
    evDifferenceMap.count { it.value < 0 } > 1
