package game

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import util.AbstractClientTest

class RenderingUtilTest : AbstractClientTest() {
    @Test
    fun `Should render cards to HTML correctly`() {
        getCardHtml("Jo1") shouldBe "<font color=\"#FF33CC\">Jo</font>"
        getCardHtml("Th") shouldBe
            "<font color=\"${Suit.Hearts.getColourHex()}\" face=\"Segoe UI Symbol\">Th</font>"
        getCardHtml("-Jc") shouldBe
            "<font color=\"${Suit.Clubs.getColourHex()}\" face=\"Segoe UI Symbol\">Jc</font>"
    }

    @Test
    fun `Should render a Vectropy result`() {
        val plainResult = getVectropyResult(listOf("Tc", "3c", "2d"), 2, null, false, false)
        plainResult shouldBe
            "(<font color=\"${Suit.Clubs.getColourHex()}\">2</font>, " +
                "<font color=\"${Suit.Diamonds.getColourHex()}\">1</font>, " +
                "<font color=\"${Suit.Hearts.getColourHex()}\">0</font>, " +
                "<font color=\"${Suit.Spades.getColourHex()}\">0</font>)"

        val highlightedResult =
            getVectropyResult(listOf("Jo1", "Tx", "3d", "5d"), 2, Suit.Diamonds, true, true)
        highlightedResult shouldBe
            "(<font color=\"${Suit.Clubs.getColourHex()}\">2</font>, " +
                "<font color=\"${Suit.Diamonds.getColourHex()}\"><b>4</b></font>, " +
                "<font color=\"${Suit.Hearts.getColourHex()}\">2</font>, " +
                "<font color=\"${Suit.Moons.getColourHex()}\">2</font>, " +
                "<font color=\"${Suit.Spades.getColourHex()}\">2</font>, " +
                "<font color=\"${Suit.Stars.getColourHex()}\">3</font>)"
    }
}
