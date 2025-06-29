package game

fun getCardHtml(card: String): String {
    if (card.contains("Jo")) {
        return "<font color=\"#FF33CC\">Jo</font>"
    }

    val nonNegative = card.replace("-", "")
    val suit = Suit.entries.first { nonNegative.contains(it.letter) }
    return "<font color=\"${suit.getColourHex()}\" face=\"Segoe UI Symbol\">$nonNegative</font>"
}

fun getVectropyResult(
    cards: List<String>,
    jokerValue: Int,
    suit: Suit?,
    includeMoons: Boolean,
    includeStars: Boolean,
): String {
    val htmlStrings =
        Suit.filter(includeMoons, includeStars).map { s ->
            val amount = countSuit(s, cards, jokerValue)
            val amountStr = if (suit == s) "<b>$amount</b>" else "$amount"
            "<font color=\"${s.getColourHex()}\">$amountStr</font>"
        }

    return "(${htmlStrings.joinToString(", ")})"
}
