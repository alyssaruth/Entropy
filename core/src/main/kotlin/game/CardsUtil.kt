package game

fun extractCards(handMap: Map<Int, List<String>>) = handMap.values.flatten()

fun countSuit(suit: Suit, cards: Map<Int, List<String>>, jokerValue: Int): Int =
    countSuit(suit, cards.values.flatten(), jokerValue)

fun countSuit(suit: Suit, cards: List<String>, jokerValue: Int) =
    cards.sumOf { countContribution(suit, it, jokerValue) }

fun countContribution(suit: Suit, card: String, jokerValue: Int) =
    if (card == "A${suit.letter}") 2
    else if (card.contains("A")) 1
    else if (card.startsWith("Jo")) jokerValue
    else if (card == "-J${suit.letter}") -1 else if (card.contains(suit.letter)) 1 else 0

fun isCardRelevant(card: String, suit: Suit) = countContribution(suit, card, 1) > 0
