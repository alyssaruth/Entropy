package plugins

import http.ClientErrorCode
import io.kotest.matchers.shouldBe
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import org.junit.jupiter.api.Test
import routes.ClientException
import testCore.AbstractTest
import testCore.shouldMatchJson

class RoutingTest : AbstractTest() {
    @Test
    fun `Should handle client errors`() = testApplication {
        application { ErrorThrowingController.installRoutes(this) }

        val response = client.get("/client-error")
        response.status shouldBe HttpStatusCode.Conflict
        response.bodyAsText() shouldMatchJson
            """{
            "errorCode": "conflictingEntity",
            "errorMessage": "Entity conflicts with another"
        }"""
                .trimIndent()
    }

    @Test
    fun `Should handle unexpected errors`() = testApplication {
        application { ErrorThrowingController.installRoutes(this) }

        val response = client.get("/internal-error")
        response.status shouldBe HttpStatusCode.InternalServerError
        response.bodyAsText() shouldMatchJson
            """{
            "errorCode": "internalServerError",
            "errorMessage": "Error handling GET - /internal-error"
        }"""
                .trimIndent()

        errorLogged() shouldBe true
    }

    @Test
    fun `Should 404 for non-existent route`() = testApplication {
        val response = client.get("/non-existent-route")
        response.status shouldBe HttpStatusCode.NotFound
    }
}

private object ErrorThrowingController {
    fun installRoutes(application: Application) {
        application.routing {
            get("/internal-error") { throw NullPointerException("Test error") }
            get("/client-error") {
                throw ClientException(
                    HttpStatusCode.Conflict,
                    ClientErrorCode("conflictingEntity"),
                    "Entity conflicts with another",
                )
            }
        }
    }
}
