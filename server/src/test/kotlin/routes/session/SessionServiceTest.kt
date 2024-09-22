package routes.session

import http.EMPTY_NAME
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
import store.SessionStore
import testCore.AbstractTest
import util.OnlineConstants

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
    fun `Should reject a blank name`() {
        val (service, store) = makeService()

        val ex = shouldThrow<ClientException> { service.beginSession(BeginSessionRequest("")) }
        ex.errorCode shouldBe EMPTY_NAME
        ex.statusCode shouldBe HttpStatusCode.BadRequest

        store.getAll().shouldBeEmpty()
    }

    @Test
    fun `Should create a session and respond with the ID`() {
        val request = BeginSessionRequest("Alyssa")
        val (service, store) = makeService()

        val response = service.beginSession(request)
        response.name shouldBe "Alyssa"

        val session = store.getAll().only()
        session.id shouldBe response.sessionId
    }

    @Test
    fun `Should handle multiple requests for the same name`() {
        val request = BeginSessionRequest("Alyssa")
        val (service, store) = makeService()

        val responseOne = service.beginSession(request)
        responseOne.name shouldBe "Alyssa"

        val responseTwo = service.beginSession(request)
        responseTwo.name shouldBe "Alyssa 2"

        val responseThree = service.beginSession(request)
        responseThree.name shouldBe "Alyssa 3"

        val sessions = store.getAll()
        sessions.size shouldBe 3

        val names = sessions.map { it.name }
        names.shouldContainExactlyInAnyOrder("Alyssa", "Alyssa 2", "Alyssa 3")
    }

    private fun makeService(): Pair<SessionService, SessionStore> {
        val store = SessionStore()
        return SessionService(store) to store
    }
}
