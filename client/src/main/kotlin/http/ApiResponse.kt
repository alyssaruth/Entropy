package http

interface ApiResponse<T> {
    val statusCode: Int
}

data class SuccessResponse<T>(override val statusCode: Int, val body: T) : ApiResponse<T>

data class FailureResponse<T>(
    override val statusCode: Int,
    val errorCode: String,
    val errorMessage: String
) : ApiResponse<T>
