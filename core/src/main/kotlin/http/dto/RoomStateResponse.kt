package http.dto

data class RoomStateResponse(val players: Map<Int, String>, val formerPlayers: Map<Int, String>)
