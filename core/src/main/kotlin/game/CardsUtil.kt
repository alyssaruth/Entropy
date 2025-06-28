package game

import java.util.*

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

fun isCardRelevant(card: String, suit: Suit?) = suit == null || countContribution(suit, card, 1) > 0

/**
 * http://www.datamation.com/entdev/article.php/11070_616221_3/How-We-Learned-to-Cheat-at-Online-Poker-A-Study-in-Software-Security.htm
 */
@JvmOverloads
fun createAndShuffleDeck(settings: GameSettings, seed: Long? = null): List<String> {
    // Creating the pack of cards
    val suits = Suit.filter(settings.includeMoons, settings.includeStars).map(Suit::letter)

    val jokers = (0..<settings.jokerQuantity).map { "Jo$it" }

    val jackStr = if (settings.negativeJacks) "-J" else "J"
    val ranks = listOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "T", jackStr, "Q", "K")

    val cards = jokers + ranks.flatMap { rank -> suits.map { suit -> "${rank}${suit}" } }

    // Shuffle the pack using Fisher-Yates.
    val r = seed?.let(::Random) ?: Random()
    return cards.shuffled(r)
}
