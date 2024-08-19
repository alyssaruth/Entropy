package help

import java.awt.Color
import java.awt.Font
import javax.swing.JTextPane
import util.EntropyColour

class RulesIllegal : HelpPanel() {
    override val nodeName = "Illegal!"
    override val panelName = "RulesIllegal"

    private val title = JTextPane()
    private val paneOne = JTextPane()

    init {
        background = Color.WHITE
        layout = null
        paneOne.font = Font("SansSerif", Font.PLAIN, 14)
        paneOne.contentType = "text/html"
        paneOne.text =
            "<html>The 'Illegal' option provides an alternative to bidding higher or challenging when facing a bid. If a player declares 'Illegal', they claim that the bid they were faced with was <font color=\"blue\"><u>perfect</u></font>. If they are right the opponent loses a card for the next round, else they lose a card - regardless of whether the bid they were faced with was an overbid or an underbid.</html>"
        paneOne.setBounds(21, 54, 429, 220)
        paneOne.isEditable = false
        add(paneOne)
        title.foreground = EntropyColour.COLOUR_HELP_TITLE
        title.font = Font("Tahoma", Font.BOLD, 18)
        title.text = "Illegal!"
        title.setBounds(21, 25, 159, 30)
        title.isEditable = false
        add(title)

        finaliseComponents()
    }

    override fun searchTermsToExclude() = listOf("bid")
}
