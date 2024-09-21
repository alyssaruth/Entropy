package routes.session

import auth.Session
import http.UPDATE_REQUIRED
import http.dto.BeginSessionRequest
import http.dto.BeginSessionResponse
import io.ktor.http.*
import java.util.*
import routes.ClientException
import store.Store
import util.OnlineConstants

class SessionService(private val sessionStore: Store<Session>) {
    private val nameSyncObject = Any()

    fun beginSession(request: BeginSessionRequest): BeginSessionResponse {
        if (request.apiVersion < OnlineConstants.API_VERSION) {
            throw ClientException(
                HttpStatusCode.BadRequest,
                UPDATE_REQUIRED,
                "API Version is out of date: ${request.apiVersion} < ${OnlineConstants.API_VERSION}"
            )
        }

        val session =
            synchronized(nameSyncObject) {
                val sessionId = UUID.randomUUID()
                val currentNames = sessionStore.getAll().map { it.name }
                val name = ensureUnique(request.name, currentNames)

                Session(name, sessionId).also { sessionStore.put(it.id.toString(), it) }
            }

        return BeginSessionResponse(session.name, session.id)
    }

    private tailrec fun ensureUnique(
        requestedName: String,
        currentNames: List<String>,
        suffix: Int = 0
    ): String {
        val nameToCheck = if (suffix > 0) "$requestedName $suffix" else requestedName

        return if (currentNames.contains(nameToCheck))
            return ensureUnique(requestedName, currentNames, suffix + 1)
        else nameToCheck
    }
}
