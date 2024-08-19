package help

import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import javax.swing.ImageIcon
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextPane
import util.EntropyColour
import util.Registry

class ToolsGameplaySettings : HelpPanel(), Registry {
    override val nodeName = "Gameplay Settings"
    override val panelName = "ToolsGameplaySettings"

    private val title = JTextPane()
    private val bulletThree = JLabel("")
    private val bulletFour = JLabel("")
    private val playBlindImage = JLabel("")
    private val txtpnOneTitle = JTextPane()
    private val txtpnOneExplanation = JTextPane()
    private val txtpnTwoTitle = JTextPane()
    private val txtpnTwoExplanation = JTextPane()
    private val txtpnThreeTitle = JTextPane()
    private val txtpnThreeExplanation = JTextPane()
    private val txtpnTickbox = JTextPane()
    private val txtpnFourTitle = JTextPane()
    private val txtpnIfThisIs = JTextPane()
    private val txtpnHandicap_1 = JTextPane()
    private val txtpnWithThisTicked_1 = JTextPane()
    private val blindPanel = JPanel()
    private val panel = JPanel()
    private val label = JLabel("")

    init {
        background = Color.WHITE
        layout = null
        title.foreground = EntropyColour.COLOUR_HELP_TITLE
        title.font = Font("Tahoma", Font.BOLD, 18)
        title.text = "Preferences: Gameplay"
        title.setBounds(21, 25, 259, 30)
        add(title)
        val bulletOne = JLabel("")
        bulletOne.icon =
            ImageIcon(ToolsGameplaySettings::class.java.getResource("/help/numberOne.png"))
        bulletOne.setBounds(24, 78, 25, 25)
        add(bulletOne)
        val bulletTwo = JLabel("")
        bulletTwo.icon =
            ImageIcon(ToolsGameplaySettings::class.java.getResource("/help/numberTwo.png"))
        bulletTwo.setBounds(24, 130, 25, 25)
        add(bulletTwo)
        txtpnOneTitle.font = Font("Tahoma", Font.BOLD, 12)
        txtpnOneTitle.text = "Starting Cards"
        txtpnOneTitle.setBounds(54, 132, 118, 20)
        add(txtpnOneTitle)
        txtpnOneExplanation.font = Font("Tahoma", Font.PLAIN, 12)
        txtpnOneExplanation.text =
            "A simple slider to set how many cards are dealt to each player at the start of a new game."
        txtpnOneExplanation.setBounds(54, 156, 390, 36)
        add(txtpnOneExplanation)
        txtpnTwoTitle.text = "Game Mode"
        txtpnTwoTitle.font = Font("Tahoma", Font.BOLD, 12)
        txtpnTwoTitle.setBounds(54, 80, 118, 20)
        add(txtpnTwoTitle)
        txtpnTwoExplanation.text = "Toggle between the different game modes you have unlocked."
        txtpnTwoExplanation.font = Font("Tahoma", Font.PLAIN, 12)
        txtpnTwoExplanation.setBounds(54, 104, 390, 25)
        add(txtpnTwoExplanation)
        val jokerPanel = JPanel()
        jokerPanel.background = Color.WHITE
        jokerPanel.setBounds(21, 206, 423, 128)
        add(jokerPanel)
        jokerPanel.layout = null
        bulletThree.setBounds(3, 4, 25, 25)
        jokerPanel.add(bulletThree)
        bulletThree.icon =
            ImageIcon(ToolsGameplaySettings::class.java.getResource("/help/numberThree.png"))
        txtpnThreeTitle.setBounds(33, 6, 118, 20)
        jokerPanel.add(txtpnThreeTitle)
        txtpnThreeTitle.text = "Joker Settings"
        txtpnThreeTitle.font = Font("Tahoma", Font.BOLD, 12)
        txtpnThreeExplanation.setBounds(33, 30, 390, 19)
        jokerPanel.add(txtpnThreeExplanation)
        txtpnThreeExplanation.text = "Settings to vary how many jokers are included in the deck:"
        txtpnThreeExplanation.font = Font("Tahoma", Font.PLAIN, 12)
        txtpnTickbox.contentType = "text/html"
        txtpnTickbox.text =
            "<html><font face=\"Tahoma\" size=\"3\"><b>- Tickbox:</b> Whether jokers are included or not. <br>\r\n<b>- Quantity:</b> How many jokers to add, between 1 and 4. <br>\r\n<b>- Value:</b> The worth of each joker, between 2 and 4. Jokers will be worth this many of every suit. </font></html>"
        txtpnTickbox.setBounds(33, 60, 380, 68)
        jokerPanel.add(txtpnTickbox)
        panel.setBounds(21, 342, 423, 104)
        add(panel)
        panel.layout = null
        panel.background = Color.WHITE
        label.icon =
            ImageIcon(ToolsGameplaySettings::class.java.getResource("/help/numberFour.png"))
        label.setBounds(3, 4, 25, 25)
        panel.add(label)
        txtpnHandicap_1.text = "Handicap"
        txtpnHandicap_1.font = Font("Tahoma", Font.BOLD, 12)
        txtpnHandicap_1.setBounds(33, 6, 118, 20)
        panel.add(txtpnHandicap_1)
        txtpnWithThisTicked_1.text =
            "With this ticked, you will be dealt less cards than your opponents at the start of a new game. The number represents how many cards less you will be dealt - so the higher the number, the larger your disadvantage will be. "
        txtpnWithThisTicked_1.font = Font("Tahoma", Font.PLAIN, 12)
        txtpnWithThisTicked_1.setBounds(33, 30, 390, 66)
        panel.add(txtpnWithThisTicked_1)
        blindPanel.layout = null
        blindPanel.background = Color.WHITE
        blindPanel.setBounds(21, 460, 423, 296)
        add(blindPanel)
        bulletFour.setBounds(3, 4, 25, 25)
        blindPanel.add(bulletFour)
        bulletFour.icon =
            ImageIcon(ToolsGameplaySettings::class.java.getResource("/help/numberFive.png"))
        txtpnFourTitle.font = Font("Tahoma", Font.BOLD, 12)
        txtpnFourTitle.setBounds(33, 6, 217, 20)
        blindPanel.add(txtpnFourTitle)
        txtpnIfThisIs.isOpaque = false
        txtpnIfThisIs.font = Font("Tahoma", Font.PLAIN, 12)
        txtpnIfThisIs.setBounds(33, 37, 380, 66)
        blindPanel.add(txtpnIfThisIs)
        playBlindImage.icon =
            ImageIcon(
                ToolsGameplaySettings::class.java.getResource("/help/playingBlind_backBlue.png")
            )
        playBlindImage.setBounds(128, 114, 199, 157)
        blindPanel.add(playBlindImage)
    }

    override fun refresh() {
        val unlockedBlind = Registry.rewards.getBoolean(Registry.REWARDS_BOOLEAN_BLIND, false)
        blindPanel.isVisible = unlockedBlind

        if (unlockedBlind) {
            preferredSize = Dimension(455, 750)
            txtpnFourTitle.text = "Blind Play"
            txtpnIfThisIs.text =
                "If this is ticked, your cards will be dealt face-down at the start of every round. \r\nYou will still be able to view your cards at any time by pressing the eye symbol below them:"
        } else {
            preferredSize = Dimension(455, 450)
            txtpnFourTitle.text = ""
            txtpnIfThisIs.text = ""
        }
    }
}
