package http

import kong.unirest.UnirestException

sealed interface ApiResponse<T>

data class SuccessResponse<T>(val statusCode: Int, val body: T) : ApiResponse<T>

data class FailureResponse<T>(
    val statusCode: Int,
    val body: String,
    val errorCode: ClientErrorCode?,
    val errorMessage: String?
) : ApiResponse<T>

data class CommunicationError<T>(val unirestException: UnirestException) : ApiResponse<T>
