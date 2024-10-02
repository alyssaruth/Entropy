package plugins

import http.ClientErrorCode
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import java.util.UUID
import logging.KEY_DURATION
import logging.KEY_REQUEST_ID
import logging.KEY_ROUTE
import logging.findLogField
import org.junit.jupiter.api.Test
import routes.ClientException
import testCore.AbstractTest
import testCore.shouldContainKeyValues
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

        val handledLog = verifyLog("callHandled")
        handledLog.shouldContainKeyValues(
            "status" to HttpStatusCode.NotFound,
            KEY_ROUTE to "GET /non-existent-route",
        )
    }

    @Test
    fun `Should log when requests are received and handled, with the passed up callId`() =
        testApplication {
            val requestId = UUID.randomUUID()
            client.get("/health-check") { header(HttpHeaders.XRequestId, requestId) }

            val receivedLog = verifyLog("callReceived")
            receivedLog.shouldContainKeyValues(
                KEY_REQUEST_ID to requestId.toString(),
                KEY_ROUTE to "GET /health-check",
            )

            val handledLog = verifyLog("callHandled")
            handledLog.shouldContainKeyValues(
                "status" to HttpStatusCode.OK,
                KEY_REQUEST_ID to requestId.toString(),
                KEY_ROUTE to "GET /health-check",
            )

            val duration = handledLog.findLogField(KEY_DURATION)
            duration.shouldBeInstanceOf<Long>()
            duration.shouldBeGreaterThan(0)
        }

    @Test
    fun `Should generate a callId if one is not received in the request`() = testApplication {
        client.get("/health-check")

        val receivedLog = verifyLog("callReceived")
        val requestId = receivedLog.findLogField(KEY_REQUEST_ID)
        requestId.shouldNotBeNull()
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
