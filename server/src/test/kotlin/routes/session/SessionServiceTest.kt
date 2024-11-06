package routes.session

import http.EMPTY_NAME
import http.INVALID_ACHIEVEMENT_COUNT
import http.INVALID_API_VERSION
import http.UPDATE_REQUIRED
import http.dto.BeginSessionRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.ktor.http.*
import org.junit.jupiter.api.Test
import routes.ClientException
import store.MemoryUserConnectionStore
import store.SessionStore
import store.UserConnectionStore
import testCore.AbstractTest
import testCore.only
import util.OnlineConstants
import util.makeSession
import util.makeUserConnection
import utils.Achievement

class SessionServiceTest : AbstractTest() {
    @Test
    fun `Should reject futuristic API versions`() {
        val request = makeBeginSessionRequest(apiVersion = OnlineConstants.API_VERSION + 1)
        val (service) = makeService()

        val ex = shouldThrow<ClientException> { service.beginSession(request, "1.2.3.4") }

        ex.statusCode shouldBe HttpStatusCode.BadRequest
        ex.errorCode shouldBe INVALID_API_VERSION
    }

    @Test
    fun `Should reject old API versions`() {
        val request = makeBeginSessionRequest(apiVersion = OnlineConstants.API_VERSION - 1)
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
                service.beginSession(makeBeginSessionRequest(""), "1.2.3.4")
            }
        ex.errorCode shouldBe EMPTY_NAME
        ex.statusCode shouldBe HttpStatusCode.BadRequest

        store.getAll().shouldBeEmpty()
    }

    @Test
    fun `Should reject negative achievement count`() {
        val (service, store) = makeService()

        val ex =
            shouldThrow<ClientException> {
                service.beginSession(makeBeginSessionRequest(achievementCount = -1), "1.2.3.4")
            }
        ex.errorCode shouldBe INVALID_ACHIEVEMENT_COUNT
        ex.statusCode shouldBe HttpStatusCode.BadRequest

        store.getAll().shouldBeEmpty()
    }

    @Test
    fun `Should reject too high achievement count`() {
        val (service, store) = makeService()

        val ex =
            shouldThrow<ClientException> {
                service.beginSession(
                    makeBeginSessionRequest(achievementCount = Achievement.entries.size + 1),
                    "1.2.3.4",
                )
            }
        ex.errorCode shouldBe INVALID_ACHIEVEMENT_COUNT
        ex.statusCode shouldBe HttpStatusCode.BadRequest

        store.getAll().shouldBeEmpty()
    }

    @Test
    fun `Should create a session and respond with the ID`() {
        val request = makeBeginSessionRequest("Alyssa")
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
        val request = makeBeginSessionRequest("Alyssa")
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
        val request = makeBeginSessionRequest("Alyssa")
        val (service, _, uscStore) = makeService()

        val response = service.beginSession(request, "1.2.3.4")
        response.name shouldBe "Alyssa"

        val usc = uscStore.getAll().only()
        usc.name shouldBe "Alyssa"
        usc.ipAddress shouldBe "1.2.3.4"
    }

    @Test
    fun `Attempts to update achievement count should be validated`() {
        val session = makeSession()
        val (service, store) = makeService()
        store.put(session)

        shouldThrow<ClientException> { service.updateAchievementCount(session, -1) }
        shouldThrow<ClientException> {
            service.updateAchievementCount(session, Achievement.entries.size + 1)
        }

        store.get(session.id).achievementCount shouldBe session.achievementCount
    }

    @Test
    fun `Should support finishing a session`() {
        val sessionA = makeSession(ip = "1.2.3.4")
        val uscA = makeUserConnection(sessionA)

        val sessionB = makeSession(ip = "5.6.7.8")
        val uscB = makeUserConnection(sessionB)
        val (service, sessionStore, uscStore) = makeService()
        sessionStore.putAll(sessionA, sessionB)
        uscStore.putAll(uscA, uscB)

        service.finishSession(sessionA)

        sessionStore.getAll().shouldContainExactly(sessionB)
        uscStore.getAll().shouldContainExactly(uscB)
    }

    @Test
    fun `Should support updating achievement count`() {
        val session = makeSession(achievementCount = 5)
        val (service, store) = makeService()
        store.put(session)

        service.updateAchievementCount(session, 7)

        store.get(session.id) shouldBe session.copy(achievementCount = 7)
    }

    private fun makeBeginSessionRequest(
        name: String = "Alyssa",
        achievementCount: Int = 5,
        apiVersion: Int = OnlineConstants.API_VERSION,
    ) = BeginSessionRequest(name, achievementCount, apiVersion)

    private fun makeService(): Triple<SessionService, SessionStore, UserConnectionStore> {
        val store = SessionStore()
        val uscStore = MemoryUserConnectionStore()
        return Triple(SessionService(store, uscStore), store, uscStore)
    }
}
