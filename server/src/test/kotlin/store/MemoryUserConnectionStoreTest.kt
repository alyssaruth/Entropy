package store

import auth.UserConnection
import io.mockk.mockk
import javax.crypto.SecretKey

class MemoryUserConnectionStoreTest : AbstractStoreTest<UserConnection>() {

    private val secretKeyA = mockk<SecretKey>()
    private val secretKeyB = mockk<SecretKey>()

    override fun makeStore() = MemoryUserConnectionStore()

    override fun makeItemA() = UserConnection("1.2.3.A", secretKeyA, "Alyssa")

    override fun makeItemB() = UserConnection("1.2.3.B", secretKeyB, "Leah")
}
