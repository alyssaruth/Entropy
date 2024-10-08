package http.dto

import http.ClientMessageType

data class LobbyResponse(val rooms: List<RoomSummary>, val users: List<OnlineUser>) :
    ClientMessage() {
    override val messageType = ClientMessageType.LOBBY
}
