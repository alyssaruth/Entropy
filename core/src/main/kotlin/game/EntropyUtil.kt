package game

fun amountRequiredToBid(desiredSuit: Suit, currentSuit: Suit, currentAmount: Int) =
    if (desiredSuit > currentSuit) currentAmount else currentAmount + 1

fun perfectBidAmount(cards: List<String>, jokerValue: Int) =
    Suit.entries.maxOf { countSuit(it, cards, jokerValue) }

fun perfectBidSuit(cards: List<String>, jokerValue: Int, includeStars: Boolean) =
    Suit.entries.last {
        countSuit(it, cards, jokerValue) == perfectBidAmount(cards, jokerValue) &&
            (includeStars || it != Suit.Stars)
    }
