package routes

import auth.Session
import http.CustomHeader
import http.INVALID_SESSION
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import java.util.*
import util.ServerGlobals

suspend fun requiresSession(call: ApplicationCall, fn: suspend (session: Session) -> Unit) {
    val sessionIdStr =
        call.request.header(CustomHeader.SESSION_ID)
            ?: throw ClientException(
                HttpStatusCode.Unauthorized,
                INVALID_SESSION,
                "Missing session id",
            )

    val sessionId =
        try {
            UUID.fromString(sessionIdStr)
        } catch (e: IllegalArgumentException) {
            throw ClientException(
                HttpStatusCode.BadRequest,
                INVALID_SESSION,
                "Session ID was not a valid UUID",
                e,
            )
        }

    val session =
        ServerGlobals.sessionStore.find(sessionId)
            ?: throw ClientException(
                HttpStatusCode.Unauthorized,
                INVALID_SESSION,
                "No session found for ID $sessionId",
            )

    fn(session)
}
