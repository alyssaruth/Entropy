package main.kotlin.testCore

import com.github.alyssaburlton.swingtest.findAll
import com.github.alyssaburlton.swingtest.findWindow
import com.github.alyssaburlton.swingtest.flushEdt
import io.kotest.matchers.maps.shouldContainExactly
import java.awt.Component
import java.awt.Container
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

/**
 * Recurses through all child components, returning an ArrayList of all children of the appropriate
 * type
 */
inline fun <reified T> Container.getAllChildComponentsForType(): List<T> {
    val ret = mutableListOf<T>()

    val components = components
    addComponents(ret, components, T::class.java)

    return ret
}

@Suppress("UNCHECKED_CAST")
fun <T> addComponents(ret: MutableList<T>, components: Array<Component>, desiredClazz: Class<T>) {
    for (comp in components) {
        if (desiredClazz.isInstance(comp)) {
            ret.add(comp as T)
        }

        if (comp is Container) {
            val subComponents = comp.components
            addComponents(ret, subComponents, desiredClazz)
        }
    }
}

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
