package screen.preference

import game.GameMode
import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.ComboBoxModel
import javax.swing.DefaultComboBoxModel
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JLabel
import javax.swing.JMenuItem
import javax.swing.JOptionPane
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JSeparator
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.ListSelectionModel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities
import javax.swing.table.TableModel
import javax.swing.table.TableRowSorter
import `object`.ApiStrategy
import `object`.LimitedDocument
import preference.PreferenceSetting
import preference.getPreference
import screen.ApiAmendDialog
import util.ApiUtil
import util.ClientGlobals.preferenceStore
import util.CpuStrategies
import util.DialogUtilNew
import util.TableUtil.DefaultModel
import util.TableUtil.SimpleRenderer

class PreferencesPanelPlayers(parent: PreferencesDialog) :
    AbstractPreferencesPanel(parent), MouseListener, ItemListener {
    private var playerName = "Player"
    private var opponentOneName = "Mark"
    private var opponentTwoName = "Dave"
    private var opponentThreeName = "Tom"
    private var opponentTwoEnabled = false
    private var opponentThreeEnabled = false
    private var opponentOneStrategy: String = "Mark"
    private var opponentTwoStrategy: String = "Basic"
    private var opponentThreeStrategy: String = "Basic"
    private var apiStrategies: List<ApiStrategy> = emptyList()
    private var gameMode: GameMode = GameMode.Entropy

    private val lblPlayers = JLabel("Players")
    private val separator_2 = JSeparator()
    private val lblPlayerName = JLabel("Player Name")
    private val playerNameField = JTextField()
    private val opponentOneNameField = JTextField()
    private val opponentTwoNameField = JTextField()
    private val opponentThreeNameField = JTextField()
    private val cbOpponentTwo = JCheckBox()
    private val cbOpponentThree = JCheckBox()
    private val opponentOneStrat = JComboBox<String?>()
    private val opponentTwoStrat = JComboBox<String?>()
    private val opponentThreeStrat = JComboBox<String?>()
    private val label = JLabel("Note: Changes will not take effect until you start a new game.")
    private val separator_4 = JSeparator()
    private val lblApiHeader = JLabel("API Options")
    private val scrollPane = JScrollPane()
    private val tableApiStrategies = JTable()
    private val popupMenu = JPopupMenu()
    private val amendItem = JMenuItem("Amend")
    private val deleteItem = JMenuItem("Delete")
    private val enableItem = JMenuItem("Enable")
    private val btnNewApiStrategy = JButton("New API Strategy")

    init {
        setLayout(null)

        separator_2.setBounds(0, 38, 434, 2)
        add(separator_2)
        lblPlayers.setBounds(172, 12, 70, 17)
        add(lblPlayers)
        lblPlayers.setHorizontalAlignment(SwingConstants.CENTER)
        lblPlayers.setFont(Font("Tahoma", Font.PLAIN, 14))
        playerNameField.setDocument(LimitedDocument(10))
        opponentOneNameField.setDocument(LimitedDocument(10))
        opponentTwoNameField.setDocument(LimitedDocument(10))
        opponentThreeNameField.setDocument(LimitedDocument(10))
        opponentOneNameField.setBounds(60, 102, 86, 22)
        add(opponentOneNameField)
        opponentOneNameField.text = opponentOneName
        opponentOneNameField.setColumns(10)
        cbOpponentTwo.setBounds(22, 143, 29, 23)
        add(cbOpponentTwo)
        cbOpponentThree.setBounds(22, 185, 29, 23)
        add(cbOpponentThree)
        opponentTwoNameField.setBounds(60, 144, 86, 22)
        add(opponentTwoNameField)
        opponentTwoNameField.text = opponentTwoName
        opponentTwoNameField.setColumns(10)
        opponentThreeNameField.setBounds(60, 186, 86, 22)
        add(opponentThreeNameField)
        opponentThreeNameField.text = opponentThreeName
        opponentThreeNameField.setColumns(10)
        opponentTwoStrat.setBounds(172, 144, 197, 22)
        add(opponentTwoStrat)
        opponentThreeStrat.setBounds(172, 186, 197, 22)
        add(opponentThreeStrat)
        opponentOneStrat.setBounds(172, 102, 197, 22)
        add(opponentOneStrat)
        playerNameField.setBounds(212, 60, 86, 22)
        add(playerNameField)
        playerNameField.setColumns(10)
        lblPlayerName.setBounds(122, 60, 80, 22)
        add(lblPlayerName)
        label.setFont(Font("Tahoma", Font.ITALIC, 11))
        label.setBounds(65, 395, 304, 14)
        add(label)
        lblApiHeader.setHorizontalAlignment(SwingConstants.CENTER)
        lblApiHeader.setFont(Font("Tahoma", Font.PLAIN, 14))
        lblApiHeader.setBounds(171, 240, 86, 17)
        add(lblApiHeader)
        separator_4.setBounds(0, 265, 429, 2)
        add(separator_4)
        scrollPane.setBounds(17, 307, 402, 80)
        add(scrollPane)
        tableApiStrategies.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        scrollPane.setViewportView(tableApiStrategies)
        btnNewApiStrategy.setBounds(17, 278, 151, 23)
        add(btnNewApiStrategy)
        tableApiStrategies.getTableHeader().setReorderingAllowed(false)
        popupMenu.add(enableItem)
        popupMenu.add(amendItem)
        popupMenu.add(deleteItem)

        btnNewApiStrategy.addActionListener(this)
        enableItem.addActionListener(this)
        deleteItem.addActionListener(this)
        amendItem.addActionListener(this)
        tableApiStrategies.addMouseListener(this)
        cbOpponentTwo.addItemListener(this)
        cbOpponentThree.addItemListener(this)
    }

    /** Abstract methods */
    override fun initVariables() {
        getVariablesFromPrefs()

        buildApiTable()
        setPlayerNames()
        setOpponentEnablementAndStrategies()
        setOpponentStrategies()
    }

    override fun valid(): Boolean {
        val nameOne = playerNameField.getText()
        val nameTwo = opponentOneNameField.getText()
        val nameThree = opponentTwoNameField.getText()
        val nameFour = opponentThreeNameField.getText()

        if (nameOne.isEmpty() || nameTwo.isEmpty() || nameThree.isEmpty() || nameFour.isEmpty()) {
            DialogUtilNew.showError("You must enter a name for each player.")
            return false
        }

        return true
    }

    override fun savePreferences() {
        playerName = playerNameField.getText()
        opponentOneName = opponentOneNameField.getText()
        opponentTwoName = opponentTwoNameField.getText()
        opponentThreeName = opponentThreeNameField.getText()
        opponentOneStrategy = opponentOneStrat.selectedItem as String
        opponentTwoStrategy = opponentTwoStrat.selectedItem as String
        opponentThreeStrategy = opponentThreeStrat.selectedItem as String

        preferenceStore.save(PreferenceSetting.PlayerName, playerName)
        preferenceStore.save(PreferenceSetting.OpponentOneName, opponentOneName)
        preferenceStore.save(PreferenceSetting.OpponentTwoName, opponentTwoName)
        preferenceStore.save(PreferenceSetting.OpponentThreeName, opponentThreeName)
        preferenceStore.save(PreferenceSetting.OpponentTwoEnabled, opponentTwoEnabled)
        preferenceStore.save(PreferenceSetting.OpponentThreeEnabled, opponentThreeEnabled)
        preferenceStore.save(PreferenceSetting.OpponentOneStrategy, opponentOneStrategy)
        preferenceStore.save(PreferenceSetting.OpponentTwoStrategy, opponentTwoStrategy)
        preferenceStore.save(PreferenceSetting.OpponentThreeStrategy, opponentThreeStrategy)

        ApiUtil.saveApiStrategiesToPreferences(apiStrategies)
    }

    private fun getVariablesFromPrefs() {
        playerName = getPreference(PreferenceSetting.PlayerName)
        opponentOneName = getPreference(PreferenceSetting.OpponentOneName)
        opponentTwoName = getPreference(PreferenceSetting.OpponentTwoName)
        opponentThreeName = getPreference(PreferenceSetting.OpponentThreeName)
        opponentTwoEnabled = getPreference(PreferenceSetting.OpponentTwoEnabled)
        opponentThreeEnabled = getPreference(PreferenceSetting.OpponentThreeEnabled)
        opponentOneStrategy = getPreference(PreferenceSetting.OpponentOneStrategy)
        opponentTwoStrategy = getPreference(PreferenceSetting.OpponentTwoStrategy)
        opponentThreeStrategy = getPreference(PreferenceSetting.OpponentThreeStrategy)
        apiStrategies = ApiUtil.getApiStrategiesFromPreferences()

        gameMode = GameMode.valueOf(getPreference(PreferenceSetting.GameMode))
    }

    private fun setPlayerNames() {
        playerNameField.text = playerName
        opponentOneNameField.text = opponentOneName
        opponentTwoNameField.text = opponentTwoName
        opponentThreeNameField.text = opponentThreeName
    }

    private fun setOpponentEnablementAndStrategies() {
        cbOpponentThree.setEnabled(opponentTwoEnabled)
        cbOpponentTwo.setEnabled(!opponentThreeEnabled)

        cbOpponentTwo.setSelected(opponentTwoEnabled)
        cbOpponentThree.setSelected(opponentThreeEnabled)
        opponentTwoNameField.setEnabled(opponentTwoEnabled)
        opponentThreeNameField.setEnabled(opponentThreeEnabled)
        opponentTwoStrat.setEnabled(opponentTwoEnabled)
        opponentThreeStrat.setEnabled(opponentThreeEnabled)
    }

    private fun setOpponentStrategies() {
        opponentOneStrat.setSelectedItem(opponentOneStrategy)
        opponentTwoStrat.setSelectedItem(opponentTwoStrategy)
        opponentThreeStrat.setSelectedItem(opponentThreeStrategy)
    }

    private fun buildApiTable() {
        val model = DefaultModel()
        tableApiStrategies.setModel(model)

        // Columns
        model.addColumn("Name")
        model.addColumn("Port")
        model.addColumn("Game")
        model.addColumn("Messaging")
        model.addColumn("Enabled")

        // Centre rendering for everything but the last column
        for (i in 0..<model.columnCount - 1) {
            tableApiStrategies.getColumnModel().getColumn(i).setCellRenderer(SimpleRenderer(null))
        }

        // Sorting
        val sorter = TableRowSorter<TableModel?>(model)
        tableApiStrategies.setRowSorter(sorter)

        // Populate the rows
        apiStrategies.forEach { model.addRow(it.tableModelRow) }

        updateStrategySelection(gameMode)
    }

    fun updateStrategySelection(gameMode: GameMode) {
        this.gameMode = gameMode

        val allStrategies =
            CpuStrategies.getAllStrategies(gameMode == GameMode.Entropy, apiStrategies)

        var comboModel: ComboBoxModel<String?> = DefaultComboBoxModel(allStrategies)
        opponentOneStrat.setModel(comboModel)
        comboModel = DefaultComboBoxModel(allStrategies)
        opponentTwoStrat.setModel(comboModel)
        comboModel = DefaultComboBoxModel(allStrategies)
        opponentThreeStrat.setModel(comboModel)
    }

    private fun enableApi(strategy: ApiStrategy) {
        val question =
            "Strategy ${strategy.name} was disabled due to the following error:\n\n${strategy.error}\n\nWould you like to re-enable it?"

        val option = DialogUtilNew.showQuestion(question, false)
        if (option == JOptionPane.YES_OPTION) {
            strategy.error = ""
            buildApiTable()
        }
    }

    private fun amendApi(strategy: ApiStrategy?) {
        ApiAmendDialog.amendStrategy(strategy)
        buildApiTable()
    }

    private fun deleteApi(strategy: ApiStrategy) {
        val question = "Are you sure you want to delete the " + strategy.getName() + " strategy?"
        val option = DialogUtilNew.showQuestion(question, false)
        if (option == JOptionPane.YES_OPTION) {
            apiStrategies = apiStrategies - strategy
            buildApiTable()
        }
    }

    private fun getSelectedStrategyFromTable(): ApiStrategy? {
        val row = tableApiStrategies.selectedRow
        if (row == -1) {
            return null
        }

        val internalRow = tableApiStrategies.convertRowIndexToModel(row)
        val model = tableApiStrategies.model as DefaultModel
        val name = model.getValueAt(internalRow, 0) as String

        return apiStrategies.find { it.name == name }
    }

    override fun actionPerformed(arg0: ActionEvent) {
        val source = arg0.getSource()
        if (source === btnNewApiStrategy) {
            val strategy: ApiStrategy? = ApiAmendDialog.createStrategy()
            if (strategy != null) {
                apiStrategies = apiStrategies + strategy
                buildApiTable()
            }
        } else if (source === amendItem) {
            getSelectedStrategyFromTable()?.let(::amendApi)
        } else if (source === deleteItem) {
            getSelectedStrategyFromTable()?.let(::deleteApi)
        } else if (source === enableItem) {
            getSelectedStrategyFromTable()?.let(::enableApi)
        }
    }

    override fun mousePressed(arg0: MouseEvent?) {}

    override fun mouseClicked(arg0: MouseEvent) {
        val strategy: ApiStrategy? = getSelectedStrategyFromTable()

        if (SwingUtilities.isRightMouseButton(arg0)) {
            val point = arg0.getPoint()
            val row = tableApiStrategies.rowAtPoint(point)
            if (!tableApiStrategies.isRowSelected(row)) {
                val column = tableApiStrategies.columnAtPoint(point)
                tableApiStrategies.changeSelection(row, column, false, false)
            }

            if (strategy != null) {
                enableItem.setEnabled(!strategy.isEnabled)

                // Show the popup menu
                popupMenu.show(arg0.component, arg0.getX(), arg0.getY())
            }
        } else if (arg0.getClickCount() == 2) {
            // Double-click
            if (strategy != null) {
                if (!strategy.isEnabled) {
                    enableApi(strategy)
                } else {
                    amendApi(strategy)
                }
            }
        }
    }

    override fun mouseExited(arg0: MouseEvent?) {}

    override fun mouseEntered(arg0: MouseEvent?) {}

    override fun mouseReleased(arg0: MouseEvent?) {}

    override fun itemStateChanged(arg0: ItemEvent) {
        val source = arg0.getSource()
        if (source === cbOpponentTwo) {
            opponentTwoEnabled = cbOpponentTwo.isSelected
            setOpponentEnablementAndStrategies()
        } else if (source === cbOpponentThree) {
            opponentThreeEnabled = cbOpponentThree.isSelected
            setOpponentEnablementAndStrategies()
        }
    }
}
