package routes.session

import auth.Session
import auth.UserConnection
import http.EMPTY_NAME
import http.INVALID_ACHIEVEMENT_COUNT
import http.INVALID_API_VERSION
import http.UPDATE_REQUIRED
import http.dto.BeginSessionRequest
import http.dto.BeginSessionResponse
import io.ktor.http.*
import java.util.*
import room.Room
import routes.ClientException
import store.Store
import util.OnlineConstants
import util.ServerGlobals
import utils.Achievement
import utils.CoreGlobals.logger

class SessionService(
    private val sessionStore: Store<UUID, Session>,
    private val uscStore: Store<String, UserConnection>,
) {
    fun beginSession(request: BeginSessionRequest, ip: String): BeginSessionResponse {
        if (request.apiVersion > OnlineConstants.API_VERSION) {
            throw ClientException(
                HttpStatusCode.BadRequest,
                INVALID_API_VERSION,
                "API Version is too high: ${request.apiVersion} > ${OnlineConstants.API_VERSION}",
            )
        }

        if (request.apiVersion < OnlineConstants.API_VERSION) {
            throw ClientException(
                HttpStatusCode.BadRequest,
                UPDATE_REQUIRED,
                "API Version is out of date: ${request.apiVersion} < ${OnlineConstants.API_VERSION}",
            )
        }

        if (request.name.isEmpty()) {
            throw ClientException(HttpStatusCode.BadRequest, EMPTY_NAME, "Name cannot be empty")
        }

        validateAchievementCount(request.achievementCount)

        val sessionId = UUID.randomUUID()
        val currentNames = sessionStore.getAll().map { it.name }
        val name = ensureUnique(request.name, currentNames)
        val session =
            Session(sessionId, name, ip, request.achievementCount, request.apiVersion).also {
                sessionStore.put(it)
            }

        // Also populate legacy user connection
        val usc = UserConnection(name)
        usc.setLastActiveNow()
        uscStore.put(usc)
        ServerGlobals.lobbyService.lobbyChanged(usc)

        return BeginSessionResponse(session.name, session.id, ServerGlobals.lobbyService.getLobby())
    }

    private tailrec fun ensureUnique(
        requestedName: String,
        currentNames: List<String>,
        suffix: Int = 1,
    ): String {
        val nameToCheck = if (suffix > 1) "$requestedName $suffix" else requestedName

        return if (currentNames.contains(nameToCheck))
            return ensureUnique(requestedName, currentNames, suffix + 1)
        else nameToCheck
    }

    fun finishSession(session: Session) {
        val usc = uscStore.get(session.name)
        usc.destroyNotificationSockets()

        val rooms: List<Room> = ServerGlobals.roomStore.getAll()
        rooms.forEach { room ->
            room.removeFromObservers(usc.name)
            room.removePlayer(usc.name, false)
        }

        uscStore.remove(session.name)
        sessionStore.remove(session.id)

        logger.info("finishSession", "Session ended for ${usc.name}")

        if (sessionStore.count() == 0) {
            ServerGlobals.server.resetLobby()
        }

        ServerGlobals.lobbyService.lobbyChanged()
    }

    fun updateAchievementCount(session: Session, achievementCount: Int) {
        validateAchievementCount(achievementCount)

        sessionStore.update(session.id) { it.copy(achievementCount = achievementCount) }

        ServerGlobals.lobbyService.lobbyChanged()
    }

    private fun validateAchievementCount(achievementCount: Int) {
        if (achievementCount < 0) {
            throw ClientException(
                HttpStatusCode.BadRequest,
                INVALID_ACHIEVEMENT_COUNT,
                "Achievement count cannot be negative!",
            )
        }

        if (achievementCount > Achievement.entries.size) {
            throw ClientException(
                HttpStatusCode.BadRequest,
                INVALID_ACHIEVEMENT_COUNT,
                "Achievement count cannot be greater than ${Achievement.entries.size}",
            )
        }
    }
}
