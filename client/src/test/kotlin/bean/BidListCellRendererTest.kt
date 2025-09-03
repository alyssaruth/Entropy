package bean

import game.ChallengeAction
import game.IllegalAction
import game.Suit
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.makeEntropyBidAction

class BidListCellRendererTest {
    @Test
    fun `HTML rendering - blind challenge`() {
        val challenge = ChallengeAction("Alyssa", true)
        val result = makeRenderer().toHtmlString(challenge)
        result shouldBe "<html><b><font color=\"red\">[Alyssa]:&nbsp</b></font>Challenge</html>"
    }

    @Test
    fun `HTML rendering - illegal`() {
        val challenge = IllegalAction("Mark", false)
        val result = makeRenderer().toHtmlString(challenge)
        result shouldBe "<html><b><font color=\"blue\">Mark:&nbsp</b></font>Illegal!</html>"
    }

    @Test
    fun `HTML rendering - bid with card to show`() {
        val bid = makeEntropyBidAction(cardToReveal = "Ac", amount = 2, suit = Suit.Spades)

        val result = makeRenderer().toHtmlString(bid)
        result shouldBe
            "<html><b><font color=\"red\">Alyssa:&nbsp</b></font><font color=\"#000000FF\" face=\"Segoe UI Symbol\">2♠</font><i><font color=\"#5C5C3D\">&emsp(Shows:&nbsp</i></font><font color=\"#008000FF\" face=\"Segoe UI Symbol\">Ac</font><i><font color=\"#5C5C3D\">)</i></font></html>"
    }

    private fun makeRenderer() =
        BidListCellRenderer().also {
            it.updateColours(mapOf("Alyssa" to "red", "Mark" to "blue", "David" to "green"))
        }
}
