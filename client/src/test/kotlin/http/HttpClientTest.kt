package http

import ch.qos.logback.classic.Level
import http.dto.ClientErrorResponse
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKeys
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotBeEmpty
import java.util.UUID
import kong.unirest.HttpMethod
import kong.unirest.HttpStatus
import kong.unirest.JsonObjectMapper
import kong.unirest.Unirest
import logging.KEY_REQUEST_ID
import logging.findLogField
import logging.getLogFields
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class HttpClientTest : AbstractTest() {
    @BeforeEach
    fun beforeEach() {
        Unirest.config().reset()
    }

    @Test
    fun `Should pass a request id header and log it consistently`() {
        val (client, server) = setUpWebServer()
        server.enqueue(MockResponse().setResponseCode(HttpStatus.OK))

        client.doCall<TestApiResponse>(HttpMethod.GET, "/test-endpoint")

        val request = server.takeRequest()
        val requestId = request.getHeader("X-Request-ID")

        requestId.shouldNotBeEmpty()
        val requestUuid = shouldNotThrowAny { UUID.fromString(requestId) }

        val requestLog = verifyLog("http.request", Level.INFO)
        requestLog.findLogField(KEY_REQUEST_ID) shouldBe requestUuid

        val responseLog = verifyLog("http.response", Level.INFO)
        responseLog.findLogField(KEY_REQUEST_ID) shouldBe requestUuid
    }

    @Test
    fun `Should pass a session id header if present`() {
        val (client, server) = setUpWebServer()
        server.enqueue(MockResponse().setResponseCode(HttpStatus.OK))
        val sessionId = UUID.randomUUID()
        client.sessionId = sessionId

        client.doCall<TestApiResponse>(HttpMethod.GET, "/test-endpoint")

        val request = server.takeRequest()
        val sessionIdFromHeader = request.getHeader(CustomHeader.SESSION_ID)

        UUID.fromString(sessionIdFromHeader) shouldBe sessionId
    }

    @Test
    fun `Successful GET request`() {
        val (client, server) = setUpWebServer()
        val responseBody =
            """{
            "fieldOne": "foo",
            "fieldTwo": 500
        }"""

        server.enqueue(MockResponse().setBody(responseBody))

        val response = client.doCall<TestApiResponse>(HttpMethod.GET, "/test-endpoint")
        response shouldBe SuccessResponse(200, TestApiResponse("foo", 500))

        val request = server.takeRequest()
        request.path shouldBe "/root/test-endpoint"
        request.method shouldBe "GET"

        val requestLog = verifyLog("http.request", Level.INFO)
        requestLog.message shouldBe "GET /test-endpoint"
        requestLog.getLogFields().shouldContainKeys(KEY_REQUEST_ID)
    }

    @Test
    fun `GET with generic error response`() {
        val (client, server) = setUpWebServer()
        server.enqueue(MockResponse().setResponseCode(HttpStatus.NOT_FOUND))

        val response = client.doCall<Unit>(HttpMethod.GET, "/test-endpoint")
        response shouldBe FailureResponse(HttpStatus.NOT_FOUND, null, null)

        val responseLog = verifyLog("http.response", Level.ERROR)
        responseLog.message shouldBe "Received 404 for GET /test-endpoint"
        responseLog.findLogField("responseCode") shouldBe 404
    }

    @Test
    fun `GET with structured error response`() {
        val (client, server) = setUpWebServer()
        val errorResponse = ClientErrorResponse(ClientErrorCode("oh.dear"), "a bid already exists")

        server.enqueue(
            MockResponse()
                .setBody(JsonObjectMapper().writeValue(errorResponse))
                .setResponseCode(HttpStatus.CONFLICT)
        )

        val response = client.doCall<Unit>(HttpMethod.GET, "/test-endpoint")
        response shouldBe
            FailureResponse(HttpStatus.CONFLICT, ClientErrorCode("oh.dear"), "a bid already exists")

        val responseLog = verifyLog("http.response", Level.ERROR)
        responseLog.message shouldBe "Received 409 for GET /test-endpoint"
        responseLog.findLogField("responseCode") shouldBe 409
        responseLog.findLogField("clientErrorCode") shouldBe ClientErrorCode("oh.dear")
        responseLog.findLogField("clientErrorMessage") shouldBe "a bid already exists"
    }

    @Test
    fun `SocketTimeout error handling`() {
        Unirest.config().socketTimeout(100)
        val (client) = setUpWebServer()

        client.doCall<Unit>(HttpMethod.GET, "/test-endpoint")

        val log = verifyLog("http.error", Level.ERROR)
        log.message shouldContain "SocketTimeoutException"
    }

    @Test
    fun `Should retry failed connections`() {
        val (client, server) = setUpWebServer()

        val disconnect = MockResponse().apply { socketPolicy = SocketPolicy.DISCONNECT_AT_START }
        val successResponse = MockResponse()
        server.enqueue(disconnect)
        server.enqueue(successResponse)

        val response = client.doCall<Unit>(HttpMethod.GET, "/test-endpoint")
        response shouldBe SuccessResponse(200, null)

        server.requestCount shouldBe 2
    }

    @Test
    fun `Successful POST with body`() {
        val (client, server) = setUpWebServer()
        val request = TestApiRequest(UUID.randomUUID())
        val expectedBody = JsonObjectMapper().writeValue(request)

        server.enqueue(MockResponse().setResponseCode(HttpStatus.NO_CONTENT))

        val response = client.doCall<Unit>(HttpMethod.POST, "/test-endpoint", request)
        response shouldBe SuccessResponse(HttpStatus.NO_CONTENT, null)

        val capturedRequest = server.takeRequest()
        capturedRequest.method shouldBe "POST"
        capturedRequest.path shouldBe "/root/test-endpoint"
        capturedRequest.getHeader("Content-Type") shouldBe "application/json; charset=utf-8"
        capturedRequest.body.readUtf8() shouldBe expectedBody

        val requestLog = verifyLog("http.request", Level.INFO)
        requestLog.message shouldBe "POST /test-endpoint"
        requestLog.getLogFields().shouldContainKeys(KEY_REQUEST_ID)
        requestLog.getLogFields().shouldContain("requestBody" to expectedBody)

        val responseLog = verifyLog("http.response", Level.INFO)
        responseLog.message shouldBe "Received 204 for POST /test-endpoint"
        responseLog.findLogField(KEY_REQUEST_ID) shouldBe requestLog.findLogField(KEY_REQUEST_ID)
        responseLog.findLogField("responseCode") shouldBe 204
        responseLog.findLogField("responseBody") shouldBe ""
    }

    @Test
    fun `Should handle additional fields`() {
        val (client, server) = setUpWebServer()
        val responseBody =
            """{
            "fieldOne": "foo",
            "fieldTwo": 500,
            "extraFieldThree": 10
        }"""

        server.enqueue(MockResponse().setBody(responseBody))

        val response = client.doCall<TestApiResponse>(HttpMethod.GET, "/test-endpoint")
        response shouldBe SuccessResponse(200, TestApiResponse("foo", 500))
    }

    private fun setUpWebServer(): Pair<HttpClient, MockWebServer> {
        val server = MockWebServer()
        server.start()

        val client = HttpClient(server.url("root").toString())

        return client to server
    }
}

private data class TestApiRequest(val userId: UUID)

private data class TestApiResponse(val fieldOne: String, val fieldTwo: Int)
