package game

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import testCore.makeGameSettings
import utils.CoreGlobals

class VectropyBidActionTest : AbstractTest() {
    @Test
    fun `Should serialise and deserialise correctly`() {
        val action: PlayerAction =
            VectropyBidAction("", false, 0, 1, 2, 3, 4, 5).apply { cardToReveal = "Ac" }
        val json = CoreGlobals.jsonMapper.writeValueAsString(action)
        val deserialized = CoreGlobals.jsonMapper.readValue(json, PlayerAction::class.java)
        deserialized shouldBe action

        VectropyBidAction.fromJson(json) shouldBe action
    }

    @Test
    fun `Should be able to construct with suit values`() {
        val action = VectropyBidAction("", false, 0, 1, 2, 3, 4, 5)
        action.getAmount(Suit.Clubs) shouldBe 0
        action.getAmount(Suit.Moons) shouldBe 3
        action.getAmount(Suit.Stars) shouldBe 5

        val nullsAction = VectropyBidAction("", false, 0, 1, 2, null, 3, null)
        nullsAction.getAmount(Suit.Clubs) shouldBe 0
        nullsAction.getAmount(Suit.Moons) shouldBe null
        nullsAction.getAmount(Suit.Spades) shouldBe 3
        nullsAction.getAmount(Suit.Stars) shouldBe null
    }

    @Test
    fun `Should be able to get total`() {
        val action = VectropyBidAction("", false, 0, 1, 2, 3, 4, 5)
        action.getTotal() shouldBe 15

        val nullsAction = VectropyBidAction("", false, 0, 1, 2, null, 3, null)
        nullsAction.getTotal() shouldBe 6
    }

    @Test
    fun `Should be able to compare bids`() {
        val baseAmounts = Suit.entries.associateWith { 2 }
        val highAmounts = Suit.entries.associateWith { 5 }
        val baseBid = VectropyBidAction("", false, baseAmounts)
        baseBid.higherThan(baseBid) shouldBe false

        Suit.entries.forEach { suit ->
            val higherBid = VectropyBidAction("", false, baseAmounts.plus(suit to 3))
            higherBid.higherThan(baseBid) shouldBe true
            baseBid.higherThan(higherBid) shouldBe false

            val lowerBid = VectropyBidAction("", false, highAmounts.plus(suit to 1))
            lowerBid.higherThan(baseBid) shouldBe false
            baseBid.higherThan(lowerBid) shouldBe false
        }
    }

    @Test
    fun `Should check if over achievement threshold`() {
        VectropyBidAction("", false, 1, 1, 1, 1, 0, 1).overAchievementThreshold() shouldBe true
        VectropyBidAction("", false, 1, 1, 1, null, 1, null).overAchievementThreshold() shouldBe
            false
    }

    @Test
    fun `Should detect a perfect bid`() {
        val cards = listOf("Ad", "3c", "Jo1")
        val settings = makeGameSettings(jokerValue = 2)
        val baseMap = Suit.entries.associateWith { 0 }

        val checkPerfect: (map: Map<Suit, Int>, expected: Boolean) -> Unit = { map, expected ->
            VectropyBidAction("", false, map).isPerfect(cards, settings) shouldBe expected
        }
        checkPerfect(baseMap + (Suit.Clubs to 5), false)
        checkPerfect(baseMap + (Suit.Clubs to 3), false)

        val perfect = mapOf(Suit.Clubs to 4, Suit.Diamonds to 4, Suit.Hearts to 3, Suit.Spades to 3)
        checkPerfect(perfect, true)
    }

    @Test
    fun `Should detect an overbid`() {
        val cards = listOf("Ad", "3c", "Jo1")
        val settings = makeGameSettings(jokerValue = 2)
        val baseMap = Suit.entries.associateWith { 0 }

        val checkOverbid: (map: Map<Suit, Int>, expected: Boolean) -> Unit = { map, expected ->
            VectropyBidAction("", false, map).isOverbid(cards, settings) shouldBe expected
        }
        checkOverbid(baseMap + (Suit.Clubs to 5), true)
        checkOverbid(baseMap + (Suit.Diamonds to 5), true)
        checkOverbid(baseMap + (Suit.Hearts to 4), true)
        checkOverbid(baseMap + (Suit.Spades to 4), true)

        val perfect = mapOf(Suit.Clubs to 4, Suit.Diamonds to 4, Suit.Hearts to 3, Suit.Spades to 3)
        checkOverbid(perfect, false)
    }

    @Test
    fun `Should render to a plainString`() {
        val coreSuits =
            mapOf(Suit.Clubs to 3, Suit.Diamonds to 0, Suit.Hearts to 2, Suit.Spades to 1)
        VectropyBidAction("Alyssa", false, coreSuits).plainString() shouldBe "(3, 0, 2, 1)"

        val allSuits = coreSuits + mapOf(Suit.Moons to 7, Suit.Stars to 9)
        VectropyBidAction("", false, allSuits).plainString() shouldBe "(3, 0, 2, 7, 1, 9)"
    }

    @Test
    fun `Should allow incrementing a suit`() {
        val start = mapOf(Suit.Clubs to 3, Suit.Diamonds to 0, Suit.Hearts to 2, Suit.Spades to 1)
        val bid = VectropyBidAction("Alyssa", true, start)

        val uppedDiamonds = bid.incrementSuit(Suit.Diamonds)
        uppedDiamonds shouldBe VectropyBidAction("Alyssa", true, start + (Suit.Diamonds to 1))
    }
}
