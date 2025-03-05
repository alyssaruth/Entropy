package http

import com.github.alyssaburlton.swingtest.flushEdt
import com.github.alyssaburlton.swingtest.getChild
import getMessages
import http.dto.LobbyMessage
import http.dto.NewChatMessage
import http.dto.OnlineUser
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import online.screen.EntropyLobby
import online.screen.OnlineChatPanel
import org.junit.jupiter.api.Test
import screen.ScreenCache
import testCore.makeOnlineMessage
import testCore.makeRoomSummary
import util.AbstractClientTest
import util.put
import utils.CoreGlobals.jsonMapper

class WebSocketReceiverTest : AbstractClientTest() {
    @Test
    fun `Should report unable to handle nonsense`() {
        val receiver = WebSocketReceiver()
        receiver.canHandleMessage("<xml>something</xml>") shouldBe false
        receiver.canHandleMessage("foo") shouldBe false
        receiver.canHandleMessage("""{"messageType": "UNKNOWN"}""") shouldBe false
    }

    @Test
    fun `Should be able to handle intended messages`() {
        val receiver = WebSocketReceiver()
        receiver.canHandleMessage(
            jsonMapper.writeValueAsString(LobbyMessage(emptyList(), emptyList()))
        ) shouldBe true

        receiver.canHandleMessage(
            jsonMapper.writeValueAsString(NewChatMessage("hello", makeOnlineMessage()))
        ) shouldBe true
    }

    @Test
    fun `Should refresh lobby on update`() {
        val lobby = mockk<EntropyLobby>(relaxed = true)
        ScreenCache.put(lobby)

        val receiver = WebSocketReceiver()
        val lobbyMessage = LobbyMessage(emptyList(), listOf(OnlineUser("Alyssa", 5)))
        receiver.receiveMessage(jsonMapper.writeValueAsString(lobbyMessage))

        verify { lobby.syncLobby(lobbyMessage) }
    }

    @Test
    fun `Should update lobby chat if no roomName`() {
        val message = makeOnlineMessage()
        val receiver = WebSocketReceiver()
        val lobbyMessage = NewChatMessage(null, message)
        receiver.receiveMessage(jsonMapper.writeValueAsString(lobbyMessage))
        flushEdt()

        val chatPanel = ScreenCache.get<EntropyLobby>().getChild<OnlineChatPanel>()
        chatPanel.getMessages().shouldContainExactly(message)
    }

    @Test
    fun `Should update relevant room chat`() {
        val room = makeRoomSummary()
        val lobby = ScreenCache.get<EntropyLobby>()
        lobby.createGameRoom(room)

        val message = makeOnlineMessage()
        val receiver = WebSocketReceiver()
        val lobbyMessage = NewChatMessage(room.name, message)
        receiver.receiveMessage(jsonMapper.writeValueAsString(lobbyMessage))
        flushEdt()

        val chatPanel = lobby.getChild<OnlineChatPanel>()
        chatPanel.getMessages().shouldBeEmpty()

        val gameRoom = lobby.getGameRoomForName(room.name)!!
        val roomChatPanel = gameRoom.getChild<OnlineChatPanel>()
        roomChatPanel.getMessages().shouldContainExactly(message)
    }
}
