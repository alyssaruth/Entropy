package testCore

import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.github.alyssaburlton.swingtest.purgeWindows
import io.kotest.assertions.fail
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import logging.LogDestinationSystemOut
import logging.LogRecord
import logging.Logger
import logging.Severity
import logging.loggingCode
import main.kotlin.testCore.FakeLogDestination
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import util.AbstractClient
import util.Debug
import util.DebugOutputSystemOut
import utils.InjectedThings.slf4jLogger

private val logDestination = FakeLogDestination()
val logger = Logger(slf4jLogger, listOf(logDestination, LogDestinationSystemOut()))
private var checkedForExceptions = false

val listAppender = ListAppender<ILoggingEvent>()

@ExtendWith(BeforeAllTestsExtension::class)
open class AbstractTest {
    @BeforeEach
    fun beforeEachTest() {
        clearLogs()
        clearAllMocks()

        Debug.initialise(DebugOutputSystemOut())
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

    fun getLastLog() = getLogRecords().last()

    fun verifyLog(code: String, severity: Severity = Severity.INFO): LogRecord {
        val record = getLogRecords().findLast { it.loggingCode == code && it.severity == severity }
        record.shouldNotBeNull()

        if (severity == Severity.ERROR) {
            checkedForExceptions = true
        }

        return record
    }

    protected fun findLog(code: String, severity: Severity = Severity.INFO) =
        getLogRecords().findLast { it.loggingCode == code && it.severity == severity }

    fun verifyNoLogs(code: String) {
        getLogRecords().any { it.loggingCode == code } shouldBe false
    }

    fun errorLogged(): Boolean {
        checkedForExceptions = true
        return getErrorsLogged().isNotEmpty()
    }

    private fun getErrorsLogged() = getLogRecords().filter { it.severity == Severity.ERROR }

    fun getLogRecords() = listAppender.list

    fun clearLogs() {
        listAppender.list.clear()
    }
}
