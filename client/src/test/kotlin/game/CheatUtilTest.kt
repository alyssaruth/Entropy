package game

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.makeGameSettings
import util.AbstractClientTest

class CheatUtilTest : AbstractClientTest() {
    @Test
    fun `Contains non-joker`() {
        containsNonJoker(listOf("Jo1", "Jo2", "Jo3")) shouldBe false
        containsNonJoker(listOf()) shouldBe false

        containsNonJoker(listOf("Jo1", "Ac", "Jo3")) shouldBe true
    }

    @Test
    fun `Should print max bid in every suit`() {
        val cards = listOf("Ac", "7m", "6d", "3d", "4d", "Jo1", "5x", "Kx")
        val result =
            getMaxBidString(
                cards,
                makeGameSettings(jokerValue = 3, includeMoons = true, includeStars = true),
            )
        result shouldBe "5c, 7d, 4h, 5m, 4s, 6x"
    }
}
