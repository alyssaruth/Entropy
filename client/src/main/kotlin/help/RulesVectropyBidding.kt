package help

import java.awt.Color
import java.awt.Font
import javax.swing.JTextPane
import util.EntropyColour
import util.Registry

class RulesVectropyBidding : HelpPanel(), Registry {
    override val nodeName = "Bidding"

    private val title = JTextPane()
    private val paneOne = JTextPane()
    private val subtitle = JTextPane()
    private val paneTwo = JTextPane()

    private var clubsColour = "black"
    private var diamondsColour = "red"

    init {
        background = Color.WHITE
        layout = null
        paneOne.font = Font("SansSerif", Font.PLAIN, 14)
        paneOne.contentType = "text/html"
        paneOne.setBounds(21, 54, 429, 230)
        add(paneOne)
        paneTwo.font = Font("SansSerif", Font.PLAIN, 14)
        paneTwo.contentType = "text/html"
        paneTwo.text =
            "<html>A bid is higher than another if the sum of its elements is higher, with the added restriction that each bid must include at least as many of each individual suit as the one before it. For example, if faced with a bid of (0, 0, 0, 2):\r\n\r\n<ul style=\"margin-left:10px; padding:0px\">\r\n<li style=\"margin-bottom: 6px;\"> (1, 0, 0, 2) is a valid higher bid because it includes (0, 0, 0, 2).</li>\r\n<li style=\"margin-bottom:6px;\"> (5, 5, 5, 0) is a higher bid, but this is <b>not</b> legal because it contains fewer spades than the bid before it.</li></ul></html>"
        paneTwo.font = Font("Tahoma", Font.PLAIN, 14)
        paneTwo.setBounds(21, 320, 429, 156)
        add(paneTwo)
        title.foreground = EntropyColour.COLOUR_HELP_TITLE
        title.font = Font("Tahoma", Font.BOLD, 18)
        title.text = "Bidding"
        title.setBounds(21, 25, 159, 30)
        add(title)
        subtitle.text = "Bid Hierarchy"
        subtitle.foreground = EntropyColour.COLOUR_HELP_TITLE
        subtitle.font = Font("Tahoma", Font.BOLD, 18)
        subtitle.setBounds(21, 290, 159, 30)
        add(subtitle)

        finaliseComponents()
    }

    override fun searchTermsToExclude() = listOf("bidding")

    fun fireAppearancePreferencesChange() {
        val fourColours = useFourColours()
        clubsColour = if (fourColours) "green" else "black"
        diamondsColour = if (fourColours) "blue" else "red"
    }

    override fun refresh() {
        setPaneOneText()
    }

    private fun setPaneOneText() {
        val extraSuits = Registry.rewards.getBoolean(Registry.REWARDS_BOOLEAN_EXTRA_SUITS, false)

        var text =
            "<html>A round starts with the first person to play bidding. At the start of a new game, "
        text +=
            "the round is started by a player chosen at random. In each subsequent round the loser of the previous "
        text +=
            "round starts, unless losing the last round caused them to go out. In this case, the person to the left "
        text +=
            "of the losing player starts the next round.\r\n<br><br>\r\nEach bid is an ordered vector of four numbers. "
        text +=
            "These numbers represent the amount that is being bid for each suit from lowest to highest - "
        text +=
            "(<font color=\"$clubsColour\">\u2663</font>,  <font color=\"$diamondsColour\">\u2666</font>, "
        text += "<font color=\"red\">\u2665</font>, \u2660). "

        if (extraSuits) {
            text += "This vector naturally extends if additional suits are in play. "
        }

        text +=
            "Each subsequent bid must be higher than the last. Bidding continues clockwise round the players. "
        text +=
            "At any point a player may opt to <u><font color=\"blue\">challenge</font></u> the current bid rather than making a higher bid of their own.</html>\r\n\r\n\r\n"

        paneOne.text = text
    }
}
