package game

import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class SuitTest : AbstractTest() {
    @Test
    fun `Should describe correctly`() {
        Suit.Clubs.getDescription(1) shouldBe "club"
        Suit.Diamonds.getDescription(1) shouldBe "diamond"
        Suit.Hearts.getDescription(1) shouldBe "heart"
        Suit.Moons.getDescription(1) shouldBe "moon"
        Suit.Spades.getDescription(1) shouldBe "spade"
        Suit.Stars.getDescription(1) shouldBe "star"

        Suit.Clubs.getDescription(2) shouldBe "clubs"
        Suit.Diamonds.getDescription(4) shouldBe "diamonds"
        Suit.Hearts.getDescription(7) shouldBe "hearts"
        Suit.Moons.getDescription(5) shouldBe "moons"
        Suit.Spades.getDescription(3) shouldBe "spades"
        Suit.Stars.getDescription(0) shouldBe "stars"
    }

    @Test
    fun `Should get next suit, taking filters into account`() {
        Suit.Hearts.next(true, false) shouldBe Suit.Moons
        Suit.Hearts.next(false, false) shouldBe Suit.Spades
        Suit.Spades.next(false, true) shouldBe Suit.Stars
        Suit.Spades.next(false, false) shouldBe Suit.Clubs
        Suit.Stars.next(false, true) shouldBe Suit.Clubs
    }

    @Test
    fun `Should filter correctly`() {
        Suit.filter(true, true)
            .shouldContainExactly(
                Suit.Clubs,
                Suit.Diamonds,
                Suit.Hearts,
                Suit.Moons,
                Suit.Spades,
                Suit.Stars,
            )

        Suit.filter(false, true)
            .shouldContainExactly(Suit.Clubs, Suit.Diamonds, Suit.Hearts, Suit.Spades, Suit.Stars)

        Suit.filter(true, false)
            .shouldContainExactly(Suit.Clubs, Suit.Diamonds, Suit.Hearts, Suit.Moons, Suit.Spades)

        Suit.filter(false, false)
            .shouldContainExactly(Suit.Clubs, Suit.Diamonds, Suit.Hearts, Suit.Spades)
    }

    @Test
    fun `Should respect filters when getting a random suit`() {
        val baseSuits = (1..1000).map { Suit.random(false, false) }.toSet()
        baseSuits.shouldContainExactlyInAnyOrder(
            Suit.Clubs,
            Suit.Diamonds,
            Suit.Hearts,
            Suit.Spades,
        )

        val allSuits = (1..1000).map { Suit.random(true, true) }.toSet()
        allSuits.shouldContainAll(Suit.entries)
    }
}
