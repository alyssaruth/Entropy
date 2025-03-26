package routes.room

import http.CustomHeader
import http.Routes
import io.kotest.matchers.shouldBe
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import java.util.UUID
import org.junit.jupiter.api.Test
import testCore.shouldMatchJson
import util.ApplicationTest
import util.ServerGlobals
import util.makeRoom
import util.makeSession

class RoomControllerTest : ApplicationTest() {
    @Test
    fun `Should reject a joinRoom call without a session`() = testApplication {
        val response = client.post(Routes.JOIN_ROOM, ::buildJoinRoomRequest)
        response.status shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun `Should be able to join a room`() = testApplication {
        val session = makeSession(name = "Windy Miller")
        ServerGlobals.sessionStore.put(session)

        val room = makeRoom()
        room.attemptToSitDown("Quaker Oat Granola", 1)
        ServerGlobals.roomStore.put(room)

        val response =
            client.post(Routes.JOIN_ROOM) { buildJoinRoomRequest(this, room.id, session.id) }
        response.status shouldBe HttpStatusCode.OK
        response.bodyAsText() shouldMatchJson
            """
            {
                "chatHistory": [],
                "players": { "1": "Quaker Oat Granola" },
                "formerPlayers": {}
            }
        """

        room.observerCount shouldBe 1
    }

    private fun buildJoinRoomRequest(
        builder: HttpRequestBuilder,
        roomId: UUID = UUID.randomUUID(),
        sessionId: UUID? = null,
    ) {
        builder.contentType(ContentType.Application.Json)
        sessionId?.let { builder.header(CustomHeader.SESSION_ID, sessionId) }
        builder.setBody(
            """
                {
                    "roomId": "$roomId"
                }
            """
                .trimIndent()
        )
    }

    @Test
    fun `Should reject a sitDown call without a session`() = testApplication {
        val response = client.post(Routes.SIT_DOWN, ::buildSitDownRequest)
        response.status shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun `Should be able to sit down`() = testApplication {
        val session = makeSession(name = "Windy Miller")
        ServerGlobals.sessionStore.put(session)

        val room = makeRoom()
        room.attemptToSitDown("Quaker Oat Granola", 1)
        ServerGlobals.roomStore.put(room)

        val response =
            client.post(Routes.SIT_DOWN) { buildSitDownRequest(this, room.id, 2, session.id) }
        response.status shouldBe HttpStatusCode.OK
        response.bodyAsText() shouldMatchJson
            """
            {
                "players": { "1": "Quaker Oat Granola", "2": "Windy Miller" },
                "formerPlayers": {}
            }
        """
    }

    private fun buildSitDownRequest(
        builder: HttpRequestBuilder,
        roomId: UUID = UUID.randomUUID(),
        playerNumber: Int = 1,
        sessionId: UUID? = null,
    ) {
        builder.contentType(ContentType.Application.Json)
        sessionId?.let { builder.header(CustomHeader.SESSION_ID, sessionId) }
        builder.setBody(
            """
                {
                    "roomId": "$roomId",
                    "playerNumber": $playerNumber
                }
            """
                .trimIndent()
        )
    }

    @Test
    fun `Should reject a standUp call without a session`() = testApplication {
        val response = client.post(Routes.STAND_UP, ::buildStandUpRequest)
        response.status shouldBe HttpStatusCode.Unauthorized
    }

    @Test
    fun `Should be able to stand up`() = testApplication {
        val session = makeSession(name = "Alyssa")
        ServerGlobals.sessionStore.put(session)

        val room = makeRoom()
        room.attemptToSitDown("Alyssa", 1)
        room.attemptToSitDown("Leah", 2)
        ServerGlobals.roomStore.put(room)

        val response =
            client.post(Routes.STAND_UP) { buildStandUpRequest(this, room.id, session.id) }
        response.status shouldBe HttpStatusCode.OK
        response.bodyAsText() shouldMatchJson
            """
            {
                "players": { "2": "Leah" },
                "formerPlayers": {}
            }
        """
    }

    private fun buildStandUpRequest(
        builder: HttpRequestBuilder,
        roomId: UUID = UUID.randomUUID(),
        sessionId: UUID? = null,
    ) {
        builder.contentType(ContentType.Application.Json)
        sessionId?.let { builder.header(CustomHeader.SESSION_ID, sessionId) }
        builder.setBody(
            """
                {
                    "roomId": "$roomId"
                }
            """
                .trimIndent()
        )
    }
}
