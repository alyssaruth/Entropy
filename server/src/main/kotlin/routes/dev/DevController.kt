package routes.dev

import dto.DevCommandRequest
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import util.AbstractClient.devMode

object DevController {
    private val devService = DevService()

    fun installRoutes(application: Application) {
        if (devMode) {
            application.routing { post("/dev-command") { doDevCommand(call) } }
        }
    }

    private suspend fun doDevCommand(call: ApplicationCall) {
        val request = call.receive<DevCommandRequest>()
        val response = devService.processCommand(request.command)
        call.respond(response)
    }
}
