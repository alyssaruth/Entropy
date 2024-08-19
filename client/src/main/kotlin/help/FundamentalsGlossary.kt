package help

import java.awt.Color
import java.awt.Font
import javax.swing.JTextPane
import util.EntropyColour
import util.Registry

class FundamentalsGlossary : HelpPanel(), Registry {
    override val nodeName = "Glossary"

    private val title = JTextPane()
    private val paneOne = JTextPane()

    init {
        background = Color.WHITE
        layout = null
        paneOne.font = Font("SansSerif", Font.PLAIN, 14)
        paneOne.contentType = "text/html"
        paneOne.setBounds(21, 54, 429, 282)
        add(paneOne)
        title.text = "Glossary"
        title.foreground = EntropyColour.COLOUR_HELP_TITLE
        title.font = Font("Tahoma", Font.BOLD, 18)
        title.setBounds(21, 25, 159, 30)
        add(title)

        finaliseComponents()
    }

    override fun searchTermsToExclude() = listOf("perfect", "challenge")

    override fun refresh() {
        val blindUnlocked = Registry.rewards.getBoolean(Registry.REWARDS_BOOLEAN_BLIND, false)

        var glossaryText = "<html>\r\n<ul style=\"margin-left:10px; padding:0px\">\r\n"

        if (blindUnlocked) {
            glossaryText +=
                "<li style=\"margin-bottom: 6px;\"><b>Blind:</b> Playing blind means not looking at your cards.</li>\r\n"
        }

        glossaryText +=
            "<li style=\"margin-bottom: 6px;\"><b>Handicap:</b> Starting a game with fewer cards than your opponents.</li>\r\n"
        glossaryText +=
            "<li style=\"margin-bottom: 6px;\"><b>Minbid:</b> The minimum possible bid at a given moment, which varies for different variants of Entropy. </li>\r\n"
        glossaryText +=
            "<li style=\"margin-bottom: 6px;\"><b>Overbid:</b> A bid which will lose to a challenge - the exact definition will vary for different variants of Entropy.</li>\r\n"
        glossaryText +=
            "<li style=\"margin-bottom: 6px;\"><b>Perfect Bid:</b> The highest possible bid for a particular round. For achievements, this counts only if the numeric value is 5 or higher.</li>\r\n"
        glossaryText += "</ul>\r\n</html>"

        paneOne.text = glossaryText
    }
}
