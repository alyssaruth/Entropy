package http

import dto.ClientErrorResponse
import java.util.*
import kong.unirest.HttpResponse
import kong.unirest.JsonObjectMapper
import kong.unirest.Unirest
import logging.Severity
import util.Globals.baseUrl
import utils.InjectedThings.logger

class HttpClient {
    val jsonObjectMapper = JsonObjectMapper()

    inline fun <reified T : Any?> doCall(
        method: String,
        route: String,
        payload: Any? = null
    ): ApiResponse<T> {
        val requestId = UUID.randomUUID()
        val requestJson = payload?.let { jsonObjectMapper.writeValue(payload) }

        logger.info(
            "http.request",
            "$method $route",
            "payload" to payload,
            "requestId" to requestId,
            "requestBody" to requestJson
        )

        val request = Unirest.request(method, "${baseUrl}${route}")
        payload?.let { request.body(payload) }
        val response = request.asString()

        if (response.isSuccess) {
            logResponse(Severity.INFO, requestId, route, method, requestJson, response)
            val body = jsonObjectMapper.readValue(response.body, T::class.java)
            return SuccessResponse(response.status, body)
        } else {
            logResponse(Severity.ERROR, requestId, route, method, requestJson, response)
            val errorResponse =
                jsonObjectMapper.readValue(response.body, ClientErrorResponse::class.java)
            return FailureResponse(
                response.status,
                errorResponse.errorCode,
                errorResponse.errorMessage
            )
        }
    }

    fun logResponse(
        level: Severity,
        requestId: UUID,
        route: String,
        method: String,
        requestJson: String?,
        response: HttpResponse<String>
    ) {
        logger.log(
            level,
            "http.response",
            "Received ${response.status} for $method $route",
            null,
            mapOf(
                "requestId" to requestId,
                "requestBody" to requestJson,
                "responseCode" to response.status,
                "responseBody" to response.body.toString()
            )
        )
    }
}
