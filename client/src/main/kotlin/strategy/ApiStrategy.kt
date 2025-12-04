package strategy

import game.GameMode
import java.util.UUID

data class ApiStrategy(
    override val name: String,
    val supportedModes: List<GameMode>,
    val port: Int,
    val lastError: String? = null,
    val id: UUID = UUID.randomUUID(),
) : IStrategy
