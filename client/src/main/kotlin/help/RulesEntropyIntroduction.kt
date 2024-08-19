package help

import java.awt.Color
import java.awt.Font
import javax.swing.JTextPane
import util.EntropyColour

class RulesEntropyIntroduction : HelpPanel() {
    override val nodeName = "Introduction"

    private val title = JTextPane()
    private val paneOne = JTextPane()

    init {
        background = Color.WHITE
        layout = null
        paneOne.font = Font("SansSerif", Font.PLAIN, 14)
        paneOne.contentType = "text/html"
        paneOne.text =
            "<html>Entropy is a <u><font color=\"blue\">bidding</font></u> game for 2-4 players, played with one or more decks of cards. The players are dealt hands of up to 5 cards and play proceeds clockwise, with each player <u><font color=\"blue\">bidding</font></u> higher than those before until someone <u><font color=\"blue\">challenges</font></u> and the cards are revealed. \r\n<br><br>\r\nThe loser of the <u><font color=\"blue\">challenge</font></u> loses one card for the next round. Rounds continue until only one player has cards left, at which point they are declared the winner.</html>"
        paneOne.setBounds(21, 54, 418, 158)
        add(paneOne)
        title.text = "Introduction"
        title.foreground = EntropyColour.COLOUR_HELP_TITLE
        title.font = Font("Tahoma", Font.BOLD, 18)
        title.setBounds(21, 25, 159, 30)
        add(title)

        finaliseComponents()
    }
}
