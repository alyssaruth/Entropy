package routes.session

import http.Routes
import http.dto.BeginSessionRequest
import http.dto.UpdateAchievementCountRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.requiresSession
import util.ServerGlobals

class SessionController {
    private val sessionService = SessionService(ServerGlobals.sessionStore, ServerGlobals.uscStore)

    fun installRoutes(application: Application) {
        application.routing {
            post(Routes.BEGIN_SESSION) { beginSession(call) }
            post(Routes.ACHIEVEMENT_COUNT) { updateAchievementCount(call) }
        }
    }

    private suspend fun beginSession(call: ApplicationCall) {
        val ip = call.request.origin.remoteAddress
        val request = call.receive<BeginSessionRequest>()
        val response = sessionService.beginSession(request, ip)
        call.respond(response)
    }

    private suspend fun updateAchievementCount(call: ApplicationCall) =
        requiresSession(call) { session ->
            val request = call.receive<UpdateAchievementCountRequest>()
            sessionService.updateAchievementCount(session, request.achievementCount)
            call.respond(HttpStatusCode.NoContent)
        }
}
