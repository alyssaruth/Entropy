package routes.session

import http.Routes
import http.dto.BeginSessionRequest
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

object SessionController {
    fun installRoutes(application: Application) {
        application.routing {
            val service by inject<SessionService>()

            post(Routes.BEGIN_SESSION) { beginSession(call, service) }
            post(Routes.ACHIEVEMENT_COUNT) { updateAchievementCount(call, service) }
        }
    }

    private suspend fun beginSession(call: ApplicationCall, service: SessionService) {
        val ip = call.request.origin.remoteAddress
        val request = call.receive<BeginSessionRequest>()
        val response = service.beginSession(request, ip)
        call.respond(response)
    }

    private suspend fun updateAchievementCount(call: ApplicationCall, service: SessionService) {}
}
