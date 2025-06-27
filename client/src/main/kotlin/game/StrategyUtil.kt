package game

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
