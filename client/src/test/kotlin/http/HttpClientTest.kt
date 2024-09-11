package http

import dto.ClientErrorResponse
import io.kotest.matchers.maps.shouldContain
import io.kotest.matchers.maps.shouldContainKeys
import io.kotest.matchers.shouldBe
import java.util.UUID
import kong.unirest.HttpMethod
import kong.unirest.HttpStatus
import kong.unirest.JsonObjectMapper
import kong.unirest.MockClient
import kong.unirest.MockResponse
import logging.Severity
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import util.Globals

class HttpClientTest : AbstractTest() {
    @Test
    fun `Successful GET request`() {
        val mockClient = MockClient.register()

        mockClient
            .expect(HttpMethod.GET, "${Globals.baseUrl}/test-endpoint")
            .thenReturn(
                """{
            "fieldOne": "foo",
            "fieldTwo": 500
        }"""
                    .trimIndent()
            )

        val client = HttpClient()
        val response = client.doCall<TestApiResponse>(HttpMethod.GET, "/test-endpoint")
        response shouldBe SuccessResponse(200, TestApiResponse("foo", 500))

        val requestLog = verifyLog("http.request", Severity.INFO)
        requestLog.message shouldBe "GET /test-endpoint"
        requestLog.keyValuePairs.shouldContainKeys("requestId")
    }

    @Test
    fun `GET with error response`() {
        val mockClient = MockClient.register()
        val errorResponse = ClientErrorResponse("oh.dear", "a bid already exists")

        mockClient
            .expect(HttpMethod.GET, "${Globals.baseUrl}/test-endpoint")
            .thenReturn(
                MockResponse(
                    HttpStatus.CONFLICT,
                    "Conflict",
                    JsonObjectMapper().writeValue(errorResponse),
                )
            )

        val client = HttpClient()
        val response = client.doCall<Unit>(HttpMethod.GET, "/test-endpoint")
        response shouldBe FailureResponse(HttpStatus.CONFLICT, "oh.dear", "a bid already exists")

        val responseLog = verifyLog("http.response", Severity.ERROR)
        responseLog.message shouldBe "Received 409 for GET /test-endpoint"
        responseLog.keyValuePairs["responseCode"] shouldBe 409
        responseLog.keyValuePairs["clientErrorCode"] shouldBe "oh.dear"
        responseLog.keyValuePairs["clientErrorMessage"] shouldBe "a bid already exists"
    }

    @Test
    fun `Successful POST with body`() {
        val mockClient = MockClient.register()
        val request = TestApiRequest(UUID.randomUUID())
        val expectedBody = JsonObjectMapper().writeValue(request)

        mockClient
            .expect(HttpMethod.POST, "${Globals.baseUrl}/test-endpoint")
            .thenReturn(MockResponse(HttpStatus.NO_CONTENT, "No Content", null))

        val client = HttpClient()
        val response = client.doCall<Unit>(HttpMethod.POST, "/test-endpoint", request)
        response shouldBe SuccessResponse(204, null)

        val requestLog = verifyLog("http.request", Severity.INFO)
        requestLog.message shouldBe "POST /test-endpoint"
        requestLog.keyValuePairs.shouldContainKeys("requestId")
        requestLog.keyValuePairs.shouldContain("requestBody" to expectedBody)

        val responseLog = verifyLog("http.response", Severity.INFO)
        responseLog.message shouldBe "Received 204 for POST /test-endpoint"
        responseLog.keyValuePairs["requestId"] shouldBe requestLog.keyValuePairs["requestId"]
        responseLog.keyValuePairs["responseCode"] shouldBe 204
        responseLog.keyValuePairs["responseBody"] shouldBe "null"
    }
}

private data class TestApiRequest(val userId: UUID)

private data class TestApiResponse(val fieldOne: String, val fieldTwo: Int)
