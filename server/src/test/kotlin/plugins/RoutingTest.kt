package plugins

import http.ClientErrorCode
import http.CustomHeader
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.types.shouldBeInstanceOf
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.testing.*
import java.util.*
import logging.KEY_DURATION
import logging.KEY_REQUEST_ID
import logging.KEY_ROUTE
import logging.findLogField
import org.junit.jupiter.api.Test
import routes.ClientException
import routes.requiresSession
import testCore.shouldContainKeyValues
import testCore.shouldMatchJson
import util.ApplicationTest
import util.ServerGlobals
import util.makeSession

class RoutingTest : ApplicationTest() {
    @Test
    fun `Should handle client errors`() = testApplication {
        application { TestingController.installRoutes(this) }

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
        application { TestingController.installRoutes(this) }

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

    @Test
    fun `Should reject a call with no session ID`() = testApplication {
        application { TestingController.installRoutes(this) }

        val response = client.get("/username")
        response.status shouldBe HttpStatusCode.Unauthorized
        response.bodyAsText().shouldContain("Missing session id")
    }

    @Test
    fun `Should reject a call with a malformed session ID`() = testApplication {
        application { TestingController.installRoutes(this) }

        val response = client.get("/username") { header(CustomHeader.SESSION_ID, "foo") }
        response.status shouldBe HttpStatusCode.BadRequest
        response.bodyAsText().shouldContain("Session ID was not a valid UUID")
    }

    @Test
    fun `Should reject a call with a nonexistent session ID`() = testApplication {
        application { TestingController.installRoutes(this) }

        val sessionId = UUID.randomUUID()

        val response = client.get("/username") { header(CustomHeader.SESSION_ID, sessionId) }
        response.status shouldBe HttpStatusCode.Unauthorized
        response.bodyAsText().shouldContain("No session found for ID $sessionId")
    }

    @Test
    fun `Should successfully extract a session that exists`() = testApplication {
        application { TestingController.installRoutes(this) }

        val session = makeSession()
        ServerGlobals.sessionStore.put(session)

        val response = client.get("/username") { header(CustomHeader.SESSION_ID, session.id) }
        response.status shouldBe HttpStatusCode.OK
        response.bodyAsText().shouldBe(session.name)
    }
}

private object TestingController {
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
            get("/username") { requiresSession(call) { call.respond(it.name) } }
        }
    }
}
