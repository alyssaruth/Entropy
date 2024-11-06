package util

import org.junit.jupiter.api.BeforeEach
import routes.lobby.LobbyService
import routes.session.SessionService
import store.MemoryUserConnectionStore
import store.SessionStore
import testCore.AbstractTest
import util.ServerGlobals.sessionStore
import util.ServerGlobals.uscStore

/** Clear down stores between tests. Can be replaced with proper DI once legacy code is gone */
abstract class ApplicationTest : AbstractTest() {
    @BeforeEach
    fun beforeEach() {
        sessionStore = SessionStore()
        uscStore = MemoryUserConnectionStore()
        ServerGlobals.lobbyService = LobbyService(ServerGlobals.server, sessionStore, uscStore)
        ServerGlobals.sessionService = SessionService(sessionStore, uscStore)
    }
}
