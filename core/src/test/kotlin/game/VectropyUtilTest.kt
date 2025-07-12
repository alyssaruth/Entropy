package game

import io.kotest.matchers.shouldBe
import `object`.VectropyBid
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class VectropyUtilTest : AbstractTest() {
    @Test
    fun `Should detect an overbid`() {
        val cards = listOf("Ad", "3c", "Jo1")
        val baseMap = Suit.entries.associateWith { 0 }
        isOverbid(VectropyBid(baseMap + (Suit.Clubs to 5), false, false), cards, 2) shouldBe true
        isOverbid(VectropyBid(baseMap + (Suit.Diamonds to 5), false, false), cards, 2) shouldBe true
        isOverbid(VectropyBid(baseMap + (Suit.Hearts to 4), false, false), cards, 2) shouldBe true
        isOverbid(VectropyBid(baseMap + (Suit.Spades to 4), false, false), cards, 2) shouldBe true

        val perfect = mapOf(Suit.Clubs to 4, Suit.Diamonds to 4, Suit.Hearts to 3, Suit.Spades to 3)
        isOverbid(VectropyBid(perfect, false, false), cards, 2) shouldBe false
    }
}
