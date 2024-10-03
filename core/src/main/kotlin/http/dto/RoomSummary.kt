package http.dto

import game.GameSettings

data class RoomSummary(
    val name: String,
    val gameSettings: GameSettings,
    val capacity: Int,
    val players: Int,
    val observers: Int,
)
