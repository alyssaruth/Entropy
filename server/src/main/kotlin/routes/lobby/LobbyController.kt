package routes.lobby

import http.Routes
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import routes.requiresSession
import util.ServerGlobals

object LobbyController {
    fun installRoutes(application: Application) {
        application.routing { get(Routes.LOBBY) { doGetLobby(call) } }
    }

    private suspend fun doGetLobby(call: ApplicationCall) =
        requiresSession(call) {
            val response = ServerGlobals.lobbyService.getLobby()
            call.respond(response)
        }
}
