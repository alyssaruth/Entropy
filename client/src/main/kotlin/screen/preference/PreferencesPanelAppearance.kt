package screen.preference

import achievement.Reward
import bean.ComboBoxItem
import java.awt.Color
import java.awt.Dimension
import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.util.Vector
import javax.swing.BorderFactory
import javax.swing.ButtonGroup
import javax.swing.ComboBoxModel
import javax.swing.DefaultComboBoxModel
import javax.swing.ImageIcon
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JRadioButton
import javax.swing.JSeparator
import javax.swing.JTextPane
import javax.swing.SwingConstants
import javax.swing.UIManager
import javax.swing.border.LineBorder
import javax.swing.text.DefaultCaret
import `object`.DisabledComboBoxModel
import preference.DECK_DESIGN_ALTERNATE
import preference.DECK_DESIGN_CLASSIC
import preference.FOUR_COLOURS
import preference.JOKER_DESIGN_CLASSIC
import preference.JOKER_DESIGN_DEVELOPERS
import preference.PreferenceSetting
import preference.TWO_COLOURS
import preference.getPreference
import screen.MainScreen
import screen.ScreenCache
import util.ClientGlobals.preferenceStore
import util.GameUtil

class PreferencesPanelAppearance(parent: PreferencesDialog) :
    AbstractPreferencesPanel(parent), ItemListener {
    private var deckDesign = ""
    private var jokerDesign = ""
    private var cardBacks = ""
    private var numberOfColours = ""
    private var lookAndFeel = ""

    private val lblVisualPreferences = JLabel("Appearance")
    private val separator_1 = JSeparator()
    private val deckDesignPanel = JPanel()
    private val bgDeckDesign = ButtonGroup()
    private val lblCardDesign = JLabel("Card Design")
    private val rdbtnClassicDesign = JRadioButton("Classic")
    private val rdbtnMinimalistDesign = JRadioButton("Minimalist")
    private val deckPreviewPanel = JPanel()
    private val labelJc = JLabel()
    private val labelQd = JLabel()
    private val labelKh = JLabel()
    private val labelAs = JLabel()
    private val jokerDesignPanel = JPanel()
    private val bgJokerDesign = ButtonGroup()
    private val rdbtnClassicJokers = JRadioButton("Classic")
    private val rdbtnDeveloperJokers = JRadioButton("Developers")
    private val lblJokerDesign = JLabel("Joker Design")
    private val jokerPreviewPanel = JPanel()
    private val labelJo0 = JLabel()
    private val labelJo1 = JLabel()
    private val labelJo2 = JLabel()
    private val labelJo3 = JLabel()
    private val cbFourColour = JCheckBox("Use 4 colour deck")
    private val backDesignPanel = JPanel()
    private val lblCardBacks = JLabel("Back Design")
    private val comboBoxBacks = JComboBox<ComboBoxItem<String>>()
    private val comboBoxLookAndFeel = JComboBox<String?>()
    private val labelBack = JLabel()
    private val lblLookFeel = JLabel("Look & Feel")
    private val txtpnLookAndFeelDisclaimer = JTextPane()
    private val panelLookAndFeel = JPanel()

    init {
        preferredSize = Dimension(400, 760)
        setLayout(null)

        bgDeckDesign.add(rdbtnClassicDesign)
        bgDeckDesign.add(rdbtnMinimalistDesign)
        bgJokerDesign.add(rdbtnClassicJokers)
        bgJokerDesign.add(rdbtnDeveloperJokers)
        deckDesignPanel.setBounds(17, 80, 374, 58)
        add(deckDesignPanel)
        deckDesignPanel.setBorder(LineBorder(Color.GRAY))
        deckDesignPanel.setLayout(null)
        rdbtnClassicDesign.setBounds(6, 30, 72, 23)
        deckDesignPanel.add(rdbtnClassicDesign)
        rdbtnMinimalistDesign.name = "MinimalistDesignRadio"
        rdbtnMinimalistDesign.setBounds(80, 30, 146, 23)
        deckDesignPanel.add(rdbtnMinimalistDesign)
        deckDesignPanel.add(lblCardDesign)
        deckPreviewPanel.setBounds(17, 137, 374, 136)
        add(deckPreviewPanel)
        deckPreviewPanel.setBorder(LineBorder(Color.GRAY))
        deckPreviewPanel.setLayout(null)
        labelKh.setBounds(187, 20, 72, 96)
        deckPreviewPanel.add(labelKh)
        labelJc.setBounds(43, 20, 72, 96)
        deckPreviewPanel.add(labelJc)
        labelQd.setBounds(115, 20, 72, 96)
        deckPreviewPanel.add(labelQd)
        labelAs.setBounds(259, 20, 72, 96)
        deckPreviewPanel.add(labelAs)
        lblCardDesign.setHorizontalAlignment(SwingConstants.CENTER)
        lblCardDesign.setFont(Font("Tahoma", Font.BOLD, 14))
        lblCardDesign.setBounds(131, 8, 113, 17)
        lblVisualPreferences.setHorizontalAlignment(SwingConstants.CENTER)
        lblVisualPreferences.setBounds(178, 12, 72, 17)
        add(lblVisualPreferences)
        lblVisualPreferences.setFont(Font("Tahoma", Font.PLAIN, 14))
        add(separator_1)
        jokerDesignPanel.setLayout(null)
        jokerDesignPanel.setBorder(LineBorder(Color.GRAY))
        jokerDesignPanel.setBounds(17, 288, 374, 58)
        add(jokerDesignPanel)
        rdbtnClassicJokers.setBounds(6, 30, 72, 23)
        jokerDesignPanel.add(rdbtnClassicJokers)
        rdbtnDeveloperJokers.name = "DeveloperJokersRadio"
        rdbtnDeveloperJokers.setBounds(80, 30, 102, 23)
        jokerDesignPanel.add(rdbtnDeveloperJokers)
        separator_1.setBounds(0, 38, 429, 2)
        lblJokerDesign.setHorizontalAlignment(SwingConstants.CENTER)
        lblJokerDesign.setFont(Font("Tahoma", Font.BOLD, 14))
        lblJokerDesign.setBounds(136, 8, 102, 17)
        jokerDesignPanel.add(lblJokerDesign)
        jokerPreviewPanel.setLayout(null)
        jokerPreviewPanel.setBorder(LineBorder(Color.GRAY))
        jokerPreviewPanel.setBounds(17, 345, 374, 136)
        add(jokerPreviewPanel)
        labelJo2.setBounds(187, 20, 72, 96)
        jokerPreviewPanel.add(labelJo2)
        labelJo0.setBounds(43, 20, 72, 96)
        jokerPreviewPanel.add(labelJo0)
        labelJo1.setBounds(115, 20, 72, 96)
        jokerPreviewPanel.add(labelJo1)
        labelJo3.setBounds(259, 20, 72, 96)
        jokerPreviewPanel.add(labelJo3)
        cbFourColour.name = "FourColourCheckbox"
        cbFourColour.setBounds(17, 48, 135, 23)
        add(cbFourColour)
        backDesignPanel.setLayout(null)
        backDesignPanel.setBorder(LineBorder(Color.GRAY))
        backDesignPanel.setBounds(17, 496, 374, 114)
        add(backDesignPanel)
        lblCardBacks.setHorizontalAlignment(SwingConstants.CENTER)
        lblCardBacks.setFont(Font("Tahoma", Font.BOLD, 14))
        lblCardBacks.setBounds(127, 8, 102, 17)
        backDesignPanel.add(lblCardBacks)
        labelBack.name = "BackPreview"
        labelBack.setBounds(23, 9, 72, 96)
        backDesignPanel.add(labelBack)
        comboBoxBacks.name = "backs"
        comboBoxBacks.setBounds(121, 46, 190, 22)
        backDesignPanel.add(comboBoxBacks)
        panelLookAndFeel.setLayout(null)
        panelLookAndFeel.setBorder(LineBorder(Color.GRAY))
        panelLookAndFeel.setBounds(17, 625, 374, 114)
        add(panelLookAndFeel)
        lblLookFeel.setHorizontalAlignment(SwingConstants.CENTER)
        lblLookFeel.setFont(Font("Tahoma", Font.BOLD, 14))
        lblLookFeel.setBounds(127, 8, 102, 17)
        panelLookAndFeel.add(lblLookFeel)
        comboBoxLookAndFeel.setBounds(86, 81, 190, 22)
        panelLookAndFeel.add(comboBoxLookAndFeel)
        val caret = DefaultCaret()
        caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE)
        txtpnLookAndFeelDisclaimer.setCaret(caret)
        txtpnLookAndFeelDisclaimer.setText(
            "Note: These options are experimental. You will need to restart for this to take effect."
        )
        txtpnLookAndFeelDisclaimer.setBounds(10, 28, 354, 40)
        txtpnLookAndFeelDisclaimer.setOpaque(false)
        txtpnLookAndFeelDisclaimer.setBorder(BorderFactory.createEmptyBorder())
        txtpnLookAndFeelDisclaimer.setBackground(Color(0, 0, 0, 0))
        txtpnLookAndFeelDisclaimer.setEditable(false)
        panelLookAndFeel.add(txtpnLookAndFeelDisclaimer)

        rdbtnClassicDesign.addActionListener(this)
        rdbtnMinimalistDesign.addActionListener(this)
        rdbtnClassicJokers.addActionListener(this)
        rdbtnDeveloperJokers.addActionListener(this)
        comboBoxBacks.addActionListener(this)
        cbFourColour.addItemListener(this)
    }

    /** Abstract methods */
    override fun initVariables() {
        getVariablesFromPrefs()

        selectRadioButtonsBasedOnDirectories()
        refreshDeckPreview()
        refreshJokerPreview()

        selectCardBackBasedOnPreference()
        refreshCardBackPreview()
        setLookAndFeelComboBoxModel()

        hideLockedFields()
    }

    override fun valid(): Boolean {
        return true
    }

    override fun savePreferences() {
        preferenceStore.save(PreferenceSetting.DeckDesign, deckDesign)
        preferenceStore.save(PreferenceSetting.JokerDesign, jokerDesign)
        preferenceStore.save(PreferenceSetting.NumberOfColours, numberOfColours)
        preferenceStore.save(PreferenceSetting.CardBacks, cardBacks)
        preferenceStore.save(
            PreferenceSetting.LookAndFeel,
            comboBoxLookAndFeel.selectedItem as String,
        )

        ScreenCache.get<MainScreen>().fireAppearancePreferencesChange()
    }

    private fun getVariablesFromPrefs() {
        deckDesign = getPreference(PreferenceSetting.DeckDesign)
        jokerDesign = getPreference(PreferenceSetting.JokerDesign)
        numberOfColours = getPreference(PreferenceSetting.NumberOfColours)
        cardBacks = getPreference(PreferenceSetting.CardBacks)
        lookAndFeel = getPreference(PreferenceSetting.LookAndFeel)
    }

    private fun hideLockedFields() {
        toggleLockedComponent(cbFourColour, Reward.FourColours)
        toggleLockedComponent(rdbtnMinimalistDesign, Reward.MinimalistDeck)
        toggleLockedComponent(rdbtnDeveloperJokers, Reward.DeveloperSet)
    }

    private fun refreshDeckPreview() {
        val jackClubs = GameUtil.getImageForCard("Jc", deckDesign, jokerDesign, numberOfColours)
        val queenDiamonds = GameUtil.getImageForCard("Qd", deckDesign, jokerDesign, numberOfColours)
        val kingHearts = GameUtil.getImageForCard("Kh", deckDesign, jokerDesign, numberOfColours)
        val aceSpades = GameUtil.getImageForCard("As", deckDesign, jokerDesign, numberOfColours)
        labelJc.setIcon(jackClubs)
        labelQd.setIcon(queenDiamonds)
        labelKh.setIcon(kingHearts)
        labelAs.setIcon(aceSpades)
    }

    private fun refreshJokerPreview() {
        val jo0 = GameUtil.getImageForCard("Jo0", deckDesign, jokerDesign, numberOfColours)
        val jo1 = GameUtil.getImageForCard("Jo1", deckDesign, jokerDesign, numberOfColours)
        val jo2 = GameUtil.getImageForCard("Jo2", deckDesign, jokerDesign, numberOfColours)
        val jo3 = GameUtil.getImageForCard("Jo3", deckDesign, jokerDesign, numberOfColours)
        labelJo0.setIcon(jo0)
        labelJo1.setIcon(jo1)
        labelJo2.setIcon(jo2)
        labelJo3.setIcon(jo3)
    }

    private fun selectCardBackBasedOnPreference() {
        val backs = initialiseBacksVector()

        val model: ComboBoxModel<ComboBoxItem<String>> = DisabledComboBoxModel(backs)
        comboBoxBacks.setModel(model)

        val selectedItem = backs.firstOrNull { it.hiddenData == cardBacks } ?: backs.firstElement()
        comboBoxBacks.setSelectedItem(selectedItem)
    }

    private fun initialiseBacksVector(): Vector<ComboBoxItem<String>> {
        val backs = Vector<ComboBoxItem<String>>()

        backs.addElement(ComboBoxItem("backBlue", "Blue", true))
        backs.addElement(ComboBoxItem("backRed", "Red", true))

        backs.addElement(makeComboBoxItem("backGreen", "Green", Reward.FourColours))
        backs.addElement(makeComboBoxItem("backPurple", "Purple", Reward.NegativeJacks))
        backs.addElement(makeComboBoxItem("backOrange", "Orange", Reward.Blind))
        backs.addElement(makeComboBoxItem("backLightBlue", "Light Blue", Reward.MinimalistDeck))
        backs.addElement(makeComboBoxItem("backPink", "Pink", Reward.Vectropy))
        backs.addElement(makeComboBoxItem("backSilver", "Silver", Reward.CardReveal))
        backs.addElement(makeComboBoxItem("backGold", "Gold", Reward.ExtraSuits))
        backs.addElement(makeComboBoxItem("backMatrix", "Matrix", Reward.Illegal))
        backs.addElement(makeComboBoxItem("backCosmic", "Cosmic", Reward.DeveloperSet))
        backs.addElement(makeComboBoxItem("backRainbow", "Rainbow", Reward.Cheats))

        return backs
    }

    private fun makeComboBoxItem(assetName: String, displayName: String, reward: Reward) =
        if (reward.isUnlocked()) {
            ComboBoxItem(assetName, displayName, true)
        } else {
            ComboBoxItem("", reward.threshold.toString() + " achievements to unlock", false)
        }

    private fun setLookAndFeelComboBoxModel() {
        val backs = Vector<String?>()
        for (info in UIManager.getInstalledLookAndFeels()) {
            val lookAndFeelName = info.getName()
            backs.add(lookAndFeelName)
        }

        val model: ComboBoxModel<String?> = DefaultComboBoxModel<String?>(backs)
        comboBoxLookAndFeel.setModel(model)
        comboBoxLookAndFeel.setSelectedItem(lookAndFeel)
    }

    private fun refreshCardBackPreview() {
        val selection = comboBoxBacks.selectedItem as ComboBoxItem<String>?
        if (selection == null) {
            cardBacks = comboBoxBacks.getItemAt(0)!!.hiddenData
        } else {
            cardBacks = selection.hiddenData
        }

        val back = ImageIcon(javaClass.getResource("/backs/$cardBacks.png"))
        labelBack.setIcon(back)
    }

    private fun selectRadioButtonsBasedOnDirectories() {
        cbFourColour.setSelected(numberOfColours == FOUR_COLOURS)

        if (deckDesign == DECK_DESIGN_CLASSIC) {
            rdbtnClassicDesign.setSelected(true)
        } else if (deckDesign == DECK_DESIGN_ALTERNATE) {
            rdbtnMinimalistDesign.setSelected(true)
        }

        if (jokerDesign == JOKER_DESIGN_CLASSIC) {
            rdbtnClassicJokers.setSelected(true)
        } else if (jokerDesign == JOKER_DESIGN_DEVELOPERS) {
            rdbtnDeveloperJokers.setSelected(true)
        }
    }

    override fun actionPerformed(arg0: ActionEvent) {
        val source = arg0.getSource()
        if (source === rdbtnClassicDesign) {
            deckDesign = DECK_DESIGN_CLASSIC
            refreshDeckPreview()
        } else if (source === rdbtnMinimalistDesign) {
            deckDesign = DECK_DESIGN_ALTERNATE
            refreshDeckPreview()
        } else if (source === rdbtnClassicJokers) {
            jokerDesign = JOKER_DESIGN_CLASSIC
            refreshJokerPreview()
        } else if (source === rdbtnDeveloperJokers) {
            jokerDesign = JOKER_DESIGN_DEVELOPERS
            refreshJokerPreview()
        } else if (source === comboBoxBacks) {
            refreshCardBackPreview()
        }
    }

    override fun itemStateChanged(arg0: ItemEvent?) {
        numberOfColours =
            if (cbFourColour.isSelected) {
                FOUR_COLOURS
            } else {
                TWO_COLOURS
            }

        refreshDeckPreview()
        refreshJokerPreview()
    }
}
