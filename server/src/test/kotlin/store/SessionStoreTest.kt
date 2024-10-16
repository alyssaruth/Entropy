package store

import auth.Session
import java.util.*

class SessionStoreTest : AbstractStoreTest<UUID, Session>() {
    override fun makeStore() = SessionStore()

    override fun makeIdA(): UUID = UUID.fromString("e115f1ce-0021-4653-9888-ea330b07f3a8")

    override fun makeIdB(): UUID = UUID.fromString("7a10f6a0-03bc-4e95-a4cb-44b92582f016")

    override fun makeItemA(id: UUID) = Session(id, "Alyssa", "1.2.3.4", 5, 15)

    override fun makeItemB(id: UUID) = Session(id, "David", "5.6.7.8", 10, 16)
}
