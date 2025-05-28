package game

import util.CardsUtil
import util.StrategyParms

fun countSuit(suit: Suit, cards: List<String>, jokerValue: Int) =
    cards.sumOf { countContribution(suit, it, jokerValue) }

fun countContribution(suit: Suit, card: String, jokerValue: Int) =
    if (card == "A${suit.letter}") 2
    else if (card.contains("A")) 1
    else if (card.startsWith("Jo")) jokerValue
    else if (card == "-J${suit.letter}") -1 else if (card.contains(suit.letter)) 1 else 0

fun isCardRelevant(card: String, suit: Suit) = countContribution(suit, card, 1) > 0

fun getEvMap(visibleCards: List<String>, strategyParams: StrategyParms): Map<Suit, Double> {
    val unknownCardsInPlay = strategyParams.totalNumberOfCards - visibleCards.size
    val remainingDeck =
        CardsUtil.createAndShuffleDeck(
                strategyParams.jokerQuantity,
                strategyParams.includeMoons,
                strategyParams.includeStars,
                strategyParams.negativeJacks,
            )
            .filterNot { visibleCards.contains(it) }

    return Suit.entries.associateWith { suit ->
        val known = countSuit(suit, visibleCards, strategyParams.jokerValue)
        val possibleOthers =
            remainingDeck
                .sumOf { countContribution(suit, it, strategyParams.jokerValue) }
                .toDouble()
        val extraEv =
            possibleOthers * (unknownCardsInPlay.toDouble() / remainingDeck.size.toDouble())

        extraEv + known
    }
}
