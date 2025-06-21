package routes.chat

import http.CustomHeader
import http.Routes
import http.dto.OnlineMessage
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.testing.*
import java.util.*
import org.junit.jupiter.api.Test
import util.ApplicationTest
import util.ServerGlobals
import util.makeRoom
import util.makeSession

class ChatControllerTest : ApplicationTest() {
    @Test
    fun `Should reject a new chat message with no session`() = testApplication {
        val response = client.put(Routes.CHAT, ::buildNewChatRequest)
        response.status shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun `Should accept a new chat message`() = testApplication {
        val session = makeSession()
        ServerGlobals.sessionStore.put(session)

        val room = makeRoom()
        ServerGlobals.roomStore.put(room)

        val response =
            client.put(Routes.CHAT) { buildNewChatRequest(this, roomName = room.name, session.id) }
        response.status shouldBe HttpStatusCode.Created

        room.chatHistory.shouldContainExactly(OnlineMessage("gray", session.name, "some text"))
    }

    private fun buildNewChatRequest(
        builder: HttpRequestBuilder,
        roomName: String? = null,
        sessionId: UUID? = null,
    ) {
        sessionId?.let { builder.header(CustomHeader.SESSION_ID, sessionId) }
        builder.contentType(ContentType.Application.Json)
        builder.setBody(
            """
                {
                    ${roomName?.let { """"roomName": "$roomName",""" }}
                    "message": "some text"
                }
            """
                .trimIndent()
        )
    }
}
