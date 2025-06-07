package util

import game.GameSettings
import `object`.Bid

data class StrategyParams(
    val settings: GameSettings,
    val cardsInPlay: Int,
    val opponentCardsOnShow: List<String>,
    val lastBid: Bid?,
    val logging: Boolean,
)
