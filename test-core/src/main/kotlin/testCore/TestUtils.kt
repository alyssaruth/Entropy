package main.kotlin.testCore

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import com.github.alyssaburlton.swingtest.findAll
import com.github.alyssaburlton.swingtest.findWindow
import com.github.alyssaburlton.swingtest.flushEdt
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import io.mockk.verify
import java.time.Instant
import javax.swing.JDialog
import javax.swing.JLabel
import javax.swing.SwingUtilities
import logging.LogRecord
import logging.getLogFields

fun ILoggingEvent.shouldContainKeyValues(vararg values: Pair<String, Any?>) {
    this.getLogFields().shouldContainAll(mapOf(*values))
}

fun makeLogRecord(
    timestamp: Instant = CURRENT_TIME,
    severity: Level = Level.INFO,
    loggingCode: String = "log",
    message: String = "A thing happened",
    errorObject: Throwable? = null,
    keyValuePairs: Map<String, Any?> = mapOf(),
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

fun <T> List<T>.only(): T {
    size shouldBe 1
    return first()
}

fun verifyNotCalled(verifyBlock: io.mockk.MockKVerificationScope.() -> Unit) {
    verify(exactly = 0) { verifyBlock() }
}
