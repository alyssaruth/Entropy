package http

import dto.ClientErrorResponse
import java.util.*
import kong.unirest.HttpMethod
import kong.unirest.HttpResponse
import kong.unirest.JsonObjectMapper
import kong.unirest.Unirest
import logging.Severity
import util.Globals.baseUrl
import utils.InjectedThings.logger

class HttpClient {
    val jsonObjectMapper = JsonObjectMapper()

    inline fun <reified T : Any?> doCall(
        method: HttpMethod,
        route: String,
        payload: Any? = null,
    ): ApiResponse<T> {
        val requestId = UUID.randomUUID()
        val requestJson = payload?.let { jsonObjectMapper.writeValue(payload) }

        logger.info(
            "http.request",
            "$method $route",
            "requestId" to requestId,
            "requestBody" to requestJson,
        )

        val request = Unirest.request(method.toString(), "${baseUrl}${route}")
        payload?.let { request.body(requestJson) }
        val response = request.asString()

        if (response.isSuccess) {
            logResponse(Severity.INFO, requestId, route, method, requestJson, response)
            val body = jsonObjectMapper.readValue(response.body, T::class.java)
            return SuccessResponse(response.status, body)
        } else {
            val errorResponse = tryParseErrorResponse(response)
            logResponse(
                Severity.ERROR,
                requestId,
                route,
                method,
                requestJson,
                response,
                errorResponse,
            )
            return FailureResponse(
                response.status,
                errorResponse?.errorCode,
                errorResponse?.errorMessage,
            )
        }
    }

    fun tryParseErrorResponse(response: HttpResponse<String>) =
        try {
            jsonObjectMapper.readValue(response.body, ClientErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }

    fun logResponse(
        level: Severity,
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
                "requestId" to requestId,
                "requestBody" to requestJson,
                "responseCode" to response.status,
                "responseBody" to response.body?.toString(),
                "clientErrorCode" to errorResponse?.errorCode,
                "clientErrorMessage" to errorResponse?.errorMessage,
            ),
        )
    }
}
