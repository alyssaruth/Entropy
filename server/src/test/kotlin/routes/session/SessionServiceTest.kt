package routes.session

import http.EMPTY_NAME
import http.INVALID_API_VERSION
import http.LegacyConstants
import http.UPDATE_REQUIRED
import http.dto.BeginSessionRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import main.kotlin.testCore.only
import org.junit.jupiter.api.Test
import routes.ClientException
import store.MemoryUserConnectionStore
import store.SessionStore
import store.UserConnectionStore
import testCore.AbstractTest
import util.OnlineConstants

class SessionServiceTest : AbstractTest() {
    @Test
    fun `Should reject futuristic API versions`() {
        val request = BeginSessionRequest("Alyssa", OnlineConstants.API_VERSION + 1)
        val (service) = makeService()

        val ex = shouldThrow<ClientException> { service.beginSession(request, "1.2.3.4") }

        ex.statusCode shouldBe HttpStatusCode.BadRequest
        ex.errorCode shouldBe INVALID_API_VERSION
    }

    @Test
    fun `Should reject old API versions`() {
        val request = BeginSessionRequest("Alyssa", OnlineConstants.API_VERSION - 1)
        val (service) = makeService()

        val ex = shouldThrow<ClientException> { service.beginSession(request, "1.2.3.4") }

        ex.statusCode shouldBe HttpStatusCode.BadRequest
        ex.errorCode shouldBe UPDATE_REQUIRED
    }

    @Test
    fun `Should reject a blank name`() {
        val (service, store) = makeService()

        val ex =
            shouldThrow<ClientException> {
                service.beginSession(BeginSessionRequest(""), "1.2.3.4")
            }
        ex.errorCode shouldBe EMPTY_NAME
        ex.statusCode shouldBe HttpStatusCode.BadRequest

        store.getAll().shouldBeEmpty()
    }

    @Test
    fun `Should create a session and respond with the ID`() {
        val request = BeginSessionRequest("Alyssa")
        val (service, store) = makeService()

        val response = service.beginSession(request, "1.2.3.4")
        response.name shouldBe "Alyssa"

        val session = store.getAll().only()
        session.id shouldBe response.sessionId
        session.ip shouldBe "1.2.3.4"
        session.apiVersion shouldBe OnlineConstants.API_VERSION
    }

    @Test
    fun `Should handle multiple requests for the same name`() {
        val request = BeginSessionRequest("Alyssa")
        val (service, store) = makeService()

        val responseOne = service.beginSession(request, "1.2.3.4")
        responseOne.name shouldBe "Alyssa"

        val responseTwo = service.beginSession(request, "1.2.3.4")
        responseTwo.name shouldBe "Alyssa 2"

        val responseThree = service.beginSession(request, "1.2.3.4")
        responseThree.name shouldBe "Alyssa 3"

        val sessions = store.getAll()
        sessions.size shouldBe 3

        val names = sessions.map { it.name }
        names.shouldContainExactlyInAnyOrder("Alyssa", "Alyssa 2", "Alyssa 3")
    }

    @Test
    fun `Should create a legacy user connection`() {
        val request = BeginSessionRequest("Alyssa")
        val (service, _, uscStore) = makeService()

        val response = service.beginSession(request, "1.2.3.4")
        response.name shouldBe "Alyssa"

        val usc = uscStore.getAll().only()
        usc.name shouldBe "Alyssa"
        usc.symmetricKey shouldBe LegacyConstants.SYMMETRIC_KEY
        usc.ipAddress shouldBe "1.2.3.4"
    }

    private fun makeService(): Triple<SessionService, SessionStore, UserConnectionStore> {
        val store = SessionStore()
        val uscStore = MemoryUserConnectionStore()
        return Triple(SessionService(store, uscStore), store, uscStore)
    }
}
