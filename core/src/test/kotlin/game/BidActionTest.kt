package game

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class BidActionTest : AbstractTest() {
    @Test
    fun `Should include card to reveal in description`() {
        val testBid = TestBidAction("Alyssa", false)

        testBid.toString() shouldBe "5 things"

        testBid.cardToReveal = "Ac"
        testBid.toString() shouldBe "5 things (Shows: Ac)"
    }
}

private class TestBidAction(override val playerName: String, override val blind: Boolean) :
    BidAction<TestBidAction>() {
    override fun higherThan(other: TestBidAction) = false

    override fun overAchievementThreshold() = false

    override fun isPerfect(cards: List<String>, settings: GameSettings) = false

    override fun isOverbid(cards: List<String>, settings: GameSettings) = false

    override fun plainString() = "5 things"
}
