package store

import auth.UserConnection

class MemoryUserConnectionStoreTest : AbstractStoreTest<String, UserConnection>() {
    override fun makeStore() = MemoryUserConnectionStore()

    override fun makeIdA() = "1.2.3.A"

    override fun makeIdB() = "4.5.6.B"

    override fun makeItemA(id: String) = UserConnection(id, "Alyssa")

    override fun makeItemB(id: String) = UserConnection(id, "Leah")
}
