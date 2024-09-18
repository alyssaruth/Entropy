package http

import dto.ClientErrorResponse
import java.util.*
import kong.unirest.HttpMethod
import kong.unirest.HttpResponse
import kong.unirest.JsonObjectMapper
import kong.unirest.Unirest
import kong.unirest.UnirestException
import logging.Severity
import org.apache.http.HttpHeaders
import utils.InjectedThings.logger

class HttpClient(val baseUrl: String) {
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

        val baseRequest =
            Unirest.request(method.toString(), "${baseUrl}${route}")
                .header("X-Request-ID", requestId.toString())

        val request =
            if (requestJson != null)
                baseRequest
                    .header(HttpHeaders.CONTENT_TYPE, "application/json; charset=utf-8")
                    .body(requestJson)
            else baseRequest

        try {
            val response = request.asString()
            return handleResponse(response, requestId, route, method, requestJson)
        } catch (e: UnirestException) {
            logUnirestError(requestId, route, method, requestJson, e)
            return CommunicationError(e)
        }
    }

    inline fun <reified T : Any?> handleResponse(
        response: HttpResponse<String>,
        requestId: UUID,
        route: String,
        method: HttpMethod,
        requestJson: String?
    ): ApiResponse<T> =
        if (response.isSuccess) {
            logResponse(Severity.INFO, requestId, route, method, requestJson, response)
            val body = jsonObjectMapper.readValue(response.body, T::class.java)
            SuccessResponse(response.status, body)
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
            FailureResponse(
                response.status,
                errorResponse?.errorCode,
                errorResponse?.errorMessage,
            )
        }

    fun tryParseErrorResponse(response: HttpResponse<String>) =
        try {
            jsonObjectMapper.readValue(response.body, ClientErrorResponse::class.java)
        } catch (e: Exception) {
            null
        }

    fun logUnirestError(
        requestId: UUID,
        route: String,
        method: HttpMethod,
        requestJson: String?,
        e: UnirestException
    ) {
        logger.error(
            "http.error",
            "Caught ${e.message} for $method $route",
            e,
            "requestId" to requestId,
            "requestBody" to requestJson,
            "unirestError" to e.message
        )
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
