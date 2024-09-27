package routes.session

import auth.Session
import auth.UserConnection
import http.EMPTY_NAME
import http.INVALID_API_VERSION
import http.LegacyConstants
import http.UPDATE_REQUIRED
import http.dto.BeginSessionRequest
import http.dto.BeginSessionResponse
import io.ktor.http.*
import java.util.*
import routes.ClientException
import store.Store
import util.Globals
import util.OnlineConstants

class SessionService(
    private val sessionStore: Store<Session>,
    private val uscStore: Store<UserConnection>
) {
    fun beginSession(request: BeginSessionRequest, ip: String): BeginSessionResponse {
        if (request.apiVersion > OnlineConstants.API_VERSION) {
            throw ClientException(
                HttpStatusCode.BadRequest,
                INVALID_API_VERSION,
                "API Version is too high: ${request.apiVersion} > ${OnlineConstants.API_VERSION}"
            )
        }

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
        val session =
            Session(sessionId, name, ip, request.apiVersion).also {
                sessionStore.put(it.id.toString(), it)
            }

        // Also populate legacy user connection
        val usc = UserConnection(ip, LegacyConstants.SYMMETRIC_KEY, name)
        usc.setLastActiveNow()
        uscStore.put(ip, usc)
        Globals.server.lobbyChanged(usc)

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
