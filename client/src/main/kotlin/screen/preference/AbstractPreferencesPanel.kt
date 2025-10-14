package screen.preference

import achievement.Reward
import java.awt.event.ActionListener
import javax.swing.AbstractButton
import javax.swing.JPanel

abstract class AbstractPreferencesPanel(protected val parentDialog: PreferencesDialog) :
    JPanel(), ActionListener {
    abstract fun initVariables()

    abstract fun valid(): Boolean

    abstract fun savePreferences()

    protected fun toggleLockedComponent(c: AbstractButton, requiredReward: Reward) {
        val unlocked = requiredReward.isUnlocked()
        val text = if (unlocked) c.text else "Locked"
        val toolTipText =
            if (unlocked) c.toolTipText
            else "Unlock at " + requiredReward.threshold + " achievements"

        c.setText(text)
        c.setToolTipText(toolTipText)
        c.setEnabled(unlocked)
    }
}
