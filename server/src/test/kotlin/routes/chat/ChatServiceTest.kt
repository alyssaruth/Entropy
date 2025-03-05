package routes.chat

import auth.UserConnection
import http.dto.NewChatMessage
import http.dto.NewChatRequest
import http.dto.OnlineMessage
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import routes.ClientException
import server.EntropyServer
import store.MemoryUserConnectionStore
import store.RoomStore
import store.UserConnectionStore
import testCore.AbstractTest
import util.XmlConstants
import util.makeRoom
import util.makeSession
import util.makeUserConnection
import utils.CoreGlobals.jsonMapper

class ChatServiceTest : AbstractTest() {
    @Test
    fun `Should accept a lobby message and notify all users`() {
        val server = mockk<EntropyServer>(relaxed = true)
        val (service, uscStore) = makeService(server)

        val session = makeSession()

        val myUsc = makeUserConnection(session)
        val uscA = UserConnection("Bob")
        val uscB = UserConnection("Clive")
        uscStore.putAll(myUsc, uscA, uscB)

        val request = NewChatRequest("bye", null)
        service.receiveChat(request, session)

        val chatMessage =
            NewChatMessage(null, OnlineMessage(myUsc.colour, myUsc.name, request.message))

        verify {
            server.sendViaNotificationSocket(
                listOf(myUsc, uscA, uscB),
                jsonMapper.writeValueAsString(chatMessage),
                XmlConstants.SOCKET_NAME_CHAT,
            )
        }
    }

    @Test
    fun `Should throw a client exception if no room exists for the specified name`() {
        val session = makeSession()
        val request = NewChatRequest("breaking things", "Bad Room")

        val ex = shouldThrow<ClientException> { makeService().first.receiveChat(request, session) }
        ex.statusCode shouldBe HttpStatusCode.BadRequest
        ex.message shouldBe "No room exists for name Bad Room"
    }

    @Test
    fun `Should process a chat message for a room, and notify others in that room`() {
        val server = mockk<EntropyServer>(relaxed = true)
        val (service, uscStore, roomStore) = makeService(server)

        val sessions = listOf("Alyssa", "Mark", "David").map { makeSession(name = it) }
        val (myUsc, markUsc, davidUsc) = sessions.map(::makeUserConnection)
        uscStore.putAll(myUsc, markUsc, davidUsc)

        val room = makeRoom()
        room.addToObservers("Alyssa")
        room.addToObservers("Mark")
        roomStore.put(room)

        val request = NewChatRequest("Wat", room.name)
        service.receiveChat(request, sessions.first())

        val expectedMessage = OnlineMessage("gray", myUsc.name, request.message)
        val expectedChatMessage = NewChatMessage(room.name, expectedMessage)

        room.chatHistory.shouldContainExactly(expectedMessage)
        verify {
            server.sendViaNotificationSocket(
                listOf(myUsc, markUsc),
                jsonMapper.writeValueAsString(expectedChatMessage),
                XmlConstants.SOCKET_NAME_CHAT,
            )
        }
    }

    private fun makeService(
        server: EntropyServer = mockk(relaxed = true)
    ): Triple<ChatService, UserConnectionStore, RoomStore> {
        val roomStore = RoomStore()
        val uscStore = MemoryUserConnectionStore()
        val service = ChatService(server, roomStore, uscStore)
        return Triple(service, uscStore, roomStore)
    }
}
