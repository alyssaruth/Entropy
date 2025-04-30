package game

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import utils.CoreGlobals

class EntropyBidActionTest : AbstractTest() {
    @Test
    fun `Should serialise and deserialise correctly`() {
        val bid: PlayerAction = EntropyBidAction("Suzie", "Ac", false, 5, Suit.Moons)
        val json = CoreGlobals.jsonMapper.writeValueAsString(bid)
        val deserialized = CoreGlobals.jsonMapper.readValue(json, PlayerAction::class.java)
        deserialized shouldBe bid
    }
}
