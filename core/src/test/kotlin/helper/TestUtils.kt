package helper

import CURRENT_TIME
import io.kotest.matchers.maps.shouldContainExactly
import logging.LogRecord
import logging.LoggingCode
import logging.Severity
import java.awt.Component
import java.awt.Container
import java.time.Instant

fun LogRecord.shouldContainKeyValues(vararg values: Pair<String, Any?>) {
    keyValuePairs.shouldContainExactly(mapOf(*values))
}

fun makeLogRecord(
    timestamp: Instant = CURRENT_TIME,
    severity: Severity = Severity.INFO,
    loggingCode: LoggingCode = LoggingCode("log"),
    message: String = "A thing happened",
    errorObject: Throwable? = null,
    keyValuePairs: Map<String, Any?> = mapOf()
) =
    LogRecord(timestamp, severity, loggingCode, message, errorObject, keyValuePairs)

/**
 * Recurses through all child components, returning an ArrayList of all children of the appropriate type
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
