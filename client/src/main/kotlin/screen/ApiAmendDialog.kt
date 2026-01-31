package screen

import game.GameMode
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import javax.swing.JButton
import javax.swing.JCheckBox
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField
import strategy.ApiStrategy
import util.ApiUtil
import util.DialogUtilNew

class ApiAmendDialog : SimpleDialog(), ActionListener {
    private var apiStrategy: ApiStrategy? = null

    private val okCancelPanel = JPanel()
    private val panel = JPanel()
    private val lblName = JLabel("Name")
    private val textFieldName = JTextField()
    private val lblGameMode = JLabel("Game Mode")
    private val rdbtnEntropy = JCheckBox("Entropy")
    private val rdbtnVectropy = JCheckBox("Vectropy")
    private val lblPort = JLabel("Port")
    private val textFieldPort = JTextField()
    private val lblMessageType = JLabel("Messaging")
    private val btnTest = JButton("Test")

    private val modeMappings =
        listOf(rdbtnEntropy to GameMode.Entropy, rdbtnVectropy to GameMode.Vectropy)

    init {
        title = "API Setup"
        setSize(350, 300)
        setLocationRelativeTo(null)
        isResizable = false
        isModal = true

        contentPane.add(okCancelPanel, BorderLayout.SOUTH)
        okCancelPanel.add(btnOk)
        okCancelPanel.add(btnCancel)
        contentPane.add(panel, BorderLayout.CENTER)
        panel.layout = null
        lblName.setBounds(10, 10, 100, 25)
        panel.add(lblName)
        lblGameMode.setBounds(10, 45, 100, 25)
        panel.add(lblGameMode)
        rdbtnEntropy.setBounds(110, 45, 80, 25)
        panel.add(rdbtnEntropy)
        rdbtnVectropy.setBounds(190, 45, 80, 25)
        panel.add(rdbtnVectropy)
        textFieldName.setBounds(110, 10, 120, 25)
        panel.add(textFieldName)
        textFieldName.columns = 10
        lblPort.setBounds(10, 80, 100, 25)
        panel.add(lblPort)
        textFieldPort.setBounds(110, 80, 80, 25)
        panel.add(textFieldPort)
        textFieldPort.columns = 10
        lblMessageType.setBounds(10, 115, 100, 25)
        panel.add(lblMessageType)
        btnTest.setBounds(120, 172, 89, 23)
        panel.add(btnTest)

        btnTest.addActionListener(this)
    }

    fun init(strategy: ApiStrategy?) {
        this.apiStrategy = strategy

        if (strategy == null) {
            textFieldName.text = ""
            textFieldPort.text = "" + DEFAULT_PORT_NUMBER
            rdbtnEntropy.isSelected = true
        } else {
            textFieldName.text = strategy.name
            textFieldPort.text = "" + strategy.port

            rdbtnEntropy.isSelected = strategy.supportedModes.contains(GameMode.Entropy)
            rdbtnVectropy.isSelected = strategy.supportedModes.contains(GameMode.Vectropy)
        }
    }

    override fun okPressed() {
        if (valid()) {
            saveData()
        }
    }

    override fun actionPerformed(arg0: ActionEvent) {
        if (arg0.source === btnTest) {
            sendTestMessage()
        } else {
            super.actionPerformed(arg0)
        }
    }

    private fun valid(): Boolean {
        val name = textFieldName.text
        if (name.isEmpty()) {
            DialogUtilNew.showError("You must enter a name for this setup.")
            return false
        }

        if (!rdbtnEntropy.isSelected && !rdbtnVectropy.isSelected) {
            DialogUtilNew.showError("You must select at least one game mode.", this)
            return false
        }

        return true
    }

    private fun saveData() {
        val supportedModes = modeMappings.filter { it.first.isSelected }.map { it.second }
        apiStrategy = ApiStrategy(textFieldName.text, supportedModes, textFieldPort.text.toInt())

        dispose()
    }

    private fun sendTestMessage() {
        val infoMsg =
            """
            About to send a test message on port ${textFieldPort.text}
            
            Ensure that the third-party software is running and listening on this port.
            """
                .trimIndent()

        DialogUtilNew.showInfo(infoMsg)

        val portStr = textFieldPort.text
        val port = portStr.toInt()

        ApiUtil.sendTestMessage(port, true)
    }

    companion object {
        private const val DEFAULT_PORT_NUMBER = 1153

        fun createStrategy(): ApiStrategy? {
            val dialog = ApiAmendDialog()
            dialog.init(null)
            dialog.isVisible = true

            return dialog.apiStrategy
        }

        fun amendStrategy(strategy: ApiStrategy): ApiStrategy? {
            val dialog = ApiAmendDialog()
            dialog.init(strategy)
            dialog.isVisible = true

            return dialog.apiStrategy
        }
    }
}
