package routes.session

import http.Routes
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Test
import testCore.shouldMatchJson
import util.ApplicationTest
import util.OnlineConstants
import util.ServerGlobals.sessionStore

class SessionControllerTest : ApplicationTest() {
    @Test
    fun `Should respond to a begin session request`() = testApplication {
        val response = client.post(Routes.BEGIN_SESSION, ::buildRequest)
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

    @Test fun `Should reject an update achievement call without a session`() = testApplication {}

    private fun buildRequest(builder: HttpRequestBuilder) {
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
