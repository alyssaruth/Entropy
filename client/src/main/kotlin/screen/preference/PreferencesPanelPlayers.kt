package screen.preference

import bean.ComboBoxItem
import game.GameMode
import java.awt.Font
import java.awt.event.ActionEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
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
import `object`.LimitedDocument
import preference.PreferenceSetting
import preference.getPreference
import preference.saveJsonPreference
import screen.ApiAmendDialog
import strategy.ApiStrategy
import strategy.IStrategy
import strategy.getApiStrategiesFromPreferences
import strategy.getSelectedStrategy
import strategy.getStrategiesComboBoxModel
import strategy.getStrategy
import strategy.saveApiStrategiesToPreference
import strategy.toComboBoxItem
import util.ClientGlobals.preferenceStore
import util.DialogUtilNew
import util.TableUtil.DefaultModel
import util.TableUtil.SimpleRenderer

class PreferencesPanelPlayers(parent: PreferencesDialog) :
    AbstractPreferencesPanel(parent), MouseListener {
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
    private val opponentOneStrat = JComboBox<ComboBoxItem<IStrategy>>()
    private val opponentTwoStrat = JComboBox<ComboBoxItem<IStrategy>>()
    private val opponentThreeStrat = JComboBox<ComboBoxItem<IStrategy>>()
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
        opponentOneNameField.setColumns(10)
        cbOpponentTwo.setBounds(22, 143, 29, 23)
        add(cbOpponentTwo)
        cbOpponentThree.setBounds(22, 185, 29, 23)
        add(cbOpponentThree)
        opponentTwoNameField.setBounds(60, 144, 86, 22)
        add(opponentTwoNameField)
        opponentTwoNameField.setColumns(10)
        opponentThreeNameField.setBounds(60, 186, 86, 22)
        add(opponentThreeNameField)
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
        cbOpponentTwo.addActionListener(this)
        cbOpponentThree.addActionListener(this)
    }

    override fun initVariables() {
        getVariablesFromPrefs()

        buildApiTable()
        setPlayerNames()
        setOpponentEnablementAndStrategies(
            getPreference(PreferenceSetting.OpponentTwoEnabled),
            getPreference(PreferenceSetting.OpponentThreeEnabled),
        )
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
        val playerName = playerNameField.getText()
        val opponentOneName = opponentOneNameField.getText()
        val opponentTwoName = opponentTwoNameField.getText()
        val opponentThreeName = opponentThreeNameField.getText()
        val opponentOneStrategy = getSelectedStrategy(opponentOneStrat)
        val opponentTwoStrategy = getSelectedStrategy(opponentTwoStrat)
        val opponentThreeStrategy = getSelectedStrategy(opponentThreeStrat)

        preferenceStore.save(PreferenceSetting.PlayerName, playerName)
        preferenceStore.save(PreferenceSetting.OpponentOneName, opponentOneName)
        preferenceStore.save(PreferenceSetting.OpponentTwoName, opponentTwoName)
        preferenceStore.save(PreferenceSetting.OpponentThreeName, opponentThreeName)
        preferenceStore.save(PreferenceSetting.OpponentTwoEnabled, cbOpponentTwo.isSelected)
        preferenceStore.save(PreferenceSetting.OpponentThreeEnabled, cbOpponentThree.isSelected)

        saveJsonPreference(PreferenceSetting.OpponentOneStrategy, opponentOneStrategy)
        saveJsonPreference(PreferenceSetting.OpponentTwoStrategy, opponentTwoStrategy)
        saveJsonPreference(PreferenceSetting.OpponentThreeStrategy, opponentThreeStrategy)
        saveApiStrategiesToPreference(apiStrategies)
    }

    private fun getVariablesFromPrefs() {
        apiStrategies = getApiStrategiesFromPreferences()

        gameMode = GameMode.valueOf(getPreference(PreferenceSetting.GameMode))
    }

    private fun setPlayerNames() {
        playerNameField.text = getPreference(PreferenceSetting.PlayerName)
        opponentOneNameField.text = getPreference(PreferenceSetting.OpponentOneName)
        opponentTwoNameField.text = getPreference(PreferenceSetting.OpponentTwoName)
        opponentThreeNameField.text = getPreference(PreferenceSetting.OpponentThreeName)
    }

    private fun setOpponentEnablementAndStrategies(
        opponentTwoEnabled: Boolean,
        opponentThreeEnabled: Boolean,
    ) {
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
        opponentOneStrat.setSelectedItem(
            getStrategy(PreferenceSetting.OpponentOneStrategy).toComboBoxItem()
        )
        opponentTwoStrat.setSelectedItem(
            getStrategy(PreferenceSetting.OpponentTwoStrategy).toComboBoxItem()
        )
        opponentThreeStrat.setSelectedItem(
            getStrategy(PreferenceSetting.OpponentThreeStrategy).toComboBoxItem()
        )
    }

    private fun buildApiTable() {
        val model = DefaultModel()
        tableApiStrategies.setModel(model)

        // Columns
        model.addColumn("Name")
        model.addColumn("Port")
        model.addColumn("Game")
        model.addColumn("Enabled")

        // Centre rendering for everything but the last column
        for (i in 0..<model.columnCount - 1) {
            tableApiStrategies.columnModel.getColumn(i).setCellRenderer(SimpleRenderer(null))
        }

        // Sorting
        val sorter = TableRowSorter<TableModel?>(model)
        tableApiStrategies.setRowSorter(sorter)

        // Populate the rows
        apiStrategies.forEach { model.addRow(it.getTableModelRow().toTypedArray()) }

        updateStrategySelection(gameMode)
    }

    private fun ApiStrategy.getTableModelRow(): List<Any> {
        return listOf(name, port, supportedModes.joinToString(), lastError == null)
    }

    fun updateStrategySelection(gameMode: GameMode) {
        this.gameMode = gameMode

        updateStrategySelection(gameMode, opponentOneStrat)
        updateStrategySelection(gameMode, opponentTwoStrat)
        updateStrategySelection(gameMode, opponentThreeStrat)
    }

    private fun updateStrategySelection(
        gameMode: GameMode,
        combo: JComboBox<ComboBoxItem<IStrategy>>,
    ) {
        val selection = if (combo.selectedIndex == -1) null else getSelectedStrategy(combo)

        combo.setModel(getStrategiesComboBoxModel(gameMode, apiStrategies))
        selection?.let { combo.selectedItem = selection.toComboBoxItem() }
    }

    private fun enableApi(strategy: ApiStrategy) {
        val question =
            "Strategy ${strategy.name} was disabled due to the following error:\n\n${strategy.lastError}\n\nWould you like to re-enable it?"

        val option = DialogUtilNew.showQuestion(question, false)
        if (option == JOptionPane.YES_OPTION) {
            strategyUpdated(strategy, strategy.copy(lastError = null))
        }
    }

    private fun amendApi(strategy: ApiStrategy) {
        val newStrategy = ApiAmendDialog.amendStrategy(strategy) ?: return

        strategyUpdated(strategy, newStrategy)
    }

    private fun strategyUpdated(oldStrategy: ApiStrategy, newStrategy: ApiStrategy) {
        apiStrategies = apiStrategies.map { if (it == oldStrategy) newStrategy else it }

        buildApiTable()
    }

    private fun deleteApi(strategy: ApiStrategy) {
        val question = "Are you sure you want to delete the " + strategy.name + " strategy?"
        val option = DialogUtilNew.showQuestion(question, false, this)
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
        } else if (source == cbOpponentTwo || source == cbOpponentThree) {
            setOpponentEnablementAndStrategies(cbOpponentTwo.isSelected, cbOpponentThree.isSelected)
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
                enableItem.setEnabled(strategy.lastError != null)

                // Show the popup menu
                popupMenu.show(arg0.component, arg0.x, arg0.y)
            }
        } else if (arg0.clickCount == 2) {
            // Double-click
            if (strategy != null) {
                if (strategy.lastError != null) {
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
}
