package http

import http.dto.JoinRoomResponse
import http.dto.RoomStateResponse
import http.dto.SimpleRoomRequest
import http.dto.SitDownRequest
import kong.unirest.HttpMethod
import online.screen.EntropyLobby
import online.screen.GameRoom
import screen.ScreenCache
import util.DialogUtilNew

class RoomApi(private val httpClient: HttpClient) {
    fun joinRoom(room: GameRoom<*>) {
        val response =
            httpClient.doCall<JoinRoomResponse>(
                HttpMethod.POST,
                Routes.JOIN_ROOM,
                SimpleRoomRequest(room.id),
            )

        when (response) {
            is SuccessResponse<JoinRoomResponse> -> handleJoinRoom(room, response.body)
            else -> DialogUtilNew.showError("An error occurred attempting to join this room")
        }
    }

    private fun handleJoinRoom(room: GameRoom<*>, response: JoinRoomResponse) {
        room.username = ScreenCache.get<EntropyLobby>().username
        room.observer = true
        room.isVisible = true
        room.init(false)
        room.synchronisePlayers(response.players, response.formerPlayers)
        room.chatPanel.updateChatBox(response.chatHistory)
    }

    fun sitDown(room: GameRoom<*>, seat: Int) {
        val response =
            httpClient.doCall<RoomStateResponse>(
                HttpMethod.POST,
                Routes.SIT_DOWN,
                SitDownRequest(room.id, seat),
            )

        when (response) {
            is SuccessResponse<RoomStateResponse> -> handleSitDown(room, seat, response.body)
            is FailureResponse -> handleSitDownFailure(response)
            is CommunicationError ->
                DialogUtilNew.showErrorLater(
                    "Error communicating with server.\n\n${response.unirestException.message}"
                )
        }
    }

    private fun handleSitDown(room: GameRoom<*>, seat: Int, response: RoomStateResponse) {
        room.setObserver(false)
        room.setPlayerNumber(seat)
        room.init(true)
        room.synchronisePlayers(response.players, response.formerPlayers)
    }

    private fun handleSitDownFailure(response: FailureResponse<*>) {
        when (response.errorCode) {
            SEAT_TAKEN -> DialogUtilNew.showError("This seat has been taken.")
            else -> DialogUtilNew.showError("An unexpected error occurred trying to take a seat.")
        }
    }

    fun standUp(room: GameRoom<*>) {
        val response =
            httpClient.doCall<RoomStateResponse>(
                HttpMethod.POST,
                Routes.STAND_UP,
                SimpleRoomRequest(room.id),
            )

        when (response) {
            is SuccessResponse<RoomStateResponse> -> handleStandUp(room, response.body)
            else -> DialogUtilNew.showError("An error occurred attempting to stand up.")
        }
    }

    fun leaveRoom(room: GameRoom<*>) {
        val response =
            httpClient.doCall<Unit>(HttpMethod.POST, Routes.LEAVE_ROOM, SimpleRoomRequest(room.id))

        when (response) {
            is SuccessResponse<Unit> -> room.closeWindow()
            else -> DialogUtilNew.showError("An error occurred trying to leave this room.")
        }
    }

    private fun handleStandUp(room: GameRoom<*>, response: RoomStateResponse) {
        room.setObserver(true)
        room.init(true)
        room.synchronisePlayers(response.players, response.formerPlayers)
    }
}
