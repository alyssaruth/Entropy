package util

import game.GameMode

object ReplayConstants {
    const val GAME_MODE_ENTROPY = 1
    const val GAME_MODE_VECTROPY = 2
    const val GAME_MODE_ENTROPY_ONLINE = 3
    const val GAME_MODE_VECTROPY_ONLINE = 4

    @JvmStatic
    fun toGameMode(replayGameMode: Int): GameMode =
        when (replayGameMode) {
            GAME_MODE_ENTROPY,
            GAME_MODE_ENTROPY_ONLINE -> GameMode.Entropy
            else -> GameMode.Vectropy
        }
}

fun GameMode.toReplayConstant() =
    when (this) {
        GameMode.Entropy -> ReplayConstants.GAME_MODE_ENTROPY
        GameMode.Vectropy -> ReplayConstants.GAME_MODE_VECTROPY
    }
