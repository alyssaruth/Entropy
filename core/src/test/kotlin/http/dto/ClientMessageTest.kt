package http.dto

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import utils.CoreGlobals

class ClientMessageTest : AbstractTest() {
    @Test
    fun `Should be able to deserialize based on messageType`() {
        val message = LobbyMessage(emptyList(), listOf(OnlineUser("Alyssa", 4)))

        val json = CoreGlobals.jsonMapper.writeValueAsString(message)
        val deserialized = CoreGlobals.jsonMapper.readValue(json, ClientMessage::class.java)
        deserialized shouldBe message
    }
}
