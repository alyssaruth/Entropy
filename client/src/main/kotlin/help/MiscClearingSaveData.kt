package help

import java.awt.Color
import java.awt.Font
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JTextPane
import util.EntropyColour

class MiscClearingSaveData : HelpPanel() {
    override val nodeName = "Clearing Data"
    override val panelName = "MiscClearingSaveData"

    private val title = JTextPane()
    private val paneIntro = JTextPane()
    private val paneOne = JTextPane()

    init {
        background = Color.WHITE
        layout = null
        paneOne.font = Font("SansSerif", Font.PLAIN, 14)
        paneOne.contentType = "text/html"
        paneOne.text =
            "<html>\r\n<b>- Statistics Only: </b>This will clear anything that appears under your statistics such as time played and total games won. Clearing this will preserve any achievements you have already earned, but any progress made towards locked achievements (e.g. those for amount of time played) will be lost.\r\n<br><br>\r\n<b>- Achievements and Statistics: </b> This will again clear your statistics, but will this time also remove any achievements you have already earned.\r\n<br><br>\r\n<b>- My Replays / Imported Replays: </b> These options will attempt to delete the replay files in your Personal / Imported folders. Note that it is not possible to recover these once they have been deleted, so be certain that you want to do this before proceeding.\r\n</html>"
        paneOne.setBounds(21, 360, 429, 279)
        add(paneOne)
        title.text = "Clearing Saved Data"
        title.foreground = EntropyColour.COLOUR_HELP_TITLE
        title.font = Font("Tahoma", Font.BOLD, 18)
        title.isEditable = false
        title.setBounds(21, 25, 192, 30)
        add(title)
        val lblNewLabel = JLabel("")
        lblNewLabel.icon =
            ImageIcon(MiscClearingSaveData::class.java.getResource("/help/clearData.png"))
        lblNewLabel.setBounds(120, 114, 230, 230)
        add(lblNewLabel)
        paneIntro.font = Font("SansSerif", Font.PLAIN, 14)
        paneIntro.contentType = "text/html"
        paneIntro.text =
            "<html>Options to clear saved data can be found under File > Clear Data. This will bring up the following dialog: </html>"
        paneIntro.setBounds(21, 54, 429, 50)
        add(paneIntro)

        finaliseComponents()
    }
}
