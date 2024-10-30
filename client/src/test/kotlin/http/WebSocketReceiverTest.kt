package http

import http.dto.LobbyMessage
import http.dto.OnlineUser
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import online.screen.EntropyLobby
import org.junit.jupiter.api.Test
import screen.ScreenCache
import util.AbstractClientTest
import util.put
import utils.CoreGlobals

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
            CoreGlobals.jsonMapper.writeValueAsString(LobbyMessage(emptyList(), emptyList()))
        ) shouldBe true
    }

    @Test
    fun `Should refresh lobby on update`() {
        val lobby = mockk<EntropyLobby>(relaxed = true)
        ScreenCache.put(lobby)

        val receiver = WebSocketReceiver()
        val lobbyMessage = LobbyMessage(emptyList(), listOf(OnlineUser("Alyssa", 5)))
        receiver.receiveMessage(CoreGlobals.jsonMapper.writeValueAsString(lobbyMessage))

        verify { lobby.syncLobby(lobbyMessage) }
    }
}
