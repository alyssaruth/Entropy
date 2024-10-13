package util

import game.GameMode

object ReplayConstants {
    const val GAME_MODE_ENTROPY = 1
    const val GAME_MODE_VECTROPY = 2
    const val GAME_MODE_ENTROPY_ONLINE = 3
    const val GAME_MODE_VECTROPY_ONLINE = 4
}

fun GameMode.toReplayConstant() =
    when (this) {
        GameMode.Entropy -> ReplayConstants.GAME_MODE_ENTROPY
        GameMode.Vectropy -> ReplayConstants.GAME_MODE_VECTROPY
    }
