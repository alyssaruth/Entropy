package game

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import utils.CoreGlobals

class IllegalActionTest : AbstractTest() {
    @Test
    fun `Should serialise and deserialise correctly`() {
        val action: PlayerAction = IllegalAction("Susan", false)
        val json = CoreGlobals.jsonMapper.writeValueAsString(action)
        val deserialized = CoreGlobals.jsonMapper.readValue(json, PlayerAction::class.java)
        deserialized shouldBe action
    }
}
