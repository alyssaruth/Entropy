package game

import kotlin.math.ceil
import kotlin.math.floor
import strategy.DefaultRandom
import strategy.IRandom
import util.StrategyParams

fun getEvMap(
    visibleCards: List<String>,
    settings: GameSettings,
    cardsInPlay: Int,
): Map<Suit, Double> {
    val unknownCardsInPlay = cardsInPlay - visibleCards.size
    val remainingDeck = createAndShuffleDeck(settings).filterNot { visibleCards.contains(it) }

    return Suit.filter(settings).associateWith { suit ->
        val known = countSuit(suit, visibleCards, settings.jokerValue)
        val possibleOthers =
            remainingDeck.sumOf { countContribution(suit, it, settings.jokerValue) }.toDouble()
        val extraEv =
            possibleOthers * (unknownCardsInPlay.toDouble() / remainingDeck.size.toDouble())

        extraEv + known
    }
}

fun getDifferenceMap(
    bid: VectropyBidAction,
    hand: List<String>,
    jokerValue: Int,
    includeMoons: Boolean,
    includeStars: Boolean,
): Map<Suit, Int> {
    val suits = Suit.filter(includeMoons, includeStars)
    return suits.associateWith { countSuit(it, hand, jokerValue) - bid.getAmount(it)!! }
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

fun computeEvDifferences(bid: VectropyBidAction, evMap: Map<Suit, Double>): Map<Suit, Double> =
    evMap.mapValues { (suit, ev) -> ev - bid.getAmount(suit)!! }

fun shouldAutoChallengeForEvDiffOfIndividualSuit(evDifferenceMap: Map<Suit, Double>) =
    evDifferenceMap.any { it.value < -0.5 }

fun shouldAutoChallengeForMultipleSuitsOverEv(evDifferenceMap: Map<Suit, Double>) =
    evDifferenceMap.count { it.value < 0 } > 1

@JvmOverloads
fun getBasicVectropyOpening(
    opponentName: String,
    hand: List<String>,
    strategyParams: StrategyParams,
    random: IRandom = DefaultRandom(),
): VectropyBidAction {
    val settings = strategyParams.settings
    val suits = Suit.filter(settings)

    if (strategyParams.cardsInPlay <= 4) {
        val empty = suits.associateWith { 0 }
        val suitChoice = random.nextInt(suits.size)
        return VectropyBidAction(opponentName, false, empty).incrementSuit(suits[suitChoice])
    }

    val map =
        suits.associateWith { suit ->
            val myCount = countSuit(suit, hand, settings.jokerValue)
            maxOf(0, myCount + random.nextInt(3) - 1)
        }

    val bid = VectropyBidAction(opponentName, false, map)
    if (bid.getTotal() > 0) {
        return bid
    }

    return bid.incrementSuit(suits[random.nextInt(suits.size)])
}

@JvmOverloads
fun getEvVectropyOpening(
    opponentName: String,
    hand: List<String>,
    strategyParams: StrategyParams,
    random: IRandom = DefaultRandom(),
): VectropyBidAction {
    val settings = strategyParams.settings
    val hmEvBySuit = getEvMap(hand, settings, strategyParams.cardsInPlay)

    val suits = Suit.filter(settings)
    val map =
        suits.associateWith { suit ->
            val evFloor = floor(hmEvBySuit.getValue(suit)).toInt()

            val adjustmentSwitch = random.nextInt(20)
            val adjusted =
                if (adjustmentSwitch < 11) {
                    evFloor - 1
                } else if (adjustmentSwitch < 18) {
                    evFloor - 2
                } else {
                    evFloor - 3
                }

            maxOf(0, adjusted)
        }

    val bid = VectropyBidAction(opponentName, false, map)
    if (bid.getTotal() > 0) {
        return bid
    }

    // Just bid 1 of something, leaning towards choosing our best suit
    if (random.nextInt(10) < 6) {
        return bid.incrementSuit(getSuitWithMostPositiveValue(hmEvBySuit))
    }

    return bid.incrementSuit(suits[random.nextInt(suits.size)])
}
