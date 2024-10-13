package http.dto

data class LobbyMessage(val rooms: List<RoomSummary>, val users: List<OnlineUser>) :
    ClientMessage()
