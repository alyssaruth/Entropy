package testCore

import com.github.alyssaburlton.swingtest.purgeWindows
import io.kotest.assertions.fail
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import logging.LogDestinationSystemOut
import logging.LogRecord
import logging.Logger
import logging.Severity
import main.kotlin.testCore.BeforeAllTestsExtension
import main.kotlin.testCore.FakeLogDestination
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import util.AbstractClient

private val logDestination = FakeLogDestination()
val logger = Logger(listOf(logDestination, LogDestinationSystemOut()))
private var checkedForExceptions = false

@ExtendWith(BeforeAllTestsExtension::class)
open class AbstractTest {
    @BeforeEach
    fun beforeEachTest() {
        clearLogs()
        clearAllMocks()

        AbstractClient.devMode = false
        logger.loggingContext.clear()
    }

    @AfterEach
    fun afterEachTest() {
        if (!checkedForExceptions) {
            val errors = getErrorsLogged()
            if (errors.isNotEmpty()) {
                fail(
                    "Unexpected error(s) were logged during test: ${errors.map { it.getThrowableStr() } }"
                )
            }
            errorLogged() shouldBe false
        }

        checkedForExceptions = false
        purgeWindows()
    }

    fun getLastLog() = flushAndGetLogRecords().last()

    fun verifyLog(code: String, severity: Severity = Severity.INFO): LogRecord {
        val record =
            flushAndGetLogRecords().findLast { it.loggingCode == code && it.severity == severity }
        record.shouldNotBeNull()

        if (severity == Severity.ERROR) {
            checkedForExceptions = true
        }

        return record
    }

    protected fun findLog(code: String, severity: Severity = Severity.INFO) =
        getLogRecordsSoFar().findLast { it.loggingCode == code && it.severity == severity }

    fun verifyNoLogs(code: String) {
        flushAndGetLogRecords().any { it.loggingCode == code } shouldBe false
    }

    fun errorLogged(): Boolean {
        checkedForExceptions = true
        return getErrorsLogged().isNotEmpty()
    }

    private fun getErrorsLogged() = flushAndGetLogRecords().filter { it.severity == Severity.ERROR }

    fun getLogRecordsSoFar() = logDestination.logRecords.toList()

    fun flushAndGetLogRecords(): List<LogRecord> {
        logger.waitUntilLoggingFinished()
        return logDestination.logRecords.toList()
    }

    fun clearLogs() {
        logger.waitUntilLoggingFinished()
        logDestination.clear()
    }
}
