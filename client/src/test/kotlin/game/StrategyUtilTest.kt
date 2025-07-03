package game

import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.matchers.maps.shouldNotContainKeys
import org.junit.jupiter.api.Test
import testCore.makeGameSettings
import util.AbstractClientTest

class StrategyUtilTest : AbstractClientTest() {
    @Test
    fun `Should compute an EV map of suits with a base deck`() {
        val cards = listOf("As", "Jo0", "3c")
        val settings =
            makeGameSettings(
                jokerQuantity = 1,
                jokerValue = 2,
                includeMoons = false,
                includeStars = false,
            )
        val map = getEvMap(cards, settings, 11)
        map.shouldNotContainKeys(Suit.Moons, Suit.Stars)
        map.assertEv(Suit.Clubs, 6.4)
        map.assertEv(Suit.Diamonds, 5.56)
        map.assertEv(Suit.Hearts, 5.56)
        map.assertEv(Suit.Spades, 6.4)
    }

    @Test
    fun `Should compute an EV map of suits with all 6 suits in play`() {
        val cards = listOf("Jo2", "5x", "Ax", "7m", "3c", "4c")
        val settings =
            makeGameSettings(
                jokerQuantity = 4,
                jokerValue = 3,
                includeMoons = true,
                includeStars = true,
            )

        val map = getEvMap(cards, settings, 12)
        map.assertEv(Suit.Clubs, 7.97368)
        map.assertEv(Suit.Diamonds, 6.13157)
        map.assertEv(Suit.Hearts, 6.13157)
        map.assertEv(Suit.Moons, 7.05263)
        map.assertEv(Suit.Spades, 6.13157)
        map.assertEv(Suit.Stars, 7.97368)
    }

    /** Account for some double precision fun */
    private fun Map<Suit, Double>.assertEv(suit: Suit, expected: Double) {
        getValue(suit).shouldBeBetween(expected, expected, 0.00001)
    }
}
