package util

import org.junit.jupiter.api.BeforeEach
import store.MemoryUserConnectionStore
import store.SessionStore
import testCore.AbstractTest

/** Clear down stores between tests. Can be replaced with proper DI once legacy code is gone */
abstract class ApplicationTest : AbstractTest() {
    @BeforeEach
    fun beforeEach() {
        ServerGlobals.sessionStore = SessionStore()
        ServerGlobals.uscStore = MemoryUserConnectionStore()
    }
}
