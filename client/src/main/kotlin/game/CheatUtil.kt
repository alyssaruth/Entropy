package game

fun getMaxBidString(cards: List<String>, settings: GameSettings) =
    Suit.filter(settings.includeMoons, settings.includeStars).joinToString { suit ->
        val amount = countSuit(suit, cards, settings.jokerValue)
        "$amount${suit.letter}"
    }

fun containsNonJoker(cards: List<String>) = cards.any { !it.startsWith("Jo") }
