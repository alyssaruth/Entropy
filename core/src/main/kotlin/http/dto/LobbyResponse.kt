package http.dto

data class LobbyResponse(val rooms: List<RoomSummary>, val users: List<OnlineUser>)
