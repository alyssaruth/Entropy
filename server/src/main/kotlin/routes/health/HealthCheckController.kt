package routes.health

import http.Routes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class HealthCheckController {
    private val healthCheckService = HealthCheckService()

    fun installRoutes(application: Application) {
        application.routing { get(Routes.HEALTH_CHECK) { doHealthCheck(call) } }
    }

    private suspend fun doHealthCheck(call: ApplicationCall) {
        val response = healthCheckService.healthCheck()
        call.respond(response)
    }
}
