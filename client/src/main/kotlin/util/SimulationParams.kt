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
)
