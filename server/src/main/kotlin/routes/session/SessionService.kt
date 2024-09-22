package routes.session

import auth.Session
import http.EMPTY_NAME
import http.UPDATE_REQUIRED
import http.dto.BeginSessionRequest
import http.dto.BeginSessionResponse
import io.ktor.http.*
import java.util.*
import routes.ClientException
import store.Store
import util.OnlineConstants

class SessionService(private val sessionStore: Store<Session>) {
    fun beginSession(request: BeginSessionRequest): BeginSessionResponse {
        if (request.apiVersion < OnlineConstants.API_VERSION) {
            throw ClientException(
                HttpStatusCode.BadRequest,
                UPDATE_REQUIRED,
                "API Version is out of date: ${request.apiVersion} < ${OnlineConstants.API_VERSION}"
            )
        }

        if (request.name.isEmpty()) {
            throw ClientException(HttpStatusCode.BadRequest, EMPTY_NAME, "Name cannot be empty")
        }

        val sessionId = UUID.randomUUID()
        val currentNames = sessionStore.getAll().map { it.name }
        val name = ensureUnique(request.name, currentNames)
        val session = Session(name, sessionId).also { sessionStore.put(it.id.toString(), it) }

        return BeginSessionResponse(session.name, session.id)
    }

    private tailrec fun ensureUnique(
        requestedName: String,
        currentNames: List<String>,
        suffix: Int = 1
    ): String {
        val nameToCheck = if (suffix > 1) "$requestedName $suffix" else requestedName

        return if (currentNames.contains(nameToCheck))
            return ensureUnique(requestedName, currentNames, suffix + 1)
        else nameToCheck
    }
}
