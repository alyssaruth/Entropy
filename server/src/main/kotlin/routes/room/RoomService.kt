package routes.room

import auth.Session
import http.INVALID_ROOM_ID
import http.SEAT_TAKEN
import http.dto.JoinRoomRequest
import http.dto.JoinRoomResponse
import http.dto.SitDownRequest
import http.dto.StandUpRequest
import io.ktor.http.HttpStatusCode
import java.util.UUID
import routes.ClientException
import store.RoomStore

class RoomService(private val roomStore: RoomStore) {
    fun joinRoom(session: Session, request: JoinRoomRequest): JoinRoomResponse {
        val room = lookupRoom(request.roomId)
        val username = session.name
        room.addToObservers(username)

        return room.buildJoinRoomResponse()
    }

    fun sitDown(session: Session, request: SitDownRequest) {
        val room = lookupRoom(request.roomId)

        val result =
            room.attemptToSitDown(session.name, request.playerNumber)
                ?: throw ClientException(HttpStatusCode.Conflict, SEAT_TAKEN, "")
    }

    fun standUp(session: Session, request: StandUpRequest) {
        val room = lookupRoom(request.roomId)
    }

    private fun lookupRoom(id: UUID) =
        roomStore.find(id)
            ?: throw ClientException(
                HttpStatusCode.BadRequest,
                INVALID_ROOM_ID,
                "No room exists for id $id",
            )
}
