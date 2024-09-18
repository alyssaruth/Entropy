package routes.dev

import dto.DevCommandRequest
import http.Routes
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

object DevController {
    private val devService = DevService()

    fun installRoutes(application: Application) {
        application.routing { post(Routes.DEV_COMMAND) { doDevCommand(call) } }
    }

    private suspend fun doDevCommand(call: ApplicationCall) {
        val request = call.receive<DevCommandRequest>()
        val response = devService.processCommand(request.command)
        call.respond(response)
    }
}
