package routes.session

import http.CustomHeader
import http.Routes
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.UUID
import org.junit.jupiter.api.Test
import testCore.shouldMatchJson
import util.ApplicationTest
import util.OnlineConstants
import util.ServerGlobals.sessionStore
import util.ServerGlobals.uscStore
import util.makeSession
import util.makeUserConnection

class SessionControllerTest : ApplicationTest() {
    @Test
    fun `Should respond to a begin session request`() = testApplication {
        val response = client.post(Routes.BEGIN_SESSION, ::buildSessionRequest)
        response.status shouldBe HttpStatusCode.OK

        sessionStore.getAll().shouldHaveSize(1)
        val session = sessionStore.getAll().first()
        session.name shouldBe "David"

        response.bodyAsText() shouldMatchJson
            """
            {
                "name": "David",
                "sessionId": "${session.id}"
            }
        """
                .trimIndent()
    }

    @Test
    fun `Should reject a finish session call without a session`() = testApplication {
        val response = client.post(Routes.FINISH_SESSION, ::buildFinishSessionRequest)
        response.status shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun `Should be able to finish a session`() = testApplication {
        val session = makeSession(achievementCount = 4)
        val usc = makeUserConnection(session)
        sessionStore.put(session)
        uscStore.put(usc)

        val response =
            client.post(Routes.FINISH_SESSION) { buildFinishSessionRequest(this, session.id) }
        response.status shouldBe HttpStatusCode.NoContent

        sessionStore.count() shouldBe 0
        uscStore.count() shouldBe 0
    }

    @Test
    fun `Should reject an update achievement call without a session`() = testApplication {
        val response = client.post(Routes.ACHIEVEMENT_COUNT, ::buildAchievementUpdateRequest)
        response.status shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun `Should be able to update achievement count`() = testApplication {
        val session = makeSession(achievementCount = 4)
        sessionStore.put(session)

        val response =
            client.post(Routes.ACHIEVEMENT_COUNT) {
                buildAchievementUpdateRequest(this, session.id)
            }
        response.status shouldBe HttpStatusCode.NoContent

        val updatedSession = sessionStore.get(session.id)
        updatedSession.achievementCount shouldBe 8
    }

    private fun buildFinishSessionRequest(builder: HttpRequestBuilder, sessionId: UUID? = null) {
        sessionId?.let { builder.header(CustomHeader.SESSION_ID, sessionId) }
    }

    private fun buildAchievementUpdateRequest(
        builder: HttpRequestBuilder,
        sessionId: UUID? = null,
    ) {
        builder.contentType(ContentType.Application.Json)
        sessionId?.let { builder.header(CustomHeader.SESSION_ID, sessionId) }
        builder.setBody(
            """
                {
                    "achievementCount": 8
                }
            """
                .trimIndent()
        )
    }

    private fun buildSessionRequest(builder: HttpRequestBuilder) {
        builder.contentType(ContentType.Application.Json)
        builder.setBody(
            """
                {
                    "name": "David",
                    "achievementCount": 5,
                    "apiVersion": "${OnlineConstants.API_VERSION}"
                }
            """
                .trimIndent()
        )
    }
}
