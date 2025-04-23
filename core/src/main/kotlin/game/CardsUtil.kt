package game

fun countSuit(suit: Suit, cards: List<String>, jokerValue: Int) =
    cards.sumOf { countContribution(suit, it, jokerValue) }

fun countContribution(suit: Suit, card: String, jokerValue: Int) =
    if (card == "A${suit.letter}") 2
    else if (card.contains("A")) 1
    else if (card.startsWith("Jo")) jokerValue
    else if (card == "-J${suit.letter}") -1 else if (card.contains(suit.letter)) 1 else 0
