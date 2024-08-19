package help

import java.awt.Color
import java.awt.Font
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JTextPane
import util.EntropyColour

class MiscBugReport : HelpPanel() {
    override val nodeName = "Bug Report"
    override val panelName = "MiscBugReport"

    private val title = JTextPane()
    private val paneIntro = JTextPane()

    init {
        background = Color.WHITE
        layout = null
        title.text = "Bug Report"
        title.foreground = EntropyColour.COLOUR_HELP_TITLE
        title.font = Font("Tahoma", Font.BOLD, 18)
        title.isEditable = false
        title.setBounds(21, 25, 192, 30)
        add(title)
        paneIntro.font = Font("SansSerif", Font.PLAIN, 14)
        paneIntro.contentType = "text/html"
        paneIntro.text =
            "<html>You can report any issues you may find through the 'Bug Report' feature, located under the 'Help' menu. Please include as much information as you can about what you were doing when you encountered the problem. \r\n<br><br>\r\nSending the bug report will also send logs that will help me to investigate, however these are lost when you exit the application so please send the bug report before doing so. In more severe circumstances the logs will get emailed automatically, so if you see the following error message you don't need to send a bug report yourself:</html>"
        paneIntro.setBounds(21, 408, 429, 190)
        add(paneIntro)

        val lblNewLabel = JLabel("")
        lblNewLabel.icon = ImageIcon(MiscBugReport::class.java.getResource("/help/bugReport.png"))
        lblNewLabel.setBounds(70, 66, 320, 321)
        add(lblNewLabel)

        val label = JLabel("")
        label.icon = ImageIcon(MiscBugReport::class.java.getResource("/help/bugError.png"))
        label.setBounds(35, 610, 379, 105)
        add(label)

        finaliseComponents()
    }
}
