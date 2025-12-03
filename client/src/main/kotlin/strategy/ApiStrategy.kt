package strategy

import game.GameMode

data class ApiStrategy(
    val name: String,
    val supportedModes: List<GameMode>,
    val port: Int,
    val lastError: String? = null,
)
