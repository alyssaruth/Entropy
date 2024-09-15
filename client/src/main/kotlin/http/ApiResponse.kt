package http

import kong.unirest.UnirestException

interface ApiResponse<T>

data class SuccessResponse<T>(val statusCode: Int, val body: T) : ApiResponse<T>

data class FailureResponse<T>(
    val statusCode: Int,
    val errorCode: String?,
    val errorMessage: String?
) : ApiResponse<T>

data class CommunicationError<T>(val unirestException: UnirestException) : ApiResponse<T>
