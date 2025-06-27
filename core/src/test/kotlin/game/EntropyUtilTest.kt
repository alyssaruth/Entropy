package game

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class EntropyUtilTest : AbstractTest() {
    @Test
    fun `Should correctly compute the amount required to bid a specific suit`() {
        amountRequiredToBid(Suit.Clubs, Suit.Diamonds, 3) shouldBe 4
        amountRequiredToBid(Suit.Diamonds, Suit.Diamonds, 3) shouldBe 4
        amountRequiredToBid(Suit.Hearts, Suit.Diamonds, 3) shouldBe 3
        amountRequiredToBid(Suit.Moons, Suit.Diamonds, 3) shouldBe 3
        amountRequiredToBid(Suit.Spades, Suit.Diamonds, 3) shouldBe 3
        amountRequiredToBid(Suit.Stars, Suit.Diamonds, 3) shouldBe 3
    }

    @Test
    fun `Should exclude stars from perfect bid suit`() {
        val cards = listOf("Jo1", "Jo2", "Jo3")
        perfectBidSuit(cards, 2, true) shouldBe Suit.Stars
        perfectBidSuit(cards, 2, false) shouldBe Suit.Spades
    }

    @Test
    fun `Should get the perfect bid suit`() {
        perfectBidSuit(listOf("Jo1", "Ah", "Ad", "3s", "-Jc"), 3, true) shouldBe Suit.Spades
    }
}
