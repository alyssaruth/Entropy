package store

import auth.Session
import java.util.*

class SessionStoreTest : AbstractStoreTest<Session>() {
    override fun makeStore() = SessionStore()

    override fun makeItemA() =
        Session(UUID.fromString("e115f1ce-0021-4653-9888-ea330b07f3a8"), "Alyssa", "1.2.3.4", 15)

    override fun makeItemB() =
        Session(UUID.fromString("7a10f6a0-03bc-4e95-a4cb-44b92582f016"), "David", "5.6.7.8", 16)
}
