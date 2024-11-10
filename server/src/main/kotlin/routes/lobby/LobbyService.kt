package routes.lobby

import auth.Session
import auth.UserConnection
import http.dto.LobbyMessage
import http.dto.OnlineUser
import http.dto.RoomSummary
import java.util.*
import room.Room
import server.EntropyServer
import store.RoomStore
import store.Store
import store.UserConnectionStore
import util.XmlConstants
import utils.CoreGlobals

class LobbyService(
    private val server: EntropyServer,
    private val sessionStore: Store<UUID, Session>,
    private val uscStore: UserConnectionStore,
    private val roomStore: RoomStore,
) {
    fun getLobby(): LobbyMessage {
        val rooms = roomStore.getAll().map { it.toSummary() }
        val users = sessionStore.getAll().map { OnlineUser(it.name, it.achievementCount) }
        return LobbyMessage(rooms, users)
    }

    @JvmOverloads
    fun lobbyChanged(uscToExclude: UserConnection? = null) {
        val usersToNotify = uscStore.getAll().filterNot { it == uscToExclude }
        if (usersToNotify.isEmpty()) {
            return
        }

        val lobbyMessage = getLobby()
        server.sendViaNotificationSocket(
            usersToNotify,
            CoreGlobals.jsonMapper.writeValueAsString(lobbyMessage),
            XmlConstants.SOCKET_NAME_LOBBY,
        )
    }

    private fun Room.toSummary() =
        RoomSummary(name, settings, capacity, currentPlayerCount, observerCount)
}
