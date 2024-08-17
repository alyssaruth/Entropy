package util

import java.awt.Component
import java.io.File
import javax.swing.JFileChooser
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import screen.LoadingDialog
import screen.ScreenCache
import utils.InjectedThings.logger

object DialogUtilNew {
    private var loadingDialog: LoadingDialog? = null

    fun showInfo(infoText: String, parent: Component = ScreenCache.getMainScreen()) {
        logDialogShown("Info", "Information", infoText)
        JOptionPane.showMessageDialog(
            parent,
            infoText,
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        )
        logDialogClosed("Info", null)
    }

    fun showCustomMessage(message: Any, parent: Component = ScreenCache.getMainScreen()) {
        logDialogShown("CustomInfo", "Information", "?")
        JOptionPane.showMessageDialog(
            parent,
            message,
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        )
        logDialogClosed("CustomInfo", null)
    }

    fun showError(errorText: String, parent: Component? = ScreenCache.getMainScreen()) {
        dismissLoadingDialog()

        logDialogShown("Error", "Error", errorText)
        JOptionPane.showMessageDialog(parent, errorText, "Error", JOptionPane.ERROR_MESSAGE)
        logDialogClosed("Error", null)
    }

    fun showErrorLater(errorText: String) {
        SwingUtilities.invokeLater { showError(errorText) }
    }

    @JvmOverloads
    fun showQuestion(
        message: String,
        allowCancel: Boolean = false,
        parent: Component = ScreenCache.getMainScreen()
    ): Int {
        logDialogShown("Question", "Question", message)
        val option =
            if (allowCancel) JOptionPane.YES_NO_CANCEL_OPTION else JOptionPane.YES_NO_OPTION
        val selection =
            JOptionPane.showConfirmDialog(
                parent,
                message,
                "Question",
                option,
                JOptionPane.QUESTION_MESSAGE
            )
        logDialogClosed("Question", selection)
        return selection
    }

    fun showLoadingDialog(text: String) {
        logDialogShown("Loading", "", text)
        loadingDialog = LoadingDialog()
        loadingDialog?.showDialog(text)
    }

    fun dismissLoadingDialog() {
        val wasVisible = loadingDialog?.isVisible ?: false
        loadingDialog?.dismissDialog()
        if (wasVisible) {
            logDialogClosed("Loading", null)
        }
    }

    fun showOption(title: String, message: String, options: List<String>): String? {
        logDialogShown("Option", title, message)
        val typedArray = options.toTypedArray()
        val selection =
            JOptionPane.showOptionDialog(
                null,
                message,
                title,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                typedArray,
                options.first()
            )
        val selectionStr = if (selection > -1) typedArray[selection] else null

        logDialogClosed("Option", selectionStr)
        return selectionStr
    }

    fun <K> showInput(
        title: String,
        message: String,
        options: Array<K>? = null,
        defaultOption: K? = null
    ): K? {
        logDialogShown("Input", title, message)
        val selection =
            JOptionPane.showInputDialog(
                null,
                message,
                title,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                defaultOption
            ) as K?

        logDialogClosed("Input", selection)
        return selection
    }

    fun chooseDirectory(parent: Component?): File? {
        logDialogShown("File selector", "", "")
        val fc = JFileChooser()
        fc.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        val option = fc.showDialog(parent, "Select")
        if (option != JFileChooser.APPROVE_OPTION) {
            return null
        }

        val file = fc.selectedFile
        logDialogClosed("File selector", file?.absolutePath)
        return file
    }

    private fun logDialogShown(type: String, title: String, message: String) {
        logger.info(
            "dialogShown",
            "$type dialog shown: $message",
            "dialogType" to type,
            "dialogTitle" to title,
            "dialogMessage" to message
        )
    }

    private fun logDialogClosed(type: String, selection: Any?) {
        var message = "$type dialog closed"
        selection?.let { message += " - selected ${translateOption(it)}" }

        logger.info("dialogClosed", message, "dialogType" to type, "dialogSelection" to selection)
    }

    private fun translateOption(option: Any?) =
        when (option) {
            JOptionPane.YES_OPTION -> "Yes"
            JOptionPane.NO_OPTION -> "No"
            JOptionPane.CANCEL_OPTION -> "Cancel"
            else -> option
        }
}
