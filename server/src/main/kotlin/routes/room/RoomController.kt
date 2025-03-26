package routes.room

import http.Routes
import http.dto.SimpleRoomRequest
import http.dto.SitDownRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.*
import routes.requiresSession
import util.ServerGlobals.roomService

class RoomController {
    fun installRoutes(application: Application) {
        application.routing { post(Routes.JOIN_ROOM) { joinRoom(call) } }
        application.routing { post(Routes.SIT_DOWN) { sitDown(call) } }
        application.routing { post(Routes.STAND_UP) { standUp(call) } }
        application.routing { post(Routes.LEAVE_ROOM) { leaveRoom(call) } }
    }

    private suspend fun joinRoom(call: ApplicationCall) =
        requiresSession(call) { session ->
            val request = call.receive<SimpleRoomRequest>()
            val response = roomService.joinRoom(session, request)
            call.respond(HttpStatusCode.OK, response)
        }

    private suspend fun sitDown(call: ApplicationCall) =
        requiresSession(call) { session ->
            val request = call.receive<SitDownRequest>()
            val response = roomService.sitDown(session, request)
            call.respond(HttpStatusCode.OK, response)
        }

    private suspend fun standUp(call: ApplicationCall) =
        requiresSession(call) { session ->
            val request = call.receive<SimpleRoomRequest>()
            val response = roomService.standUp(session, request)
            call.respond(HttpStatusCode.OK, response)
        }

    private suspend fun leaveRoom(call: ApplicationCall) =
        requiresSession(call) { session ->
            val request = call.receive<SimpleRoomRequest>()
            roomService.leaveRoom(session, request)
            call.respond(HttpStatusCode.NoContent)
        }
}
