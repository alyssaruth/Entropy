package help

import java.awt.Color
import java.awt.Font
import javax.swing.JTextPane
import util.EntropyColour

class MiscCheatCodes : HelpPanel() {
    override val nodeName = "Cheat Codes"

    private val title = JTextPane()
    private val paneIntro = JTextPane()
    private val paneOne = JTextPane()

    private val cheatsText =
        ("<html><b>- showmethecards: </b>Turn all hands face-up!<br><br>" +
            "<b>- maxbids: </b> Lists the amount of each suit present in the current game.<br><br>" +
            "<b>- perfectbid: </b> Gives you the <u><font color=\"blue\">perfect</u></font> bid for the current round. Returns the same value as maxbids if playing in Vectropy mode.<br><br>" +
            "<b>- rainingjokers: </b> Randomly turns 1-5 of the cards in play into jokers.<br><br>" +
            "<b>- bluescreenofdeath: </b> Unlocks the hidden 'Blue Screen of Death' achievement (pfft, like you've not already got it).<br><br>" +
            "<b>- simulator: </b> Opens the Entropy Simulator. This was used during development to pit strategies against one another so that we could test enhancements and rank them.</html>")

    init {
        background = Color.WHITE
        layout = null
        paneOne.font = Font("SansSerif", Font.PLAIN, 14)
        paneOne.contentType = "text/html"
        paneOne.text = cheatsText
        paneOne.setBounds(21, 156, 429, 317)
        add(paneOne)
        title.text = "Cheat Codes"
        title.foreground = EntropyColour.COLOUR_HELP_TITLE
        title.font = Font("Tahoma", Font.BOLD, 18)
        title.setBounds(21, 25, 192, 30)
        add(title)
        paneIntro.font = Font("SansSerif", Font.PLAIN, 14)
        paneIntro.contentType = "text/html"
        paneIntro.text =
            "<html>Congratulations on reaching 50 achievements! Here are some cheats you can enter whilst playing the game, which I originally created to make testing easier. Whilst in the main window, press CTRL+; to bring up the command bar, then enter any of the following commands:</html>"
        paneIntro.setBounds(21, 54, 429, 91)
        add(paneIntro)

        finaliseComponents()
    }
}
