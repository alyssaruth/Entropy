package screen

import java.awt.BorderLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JDialog
import javax.swing.JPanel
import utils.CoreGlobals.logger

abstract class SimpleDialog : JDialog(), ActionListener {
    protected val panelOkCancel = JPanel()
    protected val btnOk = JButton("Ok")
    protected val btnCancel = JButton("Cancel")

    init {
        contentPane.add(panelOkCancel, BorderLayout.SOUTH)
        isModal = true
        isResizable = false

        panelOkCancel.add(btnOk)
        panelOkCancel.add(btnCancel)

        btnOk.addActionListener(this)
        btnCancel.addActionListener(this)
    }

    /** Abstract methods */
    abstract fun okPressed()

    /** Default methods */
    open fun cancelPressed() {
        dispose()
    }

    override fun actionPerformed(arg0: ActionEvent) {
        when (arg0.source) {
            btnOk -> okPressed()
            btnCancel -> cancelPressed()
            else -> logger.error("swing.error", "Unexpected button pressed: ${arg0.source}")
        }
    }
}
