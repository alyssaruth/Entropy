package routes.health

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

object HealthCheckController {
    private val healthCheckService = HealthCheckService()

    fun installRoutes(application: Application) {
        application.routing { get("/health-check") { doHealthCheck(call) } }
    }

    private suspend fun doHealthCheck(call: ApplicationCall) {
        val response = healthCheckService.healthCheck()
        call.respond(response)
    }
}