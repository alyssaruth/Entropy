package routes.session

import http.UPDATE_REQUIRED
import http.dto.BeginSessionRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import org.junit.jupiter.api.Test
import routes.ClientException
import store.SessionStore
import testCore.AbstractTest
import util.OnlineConstants
import utils.runInOtherThread

class SessionServiceTest : AbstractTest() {
    @Test
    fun `Should reject old API versions`() {
        val request = BeginSessionRequest("Alyssa", OnlineConstants.API_VERSION - 1)
        val (service) = makeService()

        val ex = shouldThrow<ClientException> { service.beginSession(request) }

        ex.statusCode shouldBe HttpStatusCode.BadRequest
        ex.errorCode shouldBe UPDATE_REQUIRED
    }

    @Test
    fun `Should handle concurrent requests for the same name`() {
        val request = BeginSessionRequest("Alyssa")
        val (service, store) = makeService()
        val requestCount = 20

        val threads = (1..requestCount).map { runInOtherThread { service.beginSession(request) } }
        threads.forEach(Thread::join)

        val sessions = store.getAll()
        sessions.size shouldBe requestCount

        val expectedNames = (1 ..< requestCount).map { "Alyssa $it" } + "Alyssa"
        val names = sessions.map { it.name }
        names.shouldContainExactlyInAnyOrder(expectedNames)
    }

    private fun makeService(): Pair<SessionService, SessionStore> {
        val store = SessionStore()
        return SessionService(store) to store
    }
}
