package util

import game.GameSettings
import strategy.IStrategy

data class SimulationParams(
    val settings: GameSettings,
    val opponentTwoEnabled: Boolean,
    val opponentThreeEnabled: Boolean,
    val opponentZeroStrategy: IStrategy,
    val opponentOneStrategy: IStrategy,
    val opponentTwoStrategy: IStrategy,
    val opponentThreeStrategy: IStrategy,
    val enableLogging: Boolean,
    val randomiseOrder: Boolean,
    val forceStart: Boolean,
)
