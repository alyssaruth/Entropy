package game

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class CardsUtilTest : AbstractTest() {
    @Test
    fun `Should correctly compute the contribution of individual cards`() {
        countContribution(Suit.Hearts, "Jo1", 3) shouldBe 3
        countContribution(Suit.Hearts, "Ah", 3) shouldBe 2
        countContribution(Suit.Hearts, "As", 3) shouldBe 1
        countContribution(Suit.Hearts, "-Jh", 3) shouldBe -1
        countContribution(Suit.Hearts, "Jh", 3) shouldBe 1
        countContribution(Suit.Hearts, "Qh", 3) shouldBe 1

        countContribution(Suit.Hearts, "3c", 3) shouldBe 0
        countContribution(Suit.Hearts, "-Js", 3) shouldBe 0
    }

    @Test
    fun `Should count the total for a given suit`() {
        val cards = listOf("Jo1", "Ah", "Ad", "3s", "-Jc")

        countSuit(Suit.Clubs, cards, 3) shouldBe 4
        countSuit(Suit.Diamonds, cards, 3) shouldBe 6
        countSuit(Suit.Hearts, cards, 3) shouldBe 6
        countSuit(Suit.Moons, cards, 3) shouldBe 5
        countSuit(Suit.Spades, cards, 3) shouldBe 6
        countSuit(Suit.Stars, cards, 3) shouldBe 5
    }
}
