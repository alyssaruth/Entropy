package game

import io.kotest.matchers.shouldBe
import java.awt.Color
import org.junit.jupiter.api.Test
import preference.FOUR_COLOURS
import preference.PreferenceSetting
import testCore.makeEntropyBidAction
import util.AbstractClientTest
import util.ClientGlobals

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

    @Test
    fun `HTML rendering for an entropy bid`() {
        val bid = makeEntropyBidAction(suit = Suit.Diamonds, amount = 1)
        bid.htmlString() shouldBe "<font color=\"#FF0000FF\" face=\"Segoe UI Symbol\">1♦</font>"

        ClientGlobals.preferenceStore.save(PreferenceSetting.NumberOfColours, FOUR_COLOURS)
        bid.htmlString() shouldBe "<font color=\"#0000FFFF\" face=\"Segoe UI Symbol\">1♦</font>"
    }

    @Test
    fun `Should be able to get a suit colour`() {
        Suit.Diamonds.getColour() shouldBe Color.red
        Suit.Diamonds.getColourHex() shouldBe "#FF0000FF"

        ClientGlobals.preferenceStore.save(PreferenceSetting.NumberOfColours, FOUR_COLOURS)

        Suit.Diamonds.getColour() shouldBe Color.blue
        Suit.Diamonds.getColourHex() shouldBe "#0000FFFF"
    }
}
