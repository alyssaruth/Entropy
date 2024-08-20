package help

import java.awt.Color
import java.awt.Font
import javax.swing.JTextPane
import util.EntropyColour

class RulesEntropyBidding : HelpPanel() {
    override val nodeName = "Bidding"

    private val title = JTextPane()
    private val paneOne = JTextPane()
    private val subtitle = JTextPane()
    private val paneTwo = JTextPane()

    init {
        background = Color.WHITE
        layout = null
        paneOne.font = Font("SansSerif", Font.PLAIN, 14)
        paneOne.contentType = "text/html"
        paneOne.text =
            "<html>A round starts with the first person to play bidding. At the start of a new game, the round is started by a player chosen at random. In each subsequent round the loser of the previous round starts, unless losing the last round caused them to go out. In this case, the person to the left of the losing player starts the next round.\r\n<br><br>\r\nEach bid must be a suit and a number, for example \"2 Spades\". Each subsequent bid must be higher than the last, including adhering to the suit <u><font color=\"blue\">order</font></u>. Bidding continues clockwise round the players. At any point a player may opt to <u><font color=\"blue\">challenge</font></u> the current bid rather than making a higher bid of their own.</html>"
        paneOne.setBounds(21, 54, 429, 220)
        add(paneOne)
        paneTwo.font = Font("SansSerif", Font.PLAIN, 14)
        paneTwo.contentType = "text/html"
        paneTwo.text =
            "A bid is higher than another if the number is higher or if the number is equal and the suit is higher. For example, \"3 Hearts\" is higher than \"2 Hearts\", as 3 is greater than 2. \"3 Hearts\" is lower than \"3 Spades\" because spades is a higher suit than hearts. "
        paneTwo.setBounds(21, 313, 429, 100)
        add(paneTwo)
        title.foreground = EntropyColour.COLOUR_HELP_TITLE
        title.font = Font("Tahoma", Font.BOLD, 18)
        title.text = "Bidding"
        title.setBounds(21, 25, 159, 30)
        add(title)
        subtitle.text = "Bid Hierarchy"
        subtitle.foreground = EntropyColour.COLOUR_HELP_TITLE
        subtitle.font = Font("Tahoma", Font.BOLD, 18)
        subtitle.setBounds(21, 284, 159, 30)
        add(subtitle)

        finaliseComponents()
    }

    override fun searchTermsToExclude() = listOf("bidding")
}
