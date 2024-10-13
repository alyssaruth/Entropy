package http.dto

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class ClientMessageTest : AbstractTest() {
    @Test
    fun `Should be able to deserialize based on messageType`() {
        val message = LobbyMessage(emptyList(), listOf(OnlineUser("Alyssa")))

        val mapper = JsonMapper().registerKotlinModule()
        val json = mapper.writeValueAsString(message)

        val deserialized = mapper.readValue(json, ClientMessage::class.java)
        deserialized shouldBe message
    }
}
