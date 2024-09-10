package routes

import io.ktor.http.HttpStatusCode

class ClientException(
    val statusCode: HttpStatusCode,
    val errorCode: String,
    override val message: String,
    override val cause: Throwable? = null
) : RuntimeException(message)
