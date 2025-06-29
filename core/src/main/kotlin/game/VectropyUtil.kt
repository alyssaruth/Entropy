package game

import `object`.VectropyBid

fun isOverbid(bid: VectropyBid, allCards: List<String>, jokerValue: Int): Boolean {
    val suits = Suit.filter(bid.includeMoons, bid.includeStars)
    return suits.any { bid.getAmount(it) > countSuit(it, allCards, jokerValue) }
}
