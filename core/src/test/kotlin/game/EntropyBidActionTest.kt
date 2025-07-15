package game

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import testCore.makeEntropyBidAction
import testCore.makeGameSettings
import utils.CoreGlobals

class EntropyBidActionTest : AbstractTest() {
    @Test
    fun `Should serialise and deserialise correctly`() {
        val bid: PlayerAction = EntropyBidAction("Suzie", false, 5, Suit.Moons)
        val json = CoreGlobals.jsonMapper.writeValueAsString(bid)
        val deserialized = CoreGlobals.jsonMapper.readValue(json, PlayerAction::class.java)
        deserialized shouldBe bid
    }

    @Test
    fun `Over achievement threshold`() {
        makeEntropyBidAction(amount = 4).overAchievementThreshold() shouldBe false
        makeEntropyBidAction(amount = 5).overAchievementThreshold() shouldBe true
        makeEntropyBidAction(amount = 6).overAchievementThreshold() shouldBe true
    }

    @Test
    fun `Should detect a perfect bid`() {
        val settings = makeGameSettings(jokerValue = 2)
        val cards = listOf("As", "3s", "Jo1", "4h")

        makeEntropyBidAction(4, Suit.Hearts).isPerfect(cards, settings) shouldBe false
        makeEntropyBidAction(5, Suit.Hearts).isPerfect(cards, settings) shouldBe false

        makeEntropyBidAction(4, Suit.Spades).isPerfect(cards, settings) shouldBe false
        makeEntropyBidAction(5, Suit.Spades).isPerfect(cards, settings) shouldBe true
        makeEntropyBidAction(6, Suit.Spades).isPerfect(cards, settings) shouldBe false
    }

    @Test
    fun `Should detect overbids`() {
        val settings = makeGameSettings(jokerValue = 3)
        val cards = listOf("As", "3s", "Jo1", "4h")

        makeEntropyBidAction(6, Suit.Spades).isOverbid(cards, settings) shouldBe false
        makeEntropyBidAction(7, Suit.Spades).isOverbid(cards, settings) shouldBe true

        makeEntropyBidAction(4, Suit.Clubs).isOverbid(cards, settings) shouldBe false
        makeEntropyBidAction(5, Suit.Clubs).isOverbid(cards, settings) shouldBe true
    }

    @Test
    fun `Should determine which bid is higher`() {
        val twoDiamonds = makeEntropyBidAction(2, Suit.Diamonds)

        twoDiamonds.higherThan(makeEntropyBidAction(1, Suit.Diamonds)) shouldBe true
        twoDiamonds.higherThan(makeEntropyBidAction(1, Suit.Stars)) shouldBe true
        twoDiamonds.higherThan(makeEntropyBidAction(2, Suit.Clubs)) shouldBe true

        twoDiamonds.higherThan(makeEntropyBidAction(2, Suit.Diamonds)) shouldBe false

        twoDiamonds.higherThan(makeEntropyBidAction(2, Suit.Hearts)) shouldBe false
        twoDiamonds.higherThan(makeEntropyBidAction(3, Suit.Clubs)) shouldBe false
    }
}
