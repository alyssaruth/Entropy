package http.dto

data class JoinRoomResponse(
    val chatHistory: List<OnlineMessage>,
    val players: Map<Int, String>,
    val formerPlayers: Map<Int, String>,
)
