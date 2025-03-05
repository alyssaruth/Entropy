package routes.room

import auth.Session
import http.INVALID_ROOM_ID
import http.dto.JoinRoomRequest
import http.dto.SitDownRequest
import io.ktor.http.HttpStatusCode
import java.util.UUID
import routes.ClientException
import store.RoomStore

class RoomService(private val roomStore: RoomStore) {
    fun joinRoom(session: Session, request: JoinRoomRequest) {
        val room = lookupRoom(request.roomId)
        val username = session.name
        room.addToObservers(username)

        // TODO - response
    }

    fun sitDown(session: Session, request: SitDownRequest) {
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
