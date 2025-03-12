package http

import http.dto.JoinRoomRequest
import http.dto.JoinRoomResponse
import http.dto.RoomStateResponse
import http.dto.SitDownRequest
import http.dto.StandUpRequest
import java.util.UUID
import javax.swing.SwingUtilities
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
            httpClient.doCall<RoomStateResponse>(
                HttpMethod.POST,
                Routes.SIT_DOWN,
                SitDownRequest(roomId, seat),
            )

        when (response) {
            is SuccessResponse<RoomStateResponse> -> handleSitDown(roomId, seat, response.body)
            is FailureResponse -> handleSitDownFailure(response)
            is CommunicationError ->
                DialogUtilNew.showErrorLater(
                    "Error communicating with server.\n\n${response.unirestException.message}"
                )
        }
    }

    private fun handleSitDown(roomId: UUID, seat: Int, response: RoomStateResponse) {
        val room = ScreenCache.get<EntropyLobby>().getGameRoomForId(roomId)
        room.setObserver(false)
        room.setPlayerNumber(seat)
        room.init(true)
        room.synchronisePlayers(response.players, response.formerPlayers)
    }

    private fun handleSitDownFailure(response: FailureResponse<*>) {
        SwingUtilities.invokeLater {
            when (response.errorCode) {
                SEAT_TAKEN -> DialogUtilNew.showError("This seat has been taken.")
                else ->
                    DialogUtilNew.showError("An unexpected error occurred trying to take a seat.")
            }
        }
    }

    fun standUp(room: GameRoom) {
        val response =
            httpClient.doCall<RoomStateResponse>(
                HttpMethod.POST,
                Routes.STAND_UP,
                StandUpRequest(room.id),
            )

        when (response) {
            is SuccessResponse<RoomStateResponse> -> handleStandUp(room, response.body)
            else -> DialogUtilNew.showError("An error occurred attempting to stand up.")
        }
    }

    private fun handleStandUp(room: GameRoom, response: RoomStateResponse) {
        room.setObserver(true)
        room.init(true)
        room.synchronisePlayers(response.players, response.formerPlayers)
    }
}
