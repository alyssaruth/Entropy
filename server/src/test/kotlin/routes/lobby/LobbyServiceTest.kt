package routes.lobby

import auth.Session
import auth.UserConnection
import http.dto.OnlineUser
import http.dto.RoomSummary
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.mockk.clearMocks
import io.mockk.mockk
import io.mockk.verify
import java.util.*
import org.junit.jupiter.api.Test
import server.EntropyServer
import store.MemoryUserConnectionStore
import store.SessionStore
import store.Store
import store.UserConnectionStore
import testCore.AbstractTest
import testCore.verifyNotCalled
import util.XmlConstants
import util.makeGameSettings
import util.makeSession

class LobbyServiceTest : AbstractTest() {
    @Test
    fun `getLobby should return rooms and online users`() {
        val (service, server, sessionStore) = makeService()

        val sessionA = makeSession(name = "Alyssa")
        val sessionB = makeSession(name = "Bob")
        sessionStore.putAll(sessionA, sessionB)

        val settings = makeGameSettings(includeMoons = true)
        val room = server.registerNewRoom("Carbon 1", 4, settings)
        room.addToCurrentPlayers("Alyssa", 0)
        val copy = server.registerCopy(room)
        copy.addToObservers("Bob")

        val lobby = service.getLobby()
        lobby.users.shouldContainExactlyInAnyOrder(OnlineUser("Alyssa"), OnlineUser("Bob"))
        lobby.rooms.shouldContainExactlyInAnyOrder(
            RoomSummary("Carbon 1", settings, 4, 1, 0),
            RoomSummary("Carbon 2", settings, 4, 0, 1)
        )
    }

    @Test
    fun `Should do nothing if there is no one online`() {
        val server = mockk<EntropyServer>(relaxed = true)
        val (service) = makeService(server = server)
        service.lobbyChanged()

        verifyNotCalled { server.sendViaNotificationSocket(any(), any(), any()) }
    }

    @Test
    fun `Should do nothing if told to exclude the only user`() {
        val server = mockk<EntropyServer>(relaxed = true)
        val (service, _, _, uscStore) = makeService(server = server)
        val usc = UserConnection("1.2.3.4", "Alyssa")
        uscStore.put(usc)
        service.lobbyChanged(usc)

        verifyNotCalled { server.sendViaNotificationSocket(any(), any(), any()) }
    }

    @Test
    fun `Should notify the correct users`() {
        val server = mockk<EntropyServer>(relaxed = true)
        val (service, _, _, uscStore) = makeService(server = server)
        val uscA = UserConnection("1.2.3.4", "Alyssa")
        val uscB = UserConnection("5.6.7.8", "Bob")
        val uscC = UserConnection("0.0.0.0", "Cat")
        uscStore.putAll(uscA, uscB, uscC)

        service.lobbyChanged(uscA)
        verify {
            server.sendViaNotificationSocket(
                listOf(uscB, uscC),
                any(),
                XmlConstants.SOCKET_NAME_LOBBY
            )
        }

        clearMocks(server)
        service.lobbyChanged()
        verify {
            server.sendViaNotificationSocket(
                listOf(uscA, uscB, uscC),
                any(),
                XmlConstants.SOCKET_NAME_LOBBY
            )
        }
    }

    private data class SetupParams(
        val service: LobbyService,
        val server: EntropyServer,
        val sessionStore: Store<UUID, Session>,
        val uscStore: UserConnectionStore
    )

    private fun makeService(server: EntropyServer = EntropyServer()): SetupParams {
        val store = SessionStore()
        val uscStore = MemoryUserConnectionStore()
        return SetupParams(LobbyService(server, store, uscStore), server, store, uscStore)
    }
}
