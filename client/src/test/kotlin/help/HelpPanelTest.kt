package help

import io.kotest.matchers.shouldBe
import java.awt.Color
import java.awt.Font
import javax.swing.JTextPane
import main.kotlin.testCore.AbstractTest
import org.junit.jupiter.api.Test
import util.EntropyColour

class HelpPanelTest : AbstractTest() {
    @Test
    fun `should report whether text is contained`() {
        val panel = FakeHelpPanel()

        panel.contains("first") shouldBe true
        panel.contains("second") shouldBe true
        panel.contains("third") shouldBe false
    }
}

class FakeHelpPanel : HelpPanel() {
    override val nodeName = "Fake Panel"

    private val title = JTextPane()
    private val paneOne = JTextPane()
    private val paneTwo = JTextPane()

    init {
        background = Color.WHITE
        layout = null
        paneOne.font = Font("SansSerif", Font.PLAIN, 14)
        paneOne.contentType = "text/html"
        paneOne.text = "<html>First panel.</html>"
        paneOne.setBounds(21, 54, 429, 220)
        add(paneOne)
        paneTwo.text = "<html>Second panel.</html>"
        add(paneTwo)
        title.foreground = EntropyColour.COLOUR_HELP_TITLE
        title.font = Font("Tahoma", Font.BOLD, 18)
        title.text = "Fake Panel"
        title.setBounds(21, 25, 159, 30)
        add(title)

        finaliseComponents()
    }
}
