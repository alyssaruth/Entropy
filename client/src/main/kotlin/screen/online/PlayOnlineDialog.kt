package screen.online

import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import javax.swing.border.EmptyBorder
import screen.ScreenCache
import screen.SimpleDialog
import util.ClientGlobals
import util.DialogUtilNew

class PlayOnlineDialog : SimpleDialog() {
    private val lblName = JLabel("Name")
    private val textFieldUsername = JTextField()

    init {
        title = "Play Online"
        setSize(250, 125)
        setLocationRelativeTo(ScreenCache.getMainScreen())

        val panelCenter = JPanel()
        panelCenter.border = EmptyBorder(10, 5, 5, 5)
        contentPane.add(panelCenter, BorderLayout.CENTER)
        textFieldUsername.setSize(130, 22)
        textFieldUsername.preferredSize = Dimension(100, 20)

        panelCenter.add(lblName)
        panelCenter.add(textFieldUsername)
    }

    private fun valid(): Boolean {
        if (textFieldUsername.text.isNullOrEmpty()) {
            DialogUtilNew.showError("You must enter a name.")
            return false
        }

        return true
    }

    private fun beginSession() {
        dispose()

        ScreenCache.showConnectingDialog()

        try {
            ClientGlobals.sessionApi.beginSession(textFieldUsername.text)
        } finally {
            ScreenCache.dismissConnectingDialog()
        }
    }

    override fun okPressed() {
        if (valid()) {
            beginSession()
        }
    }
}

fun showPlayOnlineDialog() {
    val playOnlineDialog = PlayOnlineDialog()
    playOnlineDialog.isVisible = true
}
