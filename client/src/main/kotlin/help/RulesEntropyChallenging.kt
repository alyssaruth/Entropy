package help

import java.awt.Color
import java.awt.Font
import javax.swing.JTextPane
import util.EntropyColour

class RulesEntropyChallenging : HelpPanel() {
    override val nodeName = "Challenging"

    private val title = JTextPane()
    private val paneOne = JTextPane()

    init {
        background = Color.WHITE
        layout = null
        paneOne.font = Font("SansSerif", Font.PLAIN, 14)
        paneOne.contentType = "text/html"
        paneOne.text =
            "<html>Any player whose turn it is to bid can choose to challenge instead. This means that the player does not believe that there are as many cards of the suit as bid by the previous player. When a player challenges, all cards are revealed on the table and counted. The challenge is then evaluated.\r\n<br><br>\r\nIf a challenge is successful, the player who made the last bid loses the round, whereas if it is unsuccessful the player who challenged is the one to lose. Whether or not a challenge is successful is determined by how many of the suit were actually present – it is successful if there were fewer than the number bid and unsuccessful otherwise. \r\n<br><br>\r\nFor example if Tom bids five hearts and Alex challenges him, that means Alex thinks there are fewer than five hearts in all the players cards. The cards are turned over and counted. In fact there were six hearts, so Alex lost the challenge and loses a card for the next round.</html>"
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
