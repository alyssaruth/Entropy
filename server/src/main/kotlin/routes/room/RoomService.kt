package routes.room

import auth.Session
import http.INVALID_ROOM_ID
import http.SEAT_TAKEN
import http.dto.JoinRoomResponse
import http.dto.RoomStateResponse
import http.dto.SimpleRoomRequest
import http.dto.SitDownRequest
import io.ktor.http.HttpStatusCode
import java.util.UUID
import routes.ClientException
import store.RoomStore

class RoomService(private val roomStore: RoomStore) {
    fun joinRoom(session: Session, request: SimpleRoomRequest): JoinRoomResponse {
        val room = lookupRoom(request.roomId)
        val username = session.name
        room.addToObservers(username)

        return room.buildJoinRoomResponse()
    }

    fun sitDown(session: Session, request: SitDownRequest): RoomStateResponse {
        val room = lookupRoom(request.roomId)

        room.attemptToSitDown(session.name, request.playerNumber)
            ?: throw ClientException(
                HttpStatusCode.Conflict,
                SEAT_TAKEN,
                "Seat ${request.playerNumber} is no longer free.",
            )

        return room.buildRoomStateResponse()
    }

    fun standUp(session: Session, request: SimpleRoomRequest): RoomStateResponse {
        val room = lookupRoom(request.roomId)
        room.addToObservers(session.name)

        return room.buildRoomStateResponse()
    }

    fun leaveRoom(session: Session, request: SimpleRoomRequest) {
        val room = lookupRoom(request.roomId)
        room.removeFromObservers(session.name)
        room.removePlayer(session.name, true)
    }

    private fun lookupRoom(id: UUID) =
        roomStore.find(id)
            ?: throw ClientException(
                HttpStatusCode.BadRequest,
                INVALID_ROOM_ID,
                "No room exists for id $id",
            )
}
