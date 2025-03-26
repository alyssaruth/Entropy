package http.dto

import game.GameSettings
import java.util.UUID

data class RoomSummary(
    val id: UUID,
    val name: String,
    val gameSettings: GameSettings,
    val capacity: Int,
    val players: Int,
    val observers: Int,
)
