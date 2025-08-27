package screen.preference

import achievement.Reward
import game.GameMode
import java.awt.FlowLayout
import java.awt.Font
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.ButtonGroup
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JSeparator
import javax.swing.JSlider
import javax.swing.JSpinner
import javax.swing.SpinnerNumberModel
import javax.swing.border.EmptyBorder
import javax.swing.border.TitledBorder
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import kotlin.math.max
import preference.PreferenceSetting
import preference.getPreference
import util.ClientGlobals.preferenceStore
import util.Registry

class PreferencesPanelGameplay(parent: PreferencesDialog) :
    AbstractPreferencesPanel(parent), ChangeListener, ItemListener, Registry {
    private var gameMode = GameMode.Entropy
    private var includeJokers = false
    private var numberOfCards = 5
    private var jokerQuantity = 2
    private var jokerValue = 2
    private var playBlind = false
    private var playWithHandicap = false
    private var handicapAmount = 1
    private var includeStars = false
    private var includeMoons = false
    private var negativeJacks = false
    private var cardReveal = false

    private val lblGameplay = JLabel("Gameplay")
    private val separatorTitle = JSeparator()
    private val panelGameMode = JPanel()
    private val bgMode = ButtonGroup()
    private val rdbtnEntropy = JRadioButton("Entropy")
    private val rdbtnVectropy = JRadioButton("Vectropy")
    private val panelStartingCards = JPanel()
    private val numberOfCardsSlider = JSlider()
    private val panelGameplay = JPanel()
    private val handicapPanel = JPanel()
    private val cbHandicap = JCheckBox("Handicap:")
    private val handicapAmountSpinner = JSpinner()
    private val cbPlayBlind = JCheckBox("Play Blind")
    private val cbPlayersRevealCards = JCheckBox("Players reveal cards")
    private val panelDeck = JPanel()
    private val panelJokers = JPanel()
    private val cbJokers = JCheckBox("Jokers")
    private val jokerQuantitySpinner = JSpinner()
    private val lblWorth = JLabel("worth")
    private val jokerValueSpinner = JSpinner()
    private val cbNegativeJacks = JCheckBox("Jacks worth -1")
    private val cbIncludeMoons = JCheckBox("Include Moons")
    private val cbIncludeStars = JCheckBox("Include Stars")
    private val lblWarning =
        JLabel("Note: Changes will not take effect until you start a new game.")

    init {
        setLayout(null)
        separatorTitle.setBounds(0, 38, 429, 2)
        add(separatorTitle)
        lblGameplay.setBounds(184, 12, 60, 17)
        add(lblGameplay)
        lblGameplay.setFont(Font("Tahoma", Font.PLAIN, 14))
        lblWarning.setBounds(65, 395, 304, 14)
        add(lblWarning)
        lblWarning.setFont(Font("Tahoma", Font.ITALIC, 11))
        panelGameMode.setBorder(
            TitledBorder(null, "Mode", TitledBorder.LEADING, TitledBorder.TOP, null, null)
        )
        panelGameMode.setBounds(20, 51, 165, 73)
        add(panelGameMode)
        panelGameMode.setLayout(FlowLayout(FlowLayout.LEADING, 5, 10))
        panelGameMode.add(rdbtnEntropy)
        rdbtnEntropy.setFont(Font("Tahoma", Font.PLAIN, 11))
        bgMode.add(rdbtnEntropy)
        panelGameMode.add(rdbtnVectropy)
        rdbtnVectropy.setFont(Font("Tahoma", Font.PLAIN, 11))
        bgMode.add(rdbtnVectropy)
        panelDeck.setBorder(
            TitledBorder(null, "Deck", TitledBorder.LEADING, TitledBorder.TOP, null, null)
        )
        panelDeck.setBounds(20, 253, 393, 131)
        add(panelDeck)
        panelDeck.setLayout(GridLayout(0, 1, 0, 0))
        val fl_panelJokers = panelJokers.layout as FlowLayout
        fl_panelJokers.setAlignment(FlowLayout.LEFT)
        panelJokers.setBorder(EmptyBorder(0, -5, 0, 0))
        panelDeck.add(panelJokers)
        panelJokers.add(cbJokers)
        cbJokers.setFont(Font("Tahoma", Font.PLAIN, 11))
        jokerQuantitySpinner.setFont(Font("Tahoma", Font.PLAIN, 11))
        panelJokers.add(jokerQuantitySpinner)
        lblWorth.setFont(Font("Tahoma", Font.PLAIN, 11))
        panelJokers.add(lblWorth)
        jokerValueSpinner.setFont(Font("Tahoma", Font.PLAIN, 11))
        panelJokers.add(jokerValueSpinner)
        cbJokers.addItemListener(this)
        panelDeck.add(cbNegativeJacks)
        cbNegativeJacks.setSelected(false)
        cbNegativeJacks.setFont(Font("Tahoma", Font.PLAIN, 11))
        panelDeck.add(cbIncludeMoons)
        cbIncludeMoons.setFont(Font("Tahoma", Font.PLAIN, 11))
        panelDeck.add(cbIncludeStars)
        cbIncludeStars.setFont(Font("Tahoma", Font.PLAIN, 11))
        panelGameplay.setBorder(
            TitledBorder(null, "Gameplay", TitledBorder.LEADING, TitledBorder.TOP, null, null)
        )
        panelGameplay.setBounds(20, 135, 393, 107)
        add(panelGameplay)
        panelGameplay.setLayout(GridLayout(0, 1, 0, 0))
        handicapPanel.setBorder(EmptyBorder(0, -5, 0, 0))
        val flowLayout_1 = handicapPanel.layout as FlowLayout
        flowLayout_1.setAlignment(FlowLayout.LEADING)
        panelGameplay.add(handicapPanel)
        handicapPanel.add(cbHandicap)
        cbHandicap.setFont(Font("Tahoma", Font.PLAIN, 11))
        cbHandicap.setToolTipText("Start with fewer cards than your opponents.")
        handicapAmountSpinner.setFont(Font("Tahoma", Font.PLAIN, 11))
        handicapPanel.add(handicapAmountSpinner)
        handicapAmountSpinner.setToolTipText("Start with this many cards less than your opponents.")
        cbHandicap.addItemListener(this)
        panelGameplay.add(cbPlayBlind)
        cbPlayBlind.setFont(Font("Tahoma", Font.PLAIN, 11))
        cbPlayBlind.setToolTipText(
            "Your hand will be face-down each round until you choose to view it manually."
        )
        cbPlayersRevealCards.setFont(Font("Tahoma", Font.PLAIN, 11))
        panelGameplay.add(cbPlayersRevealCards)
        panelStartingCards.setBorder(
            TitledBorder(null, "Starting cards", TitledBorder.LEADING, TitledBorder.TOP, null, null)
        )
        panelStartingCards.setBounds(195, 51, 218, 73)
        add(panelStartingCards)
        numberOfCardsSlider.setFont(Font("Tahoma", Font.PLAIN, 11))
        panelStartingCards.add(numberOfCardsSlider)
        numberOfCardsSlider.setMinorTickSpacing(1)
        numberOfCardsSlider.setPaintTicks(true)
        numberOfCardsSlider.setPaintLabels(true)
        numberOfCardsSlider.setMajorTickSpacing(4)
        numberOfCardsSlider.setToolTipText("")
        numberOfCardsSlider.setMinimum(1)
        numberOfCardsSlider.setMaximum(5)
        numberOfCardsSlider.addChangeListener(this)
        handicapAmountSpinner.addChangeListener(this)

        rdbtnEntropy.addActionListener(this)
        rdbtnVectropy.addActionListener(this)
    }

    override fun initVariables() {
        this.getVariablesFromPreferences()

        cbJokers.setSelected(includeJokers)
        cbNegativeJacks.setSelected(negativeJacks)
        cbPlayersRevealCards.setSelected(cardReveal)
        jokerQuantitySpinner.setModel(SpinnerNumberModel(jokerQuantity, 1, 4, 1))
        jokerValueSpinner.setModel(SpinnerNumberModel(jokerValue, 2, 4, 1))
        numberOfCardsSlider.setValue(numberOfCards)
        jokerQuantitySpinner.value = jokerQuantity
        jokerValueSpinner.value = jokerValue
        cbPlayBlind.setSelected(playBlind)
        cbHandicap.setSelected(playWithHandicap)
        jokerQuantitySpinner.setEnabled(includeJokers)
        lblWorth.setEnabled(includeJokers)
        jokerValueSpinner.setEnabled(includeJokers)
        handicapAmountSpinner.setEnabled(playWithHandicap)

        adjustHandicapSpinner()

        cbIncludeStars.setSelected(includeStars)
        cbIncludeMoons.setSelected(includeMoons)

        rdbtnEntropy.setSelected(gameMode == GameMode.Entropy)
        rdbtnVectropy.setSelected(gameMode == GameMode.Vectropy)

        hideLockedFields()
    }

    override fun valid(): Boolean {
        return true
    }

    override fun savePreferences() {
        numberOfCards = numberOfCardsSlider.value
        negativeJacks = cbNegativeJacks.isSelected
        cardReveal = cbPlayersRevealCards.isSelected
        jokerQuantity = if (includeJokers) jokerQuantitySpinner.value as Int else 0
        jokerValue = jokerValueSpinner.value as Int
        handicapAmount = handicapAmountSpinner.value as Int
        playBlind = cbPlayBlind.isSelected
        includeStars = cbIncludeStars.isSelected
        includeMoons = cbIncludeMoons.isSelected

        Registry.prefs.putInt(Registry.SHARED_INT_NUMBER_OF_CARDS, numberOfCards)
        Registry.prefs.putBoolean(Registry.SHARED_BOOLEAN_NEGATIVE_JACKS, negativeJacks)
        Registry.prefs.putInt(Registry.SHARED_INT_JOKER_QUANTITY, jokerQuantity)
        Registry.prefs.putInt(Registry.SHARED_INT_JOKER_VALUE, jokerValue)

        preferenceStore.save(PreferenceSetting.PlayWithHandicap, playWithHandicap)
        preferenceStore.save(PreferenceSetting.HandicapAmount, handicapAmount)
        preferenceStore.save(PreferenceSetting.PlayBlind, playBlind)
        Registry.prefs.putBoolean(Registry.SHARED_BOOLEAN_INCLUDE_STARS, includeStars)
        Registry.prefs.putBoolean(Registry.SHARED_BOOLEAN_INCLUDE_MOONS, includeMoons)
        Registry.prefs.putBoolean(Registry.SHARED_BOOLEAN_CARD_REVEAL, cardReveal)
        preferenceStore.save(PreferenceSetting.GameMode, gameMode.name)
    }

    private fun getVariablesFromPreferences() {
        numberOfCards = Registry.prefs.getInt(Registry.SHARED_INT_NUMBER_OF_CARDS, 5)
        jokerQuantity = Registry.prefs.getInt(Registry.SHARED_INT_JOKER_QUANTITY, 2)
        includeJokers = jokerQuantity > 0
        jokerValue = Registry.prefs.getInt(Registry.SHARED_INT_JOKER_VALUE, 2)
        playWithHandicap = getPreference(PreferenceSetting.PlayWithHandicap)
        handicapAmount = getPreference(PreferenceSetting.HandicapAmount)
        playBlind = getPreference(PreferenceSetting.PlayBlind)
        includeStars = Registry.prefs.getBoolean(Registry.SHARED_BOOLEAN_INCLUDE_STARS, false)
        includeMoons = Registry.prefs.getBoolean(Registry.SHARED_BOOLEAN_INCLUDE_MOONS, false)
        negativeJacks = Registry.prefs.getBoolean(Registry.SHARED_BOOLEAN_NEGATIVE_JACKS, false)
        cardReveal = Registry.prefs.getBoolean(Registry.SHARED_BOOLEAN_CARD_REVEAL, false)
        gameMode = GameMode.valueOf(getPreference<String>(PreferenceSetting.GameMode))
    }

    private fun adjustHandicapSpinner() {
        if (numberOfCards == 1) {
            cbHandicap.setSelected(false)
            cbHandicap.setEnabled(false)
            handicapAmountSpinner.setEnabled(false)
        } else {
            cbHandicap.setEnabled(true)
            handicapAmountSpinner.setEnabled(cbHandicap.isSelected)

            val newMax = max(1, numberOfCards - 1)
            if (handicapAmount > newMax) {
                handicapAmount = newMax
            }

            handicapAmountSpinner.setModel(SpinnerNumberModel(handicapAmount, 1, newMax, 1))
        }
    }

    private fun hideLockedFields() {
        toggleLockedComponent(cbNegativeJacks, Reward.NegativeJacks)
        toggleLockedComponent(cbPlayBlind, Reward.Blind)
        toggleLockedComponent(rdbtnVectropy, Reward.Vectropy)
        toggleLockedComponent(cbPlayersRevealCards, Reward.CardReveal)
        toggleLockedComponent(cbIncludeMoons, Reward.ExtraSuits)
        toggleLockedComponent(cbIncludeStars, Reward.ExtraSuits)
    }

    override fun actionPerformed(arg0: ActionEvent) {
        val originalGameMode: GameMode? = gameMode

        val source = arg0.getSource()
        if (source === rdbtnEntropy) {
            gameMode = GameMode.Entropy
        } else if (source === rdbtnVectropy) {
            gameMode = GameMode.Vectropy
        }

        if (originalGameMode != gameMode) {
            parentDialog.gameModeChanged(gameMode)
        }
    }

    override fun stateChanged(arg0: ChangeEvent) {
        val source = arg0.getSource()
        if (source === numberOfCardsSlider) {
            if (!numberOfCardsSlider.valueIsAdjusting) {
                numberOfCards = numberOfCardsSlider.value
                adjustHandicapSpinner()
            }
        } else if (source === handicapAmountSpinner) {
            handicapAmount = handicapAmountSpinner.value as Int
        }
    }

    override fun itemStateChanged(arg0: ItemEvent) {
        val source = arg0.getSource()
        if (source === cbJokers) {
            includeJokers = cbJokers.isSelected
            lblWorth.setEnabled(includeJokers)
            jokerQuantitySpinner.setEnabled(includeJokers)
            jokerValueSpinner.setEnabled(includeJokers)
        } else if (source === cbHandicap) {
            playWithHandicap = cbHandicap.isSelected
            handicapAmountSpinner.setEnabled(playWithHandicap)
        }
    }
}
