package routes.lobby

import auth.UserConnection
import http.dto.LobbyMessage
import http.dto.OnlineUser
import http.dto.RoomSummary
import `object`.Room
import util.ServerGlobals
import util.XmlConstants
import utils.CoreGlobals

class LobbyService {
    fun getLobby(): LobbyMessage {
        val rooms = ServerGlobals.server.rooms.map { it.toSummary() }
        val users = ServerGlobals.sessionStore.getAll().map { OnlineUser(it.name) }
        return LobbyMessage(rooms, users)
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
            CoreGlobals.jsonMapper.writeValueAsString(lobbyMessage),
            XmlConstants.SOCKET_NAME_LOBBY,
        )
    }

    private fun Room.toSummary() =
        RoomSummary(
            name,
            settings,
            capacity,
            currentPlayerCount,
            observerCount,
        )
}