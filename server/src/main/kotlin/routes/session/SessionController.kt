package routes.session

import http.Routes
import http.dto.BeginSessionRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import util.ServerGlobals

object SessionController {
    private val sessionService = SessionService(ServerGlobals.sessionStore, ServerGlobals.uscStore)

    fun installRoutes(application: Application) {
        application.routing { post(Routes.BEGIN_SESSION) { beginSession(call) } }
    }

    private suspend fun beginSession(call: ApplicationCall) {
        val ip = call.request.origin.remoteAddress
        val request = call.receive<BeginSessionRequest>()
        val response = sessionService.beginSession(request, ip)
        call.respond(response)
    }
}
