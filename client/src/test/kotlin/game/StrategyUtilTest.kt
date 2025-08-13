package game

import TestRandom
import io.kotest.matchers.doubles.shouldBeBetween
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.maps.shouldNotContainKeys
import io.kotest.matchers.shouldBe
import makeStrategyParams
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

    @Test
    fun `Should compute the difference between a vectropy bid and what can be seen`() {
        val cards = listOf("Ac", "3d", "4h", "6h")
        val bid = VectropyBidAction("", false, 0, 2, 1, null, 2, null)
        val map = getDifferenceMap(bid, cards, 1, false, false)
        map.shouldContainAll(
            mapOf(Suit.Clubs to 2, Suit.Diamonds to 0, Suit.Hearts to 2, Suit.Spades to -1)
        )
    }

    @Test
    fun `Should return the suit or suits with the highest value`() {
        val suits =
            mapOf(
                Suit.Clubs to 5.0,
                Suit.Diamonds to 3.0,
                Suit.Hearts to -7.0,
                Suit.Moons to 8.0,
                Suit.Spades to 5.0,
                Suit.Stars to 8.0,
            )

        getMaxValue(suits) shouldBe 8.0
        getSuitWithMostPositiveValue(suits) shouldBe Suit.Moons
        getSuitsWithMostPositiveValue(suits) shouldBe listOf(Suit.Moons, Suit.Stars)
    }

    @Test
    fun `Vectropy EV challenging`() {
        val evs =
            mapOf(Suit.Clubs to 5.4, Suit.Diamonds to 8.8, Suit.Hearts to 3.7, Suit.Spades to 2.0)

        val offInOneSuit =
            computeEvDifferences(VectropyBidAction("", false, 6, 0, 0, null, 0, null), evs)
        shouldAutoChallengeForEvDiffOfIndividualSuit(offInOneSuit) shouldBe true
        shouldAutoChallengeForMultipleSuitsOverEv(offInOneSuit) shouldBe false

        val offInMultipleSuits =
            computeEvDifferences(VectropyBidAction("", false, 0, 9, 4, null, 0, null), evs)
        shouldAutoChallengeForEvDiffOfIndividualSuit(offInMultipleSuits) shouldBe false
        shouldAutoChallengeForMultipleSuitsOverEv(offInMultipleSuits) shouldBe true
    }

    /** Account for some double precision fun */
    private fun Map<Suit, Double>.assertEv(suit: Suit, expected: Double) {
        getValue(suit).shouldBeBetween(expected, expected, 0.00001)
    }

    @Test
    fun `Basic Vectropy - Opening should bid 1 of random suit if 4 or less cards in play`() {
        val strategyParams = makeStrategyParams(cardsInPlay = 4)
        val random = TestRandom(0, 1)

        val openingOne = getBasicVectropyOpening("Clive", emptyList(), strategyParams, random)
        val openingTwo = getBasicVectropyOpening("Clive", emptyList(), strategyParams, random)

        val emptyBid = Suit.filter(strategyParams.settings).associateWith { 0 }

        openingOne shouldBe VectropyBidAction("Clive", false, emptyBid + (Suit.Clubs to 1))
        openingTwo shouldBe VectropyBidAction("Clive", false, emptyBid + (Suit.Diamonds to 1))
    }
}
