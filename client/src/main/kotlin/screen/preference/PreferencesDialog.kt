package screen.preference

import game.GameMode
import java.awt.BorderLayout
import javax.swing.BorderFactory
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTabbedPane
import javax.swing.SwingConstants
import screen.AbstractPreferencesPanel
import screen.PreferencesPanelAppearance
import screen.PreferencesPanelGameplay
import screen.PreferencesPanelMisc
import screen.PreferencesPanelPlayers
import screen.SimpleDialog
import util.ApiUtil
import util.Registry
import utils.getAllChildComponentsForType

class PreferencesDialog : SimpleDialog(), Registry {
    private val tabbedPane = JTabbedPane(SwingConstants.TOP)
    private val gameplayPanel = PreferencesPanelGameplay()
    private val appearanceScrollPane = JScrollPane()
    private val appearancePanel = PreferencesPanelAppearance()
    private val playersPanel = PreferencesPanelPlayers()
    private val miscPanel = PreferencesPanelMisc()
    private val okCancelPanel = JPanel()

    init {
        contentPane.setLayout(BorderLayout(0, 0))
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT)
        contentPane.add(tabbedPane)
        appearanceScrollPane.getVerticalScrollBar().setUnitIncrement(16)
        tabbedPane.addTab("Gameplay", null, gameplayPanel, null)
        tabbedPane.addTab("Players", null, playersPanel, null)
        appearanceScrollPane.setViewportView(appearancePanel)
        tabbedPane.addTab("Appearance", null, appearanceScrollPane, null)
        tabbedPane.addTab("Miscellaneous", null, miscPanel, null)
        contentPane.add(okCancelPanel, BorderLayout.SOUTH)
        okCancelPanel.setBorder(BorderFactory.createEmptyBorder(10, 150, 10, 150))
        okCancelPanel.setLayout(BorderLayout(0, 0))
        okCancelPanel.add(btnOk, BorderLayout.WEST)
        okCancelPanel.add(btnCancel, BorderLayout.EAST)

        childPanels().forEach { panel ->
            panel.setParent(this)
            panel.initVariables()
        }
    }

    private fun valid(): Boolean {
        childPanels().forEach { panel ->
            if (!panel.valid()) {
                tabbedPane.selectedComponent = panel
                return false
            }
        }

        return true
    }

    fun gameModeChanged(gameMode: GameMode?) {
        playersPanel.updateStrategySelection(gameMode)
    }

    private fun childPanels() = getAllChildComponentsForType<AbstractPreferencesPanel>()

    override fun okPressed() {
        childPanels().forEach { it.savePreferences() }
        closeDialog()
    }

    override fun cancelPressed() {
        closeDialog()
    }

    private fun closeDialog() {
        ApiUtil.clearCache()
        dispose()
    }
}
