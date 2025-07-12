package help

import game.CLUBS_SYMBOL
import game.DIAMONDS_SYMBOL
import game.HEARTS_SYMBOL
import game.MOONS_SYMBOL
import game.SPADES_SYMBOL
import game.STARS_SYMBOL
import java.awt.Color
import java.awt.Font
import javax.swing.JLabel
import javax.swing.JTextPane
import javax.swing.SwingConstants
import util.EntropyColour
import util.Registry
import utils.COLOUR_SUIT_GOLD
import utils.COLOUR_SUIT_PURPLE

class FundamentalsTheDeck : HelpPanel(), Registry {
    override val nodeName = "The Deck"

    private var clubString = "clubs (<font color = \"black\">${CLUBS_SYMBOL}</font>)"
    private var diamondString = "diamonds (<font color=\"red\">${DIAMONDS_SYMBOL}</font>)"
    private var moonString: String? = null

    private val title = JTextPane()
    private val paneOne = JTextPane()

    private val clubLabel = JLabel(CLUBS_SYMBOL)
    private val diamondLabel = JLabel(DIAMONDS_SYMBOL)
    private val heartLabel = JLabel(HEARTS_SYMBOL)
    private val moonLabel = JLabel(MOONS_SYMBOL)
    private val spadeLabel = JLabel(SPADES_SYMBOL)
    private val starLabel = JLabel(STARS_SYMBOL)
    private val label_3 = JLabel("<")
    private val label_4 = JLabel("<")
    private val label_5 = JLabel("<")
    private val rightmostLabel = JLabel("<")
    private val leftmostLabel = JLabel("<")

    init {
        background = Color.WHITE
        layout = null
        paneOne.font = Font("SansSerif", Font.PLAIN, 14)
        paneOne.contentType = "text/html"
        paneOne.setBounds(21, 54, 429, 310)
        add(paneOne)
        title.text = "The Deck"
        title.foreground = EntropyColour.COLOUR_HELP_TITLE
        title.font = Font("Tahoma", Font.BOLD, 18)
        title.setBounds(21, 25, 165, 30)
        add(title)
        clubLabel.horizontalAlignment = SwingConstants.CENTER
        clubLabel.font = Font("Segoe UI Symbol", Font.PLAIN, 40)
        clubLabel.setBounds(80, 360, 65, 60)
        add(clubLabel)
        label_4.horizontalAlignment = SwingConstants.CENTER
        label_4.font = Font("Arial", Font.PLAIN, 40)
        label_4.setBounds(120, 360, 65, 60)
        add(label_4)
        diamondLabel.horizontalAlignment = SwingConstants.CENTER
        diamondLabel.foreground = Color.RED
        diamondLabel.font = Font("Segoe UI Symbol", Font.PLAIN, 40)
        diamondLabel.setBounds(160, 360, 65, 60)
        add(diamondLabel)
        label_3.horizontalAlignment = SwingConstants.CENTER
        label_3.font = Font("Arial", Font.PLAIN, 40)
        label_3.setBounds(200, 360, 65, 60)
        add(label_3)
        heartLabel.horizontalAlignment = SwingConstants.CENTER
        heartLabel.foreground = Color.RED
        heartLabel.font = Font("Segoe UI Symbol", Font.PLAIN, 40)
        heartLabel.setBounds(240, 360, 65, 60)
        add(heartLabel)
        moonLabel.horizontalAlignment = SwingConstants.CENTER
        moonLabel.foreground = COLOUR_SUIT_PURPLE
        moonLabel.font = Font("Segoe UI Symbol", Font.PLAIN, 32)
        moonLabel.setBounds(240, 360, 65, 60)
        add(moonLabel)
        label_5.horizontalAlignment = SwingConstants.CENTER
        label_5.font = Font("Arial", Font.PLAIN, 40)
        label_5.setBounds(280, 360, 65, 60)
        add(label_5)
        spadeLabel.horizontalAlignment = SwingConstants.CENTER
        spadeLabel.font = Font("Segoe UI Symbol", Font.PLAIN, 40)
        spadeLabel.setBounds(320, 360, 65, 60)
        add(spadeLabel)
        rightmostLabel.horizontalAlignment = SwingConstants.CENTER
        rightmostLabel.font = Font("Arial", Font.PLAIN, 40)
        rightmostLabel.setBounds(360, 360, 65, 60)
        add(rightmostLabel)
        leftmostLabel.horizontalAlignment = SwingConstants.CENTER
        leftmostLabel.font = Font("Arial", Font.PLAIN, 40)
        leftmostLabel.setBounds(40, 360, 65, 60)
        add(leftmostLabel)
        starLabel.horizontalAlignment = SwingConstants.CENTER
        starLabel.font = Font("Segoe UI Symbol", Font.PLAIN, 40)
        starLabel.setBounds(400, 360, 65, 60)
        starLabel.foreground = COLOUR_SUIT_GOLD
        add(starLabel)

        finaliseComponents()
    }

    override fun searchTermsToExclude() = listOf("bidding")

    private fun setPaneOneText(moonsAndStars: Boolean) {
        var paneOneText =
            ("<html>For the standard game, a normal deck of 52 cards is used. This deck is made up of four suits: " +
                clubString +
                ", " +
                diamondString +
                ", hearts (<font color=\"red\">${HEARTS_SYMBOL}</font>) and spades (${SPADES_SYMBOL}), " +
                "each of 13 cards. ")

        if (moonsAndStars) {
            paneOneText += "Two optional suits, $moonString and stars "
            paneOneText +=
                "(<font color=\"CC9900\">" +
                    STARS_SYMBOL +
                    "</font>) can also be added to the deck."
        }

        paneOneText +=
            """
            The 13 cards in each suit are the 13 ranks of cards: ace (A), two (2), three (3), four (4), five (5), six (6), seven (7), eight (8), nine (9), ten (T), jack (J), queen (Q) and king (K). 
            <br><br>
            When playing Entropy and its variants, the focus is on the suit as that’s what is used during the bidding. Each card is worth one of its own suit, irrespective of rank. For example, the five of hearts (5<font color="red">♥</font>) is worth one heart. However, aces are special cards. Not only are they worth one of their own suit, but they are also worth an extra one of all the suits. This means that the ace of spades (A♠) is worth one club, one diamond, one heart and two spades (one for being a spade and one for being an ace).
            <br><br>
            To define whether one bid is higher than another, the suits are also ordered as follows:</html>
            
            """
                .trimIndent()

        paneOne.text = paneOneText
    }

    fun fireAppearancePreferencesChange() {
        val fourColours = useFourColours()
        val clubsColour = if (fourColours) "green" else "black"
        val diamondsColour = if (fourColours) "blue" else "red"
        val moonsColour = if (fourColours) "purple" else "CC9900"

        clubString = "clubs (<font color = \"$clubsColour\">${CLUBS_SYMBOL}</font>)"
        diamondString = "diamonds (<font color=\"$diamondsColour\">${DIAMONDS_SYMBOL}</font>)"
        moonString =
            "moons (<font face=\"Segoe UI Symbol\" color=\"" +
                moonsColour +
                "\">" +
                MOONS_SYMBOL +
                "</font>)"

        val moonsAndStars = Registry.rewards.getBoolean(Registry.REWARDS_BOOLEAN_EXTRA_SUITS, false)
        setPaneOneText(moonsAndStars)

        if (fourColours) {
            clubLabel.foreground = Color(0, 128, 0)
            diamondLabel.foreground = Color.BLUE
            moonLabel.foreground = COLOUR_SUIT_PURPLE
        } else {
            clubLabel.foreground = Color.black
            diamondLabel.foreground = Color(255, 0, 0)
            moonLabel.foreground = COLOUR_SUIT_GOLD
        }
    }

    private fun refreshSuitRankingVisibility() {
        val moonsAndStars = Registry.rewards.getBoolean(Registry.REWARDS_BOOLEAN_EXTRA_SUITS, false)

        if (moonsAndStars) {
            moonLabel.isVisible = true
            starLabel.isVisible = true
            rightmostLabel.isVisible = true
            leftmostLabel.isVisible = true
            clubLabel.setBounds(0, 360, 65, 60)
            diamondLabel.setBounds(80, 360, 65, 60)
            heartLabel.setBounds(160, 360, 65, 60)
        } else {
            moonLabel.isVisible = false
            starLabel.isVisible = false
            rightmostLabel.isVisible = false
            leftmostLabel.isVisible = false
            clubLabel.setBounds(80, 360, 65, 60)
            diamondLabel.setBounds(160, 360, 65, 60)
            heartLabel.setBounds(240, 360, 65, 60)
        }
    }

    override fun refresh() {
        fireAppearancePreferencesChange()
        refreshSuitRankingVisibility()
    }
}
