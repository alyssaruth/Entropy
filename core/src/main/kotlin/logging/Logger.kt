package logging

import getPercentage
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import kotlin.math.floor
import org.slf4j.Marker
import org.slf4j.MarkerFactory
import utils.InjectedThings

private const val LOGGER_THREAD = "Logger"

class Logger(
    private val slf4jLogger: org.slf4j.Logger,
    private val destinations: List<ILogDestination>
) {
    val loggingContext = ConcurrentHashMap<String, Any?>()
    private val loggerFactory = ThreadFactory { r -> Thread(r, LOGGER_THREAD) }
    private var logService = Executors.newFixedThreadPool(1, loggerFactory)

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
        val timestamp = InjectedThings.clock.instant()
        val logRecord =
            LogRecord(
                timestamp,
                severity,
                code,
                message,
                errorObject,
                loggingContext + keyValuePairs
            )

        val combinedKeys = loggingContext + keyValuePairs
        val marker = MarkerFactory.getMarker(code)
        getLogMethod(severity).invoke(marker, message, errorObject)

        val runnable = Runnable { destinations.forEach { it.log(logRecord) } }
        if (
            Thread.currentThread().name != LOGGER_THREAD &&
                !logService.isShutdown &&
                !logService.isTerminated
        ) {
            logService.execute(runnable)
        } else {
            runnable.run()
        }
    }

    private fun getLogMethod(
        severity: Severity
    ): (marker: Marker, message: String, t: Throwable?) -> Unit =
        when (severity) {
            Severity.ERROR -> slf4jLogger::error
            Severity.WARN -> slf4jLogger::warn
            Severity.INFO -> slf4jLogger::info
        }

    fun waitUntilLoggingFinished() {
        try {
            logService.shutdown()
            logService.awaitTermination(30, TimeUnit.SECONDS)
        } catch (_: InterruptedException) {} finally {
            logService = Executors.newFixedThreadPool(1, loggerFactory)
        }
    }
}
