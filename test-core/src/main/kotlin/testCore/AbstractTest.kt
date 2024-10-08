package testCore

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.github.alyssaburlton.swingtest.purgeWindows
import io.kotest.assertions.fail
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import logging.Logger
import logging.errorObject
import logging.extractStackTrace
import logging.loggingCode
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.MDC
import util.Debug
import util.DebugOutputSystemOut
import utils.CoreGlobals.slf4jLogger

val logger = Logger(slf4jLogger)
private var checkedForExceptions = false

val listAppender = ListAppender<ILoggingEvent>()

@ExtendWith(BeforeAllTestsExtension::class)
open class AbstractTest {
    @BeforeEach
    fun beforeEachTest() {
        clearLogs()
        clearAllMocks()

        Debug.initialise(DebugOutputSystemOut())
        logger.clearContext()
        MDC.clear()
    }

    @AfterEach
    fun afterEachTest() {
        if (!checkedForExceptions) {
            val errors = getErrorsLogged()

            if (errors.isNotEmpty()) {
                fail(
                    "Unexpected error(s) were logged during test: ${errors.map { it.errorObject()?.let(::extractStackTrace) } }"
                )
            }
            errorLogged() shouldBe false
        }

        checkedForExceptions = false
        purgeWindows()
    }

    fun getLastLog() = getLogRecords().last()

    fun verifyLog(code: String, level: Level = Level.INFO): ILoggingEvent {
        val record = getLogRecords().findLast { it.loggingCode == code && it.level == level }
        record.shouldNotBeNull()

        if (level == Level.ERROR) {
            checkedForExceptions = true
        }

        return record
    }

    protected fun findLog(code: String, level: Level = Level.INFO) =
        getLogRecords().findLast { it.loggingCode == code && it.level == level }

    fun verifyNoLogs(code: String) {
        getLogRecords().any { it.loggingCode == code } shouldBe false
    }

    fun errorLogged(): Boolean {
        checkedForExceptions = true
        return getErrorsLogged().isNotEmpty()
    }

    private fun getErrorsLogged() = getLogRecords().filter { it.level == Level.ERROR }

    fun getLogRecords(): List<ILoggingEvent> = listAppender.list

    fun clearLogs() {
        listAppender.list.clear()
    }
}
