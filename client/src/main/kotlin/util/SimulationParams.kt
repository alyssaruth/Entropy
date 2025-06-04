package util

import game.GameSettings

data class SimulationParams(
    val settings: GameSettings,
    val opponentTwoEnabled: Boolean,
    val opponentThreeEnabled: Boolean,
    val opponentZeroStrategy: String,
    val opponentOneStrategy: String,
    val opponentTwoStrategy: String,
    val opponentThreeStrategy: String,
    val enableLogging: Boolean,
    val randomiseOrder: Boolean,
    val forceStart: Boolean,
) {
    fun toStrategyParams(): StrategyParms {
        val parms = StrategyParms()
        parms.gameMode = settings.mode
        parms.includeMoons = settings.includeMoons
        parms.includeStars = settings.includeStars
        parms.negativeJacks = settings.negativeJacks
        parms.cardReveal = settings.cardReveal
        parms.jokerQuantity = settings.jokerQuantity
        parms.jokerValue = settings.jokerValue
        parms.logging = enableLogging

        return parms
    }
}
