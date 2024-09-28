package logging

import ch.qos.logback.classic.spi.ILoggingEvent
import getPercentage
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.floor
import net.logstash.logback.marker.Markers
import org.slf4j.Marker

class Logger(
    private val slf4jLogger: org.slf4j.Logger,
    private val destinations: List<ILogDestination>
) {
    val loggingContext = ConcurrentHashMap<String, Any?>()

    fun addToContext(loggingKey: String, value: Any?) {
        loggingContext[loggingKey] = value ?: ""
        destinations.forEach { it.contextUpdated(loggingContext.toMap()) }
    }

    @JvmOverloads
    fun logProgress(code: String, workDone: Long, workToDo: Long, percentageToLogAt: Int = 10) {
        // Convert 1 to 0.01, 50 to 0.5, etc.
        val percentageAsDecimal = percentageToLogAt.toDouble() / 100
        val percentageOfTotal = floor(workToDo * percentageAsDecimal)
        val remainder = workDone % percentageOfTotal
        if (remainder == 0.0) {
            val percentStr = getPercentage(workDone, workToDo)
            val logStr = "Done $workDone/$workToDo ($percentStr%)"
            info(code, logStr)
        }
    }

    fun info(code: String, message: String, vararg keyValuePairs: Pair<String, Any?>) {
        log(Severity.INFO, code, message, null, mapOf(*keyValuePairs))
    }

    fun warn(code: String, message: String, vararg keyValuePairs: Pair<String, Any?>) {
        log(Severity.WARN, code, message, null, mapOf(*keyValuePairs))
    }

    fun error(code: String, message: String, vararg keyValuePairs: Pair<String, Any?>) {
        error(code, message, Throwable(message), keyValuePairs = keyValuePairs)
    }

    fun error(
        code: String,
        message: String,
        errorObject: Throwable = Throwable(message),
        vararg keyValuePairs: Pair<String, Any?>
    ) {
        log(
            Severity.ERROR,
            code,
            message,
            errorObject,
            mapOf(*keyValuePairs, KEY_EXCEPTION_MESSAGE to errorObject.message)
        )
    }

    fun log(
        severity: Severity,
        code: String,
        message: String,
        errorObject: Throwable?,
        keyValuePairs: Map<String, Any?>
    ) {
        val combinedKeys = loggingContext + keyValuePairs + ("loggingCode" to code)
        val marker = combinedKeys.filterValues { it != null }.let(Markers::appendEntries)
        getLogMethod(severity).invoke(marker, message, errorObject)
    }

    private fun getLogMethod(
        severity: Severity
    ): (marker: Marker, message: String, t: Throwable?) -> Unit =
        when (severity) {
            Severity.ERROR -> slf4jLogger::error
            Severity.WARN -> slf4jLogger::warn
            Severity.INFO -> slf4jLogger::info
        }
}

val ILoggingEvent.loggingCode: String?
    get() = findLogField("loggingCode") as? String

fun ILoggingEvent.findLogField(key: String): Any? = getLogFields()[key]

fun ILoggingEvent.getLogFields() = this.keyValuePairs.associate { it.key to it.value }
