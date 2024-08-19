package help

import java.awt.Color
import java.awt.Font
import javax.swing.JTextPane
import util.EntropyColour

class RulesVectropyChallenging : HelpPanel() {
    override val nodeName = "Challenging"
    override val panelName = "RulesVectropyChallenging"

    private val title = JTextPane()
    private val paneOne = JTextPane()

    init {
        background = Color.WHITE
        layout = null
        paneOne.font = Font("SansSerif", Font.PLAIN, 14)
        paneOne.contentType = "text/html"
        paneOne.text =
            "<html>Any player whose turn it is to bid can choose to challenge instead. This means that the player does not believe that there are as many cards of one or more of the suits bid by the previous player. When a player challenges, all cards are revealed on the table and counted. The challenge is then evaluated.\r\n<br><br>\r\nIf a challenge is successful, the player who made the last bid loses the round, whereas if it is unsuccessful the player who challenged is the one to lose. Whether or not a challenge is successful is determined by how many of the four suits were actually present \u2013 it is successful if there were fewer of <b>any</b> of the suits bid, and unsuccessful otherwise.\r\n<br><br>\r\nFor example, in a game with 3 clubs, 4 diamonds, 2 hearts and 2 spades, challenging a bid of (3, 2, 1, 0) would be unsuccessful as there were at least as many cards as bid in all four suits. A bid of (3, 3, 0, 3), however, contains more spades than are present, so challenging this would be successful.</html>"
        paneOne.setBounds(21, 54, 429, 353)
        add(paneOne)
        title.text = "Challenging"
        title.foreground = EntropyColour.COLOUR_HELP_TITLE
        title.font = Font("Tahoma", Font.BOLD, 18)
        title.isEditable = false
        title.setBounds(21, 25, 216, 30)
        add(title)

        finaliseComponents()
    }

    override fun searchTermsToExclude() = listOf("challeng")
}
