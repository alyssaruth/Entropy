package main.kotlin.testCore

import com.github.alyssaburlton.swingtest.findAll
import com.github.alyssaburlton.swingtest.findWindow
import com.github.alyssaburlton.swingtest.flushEdt
import io.kotest.matchers.maps.shouldContainExactly
import java.time.Instant
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.SwingUtilities
import logging.LogRecord
import logging.Severity

fun LogRecord.shouldContainKeyValues(vararg values: Pair<String, Any?>) {
    keyValuePairs.shouldContainExactly(mapOf(*values))
}

fun makeLogRecord(
    timestamp: Instant = CURRENT_TIME,
    severity: Severity = Severity.INFO,
    loggingCode: String = "log",
    message: String = "A thing happened",
    errorObject: Throwable? = null,
    keyValuePairs: Map<String, Any?> = mapOf()
) = LogRecord(timestamp, severity, loggingCode, message, errorObject, keyValuePairs)

fun getInfoDialog() = getOptionPaneDialog("Information")

fun getQuestionDialog() = getOptionPaneDialog("Question")

fun getErrorDialog() = getOptionPaneDialog("Error")

private fun getOptionPaneDialog(title: String) = findWindow<JDialog> { it.title == title }!!

fun JDialog.getDialogMessage(): String {
    val messageLabels = findAll<JLabel>().filter { it.name == "OptionPane.label" }
    return messageLabels.joinToString("\n\n") { it.text }
}

fun <T> runAsync(block: () -> T?): T? {
    var result: T? = null
    SwingUtilities.invokeLater { result = block() }

    flushEdt()
    return result
}
