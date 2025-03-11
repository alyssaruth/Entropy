package http

import http.dto.JoinRoomRequest
import http.dto.JoinRoomResponse
import http.dto.SitDownRequest
import java.util.UUID
import kong.unirest.HttpMethod
import online.screen.EntropyLobby
import online.screen.GameRoom
import screen.ScreenCache
import util.DialogUtilNew

class RoomApi(private val httpClient: HttpClient) {
    fun joinRoom(room: GameRoom) {
        val response =
            httpClient.doCall<JoinRoomResponse>(
                HttpMethod.POST,
                Routes.JOIN_ROOM,
                JoinRoomRequest(room.id),
            )

        when (response) {
            is SuccessResponse<JoinRoomResponse> -> handleJoinRoom(room, response.body)
            else -> DialogUtilNew.showError("An error occurred attempting to join this room")
        }
    }

    private fun handleJoinRoom(room: GameRoom, response: JoinRoomResponse) {
        room.username = ScreenCache.get<EntropyLobby>().username
        room.observer = true
        room.isVisible = true
        room.init(false)
        room.synchronisePlayers(response.players, response.formerPlayers)
        room.chatPanel.updateChatBox(response.chatHistory)
    }

    fun sitDown(roomId: UUID, seat: Int) {
        val response =
            httpClient.doCall<Unit>(HttpMethod.POST, Routes.SIT_DOWN, SitDownRequest(roomId, seat))
    }
}
