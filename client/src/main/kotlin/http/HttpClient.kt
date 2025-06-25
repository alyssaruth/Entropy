package http

import ch.qos.logback.classic.Level
import com.fasterxml.jackson.core.JsonProcessingException
import http.dto.ClientErrorResponse
import java.util.*
import kong.unirest.HttpMethod
import kong.unirest.HttpRequest
import kong.unirest.HttpRequestWithBody
import kong.unirest.HttpResponse
import kong.unirest.Unirest
import kong.unirest.UnirestException
import logging.KEY_REQUEST_ID
import org.apache.http.HttpHeaders
import utils.CoreGlobals.jsonMapper
import utils.CoreGlobals.logger

class HttpClient(private val baseUrl: String) {
    var sessionId: UUID? = null

    inline fun <reified T : Any> doCall(
        method: HttpMethod,
        route: String,
        payload: Any? = null,
    ): ApiResponse<T> {
        return doCall(method, route, payload, T::class.java)
    }

    fun <T : Any> doCall(
        method: HttpMethod,
        route: String,
        payload: Any? = null,
        responseType: Class<T>,
    ): ApiResponse<T> {
        val requestId = UUID.randomUUID()
        val requestJson = payload?.let { jsonMapper.writeValueAsString(payload) }

        logger.info(
            "http.request",
            "$method $route",
            KEY_REQUEST_ID to requestId,
            "requestBody" to requestJson,
        )

        val request =
            Unirest.request(method.toString(), "${baseUrl}${route}")
                .header("X-Request-ID", requestId.toString())
                .addBody(requestJson)
                .addSessionId()

        try {
            val response = request.asString()
            return handleResponse(response, requestId, route, method, requestJson, responseType)
        } catch (e: UnirestException) {
            logUnirestError(requestId, route, method, requestJson, e)
            return CommunicationError(e)
        }
    }

    private fun HttpRequestWithBody.addBody(requestJson: String?) =
        requestJson?.let {
            header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8").body(requestJson)
        } ?: this

    private fun HttpRequest<*>.addSessionId() =
        sessionId?.let { id -> header(CustomHeader.SESSION_ID, id.toString()) } ?: this

    private fun <T : Any> handleResponse(
        response: HttpResponse<String>,
        requestId: UUID,
        route: String,
        method: HttpMethod,
        requestJson: String?,
        responseType: Class<T>,
    ): ApiResponse<T> =
        if (response.isSuccess) {
            logResponse(Level.INFO, requestId, route, method, requestJson, response)
            try {
                val body = parseBody(response, responseType)
                SuccessResponse(response.status, body)
            } catch (e: JsonProcessingException) {
                logger.error(
                    "responseParseError",
                    "Failed to parse response",
                    e,
                    KEY_REQUEST_ID to requestId,
                    "requestBody" to requestJson,
                    "responseCode" to response.status,
                    "responseBody" to response.body?.toString(),
                )
                FailureResponse(response.status, response.body, JSON_PARSE_ERROR, e.message)
            }
        } else {
            val errorResponse = tryParseErrorResponse(response)
            logResponse(Level.ERROR, requestId, route, method, requestJson, response, errorResponse)
            FailureResponse(
                response.status,
                response.body,
                errorResponse?.errorCode,
                errorResponse?.errorMessage,
            )
        }

    private fun <T : Any> parseBody(response: HttpResponse<String>, responseType: Class<T>): T =
        if (responseType == Unit::class.java) {
            Unit as T
        } else {
            jsonMapper.readValue(response.body, responseType)
        }

    private fun tryParseErrorResponse(response: HttpResponse<String>) =
        try {
            jsonMapper.readValue(response.body, ClientErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }

    private fun logUnirestError(
        requestId: UUID,
        route: String,
        method: HttpMethod,
        requestJson: String?,
        e: UnirestException,
    ) {
        logger.error(
            "http.error",
            "Caught ${e.message} for $method $route",
            e,
            KEY_REQUEST_ID to requestId,
            "requestBody" to requestJson,
            "unirestError" to e.message,
        )
    }

    private fun logResponse(
        level: Level,
        requestId: UUID,
        route: String,
        method: HttpMethod,
        requestJson: String?,
        response: HttpResponse<String>,
        errorResponse: ClientErrorResponse? = null,
    ) {
        logger.log(
            level,
            "http.response",
            "Received ${response.status} for $method $route",
            null,
            mapOf(
                KEY_REQUEST_ID to requestId,
                "requestBody" to requestJson,
                "responseCode" to response.status,
                "responseBody" to response.body?.toString(),
                "clientErrorCode" to errorResponse?.errorCode,
                "clientErrorMessage" to errorResponse?.errorMessage,
            ),
        )
    }
}
