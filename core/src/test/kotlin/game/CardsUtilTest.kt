package game

import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import testCore.makeGameSettings

class CardsUtilTest : AbstractTest() {
    @Test
    fun `Should extract the hands from a map`() {
        val hands = mapOf(1 to listOf("Ac", "5s"), 2 to listOf("Jo1", "4d"))
        val result = extractCards(hands)
        result.shouldContainExactlyInAnyOrder("Ac", "5s", "Jo1", "4d")
    }

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

    @Test
    fun `Should conclude if a card is relevant or not`() {
        Suit.entries.forEach { suit ->
            isCardRelevant("Jo1", suit) shouldBe true
            isCardRelevant("Ac", suit) shouldBe true
        }

        isCardRelevant("5h", Suit.Clubs) shouldBe false
        isCardRelevant("5h", Suit.Hearts) shouldBe true
    }

    @Test
    fun `Should make a standard 52-card deck`() {
        val settings =
            makeGameSettings(jokerQuantity = 0, includeMoons = false, includeStars = false)

        val deck = createAndShuffleDeck(settings)
        deck.size shouldBe 52
        deck.shouldContainExactlyInAnyOrder(
            "Ac",
            "2c",
            "3c",
            "4c",
            "5c",
            "6c",
            "7c",
            "8c",
            "9c",
            "Tc",
            "Jc",
            "Qc",
            "Kc",
            "Ad",
            "2d",
            "3d",
            "4d",
            "5d",
            "6d",
            "7d",
            "8d",
            "9d",
            "Td",
            "Jd",
            "Qd",
            "Kd",
            "Ah",
            "2h",
            "3h",
            "4h",
            "5h",
            "6h",
            "7h",
            "8h",
            "9h",
            "Th",
            "Jh",
            "Qh",
            "Kh",
            "As",
            "2s",
            "3s",
            "4s",
            "5s",
            "6s",
            "7s",
            "8s",
            "9s",
            "Ts",
            "Js",
            "Qs",
            "Ks",
        )
    }

    @Test
    fun `Should respect passed in seed`() {
        val deck = createAndShuffleDeck(makeGameSettings(), 500)
        deck.first() shouldBe "2s"
    }

    @Test
    fun `Should include the specified number of jokers`() {
        val settings = makeGameSettings(jokerQuantity = 3)
        val deck = createAndShuffleDeck(settings)
        deck.size shouldBe 55
        deck.count { it.startsWith("Jo") } shouldBe 3
    }

    @Test
    fun `Should include moons and stars`() {
        val settings = makeGameSettings(includeMoons = true, includeStars = true)
        val deck = createAndShuffleDeck(settings)
        deck.size shouldBe 78
        deck.shouldContain("Am")
    }

    @Test
    fun `Should include negative jacks`() {
        val settings = makeGameSettings(negativeJacks = true)
        val deck = createAndShuffleDeck(settings)
        deck.shouldContainAll("-Jc", "-Jd", "-Jh", "-Js")
    }
}
