package util

import game.BidAction
import game.GameSettings

data class StrategyParams(
    val settings: GameSettings,
    val cardsInPlay: Int,
    val opponentCardsOnShow: List<String>,
    val lastBid: BidAction<*>?,
    val logging: Boolean,
)
