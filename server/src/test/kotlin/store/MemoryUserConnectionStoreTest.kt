package store

import auth.UserConnection

class MemoryUserConnectionStoreTest : AbstractStoreTest<String, UserConnection>() {
    override fun makeStore() = MemoryUserConnectionStore()

    override fun makeIdA() = "Alyssa"

    override fun makeIdB() = "Leah"

    override fun makeItemA(id: String) = UserConnection(id)

    override fun makeItemB(id: String) = UserConnection(id)
}
