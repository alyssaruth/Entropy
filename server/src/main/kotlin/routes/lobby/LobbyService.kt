package routes.lobby

import auth.UserConnection
import game.GameMode
import game.GameSettings
import http.dto.LobbyResponse
import http.dto.OnlineUser
import http.dto.RoomSummary
import `object`.Room
import util.GameConstants
import util.ServerGlobals
import util.XmlConstants
import utils.CoreGlobals.objectMapper

class LobbyService {
    fun getLobby(): LobbyResponse {
        val rooms = ServerGlobals.server.rooms.map { it.toSummary() }
        val users = ServerGlobals.sessionStore.getAll().map { OnlineUser(it.name) }
        return LobbyResponse(rooms, users)
    }

    @JvmOverloads
    fun lobbyChanged(uscToExclude: UserConnection? = null) {
        val usersToNotify = ServerGlobals.uscStore.getAll().filterNot { it == uscToExclude }
        if (usersToNotify.isEmpty()) {
            return
        }

        val lobbyMessage = getLobby()
        ServerGlobals.server.sendViaNotificationSocket(
            usersToNotify,
            objectMapper.writeValueAsString(lobbyMessage),
            XmlConstants.SOCKET_NAME_LOBBY,
        )
    }

    private fun Room.toSummary() =
        RoomSummary(
            roomName,
            GameSettings(
                convertLegacyMode(mode),
                jokerQuantity,
                jokerValue,
                includeMoons,
                includeStars,
                negativeJacks,
                cardReveal,
                illegalAllowed
            ),
            players,
            currentPlayerCount,
            observerCount,
        )

    private fun convertLegacyMode(mode: Int): GameMode {
        return when (mode) {
            GameConstants.GAME_MODE_ENTROPY,
            GameConstants.GAME_MODE_ENTROPY_ONLINE -> GameMode.Entropy
            else -> GameMode.Vectropy
        }
    }
}
