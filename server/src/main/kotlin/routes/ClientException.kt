package routes

import http.ClientErrorCode
import io.ktor.http.HttpStatusCode

class ClientException(
    val statusCode: HttpStatusCode,
    val errorCode: ClientErrorCode,
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message)
