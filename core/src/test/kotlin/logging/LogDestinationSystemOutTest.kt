package logging

import io.kotest.matchers.string.shouldContain
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import main.kotlin.testCore.CURRENT_TIME_STRING
import main.kotlin.testCore.makeLogRecord
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class LogDestinationSystemOutTest : AbstractTest() {
    private val originalOut = System.out

    private val newOut = ByteArrayOutputStream()

    @BeforeEach
    fun beforeEach() {
        System.setOut(PrintStream(newOut))
    }

    @AfterEach
    fun afterEach() {
        System.setOut(originalOut)
    }

    @Test
    fun `Should log the record to system out`() {
        val dest = LogDestinationSystemOut()

        val record =
            makeLogRecord(severity = Severity.INFO, loggingCode = "some.event", message = "blah")
        dest.log(record)

        val output = newOut.toString()
        output shouldContain "$CURRENT_TIME_STRING   [some.event] blah"
    }

    @Test
    fun `Should print the stack trace for errors`() {
        val dest = LogDestinationSystemOut()

        val error = Throwable("oh no")
        val record =
            makeLogRecord(
                severity = Severity.ERROR,
                loggingCode = "some.event",
                message = "blah",
                errorObject = error
            )
        dest.log(record)

        val output = newOut.toString()
        output shouldContain "$CURRENT_TIME_STRING   [some.event] blah"
        output shouldContain "$CURRENT_TIME_STRING   java.lang.Throwable: oh no"
    }

    @Test
    fun `Should print the stack for a thread dump`() {
        val dest = LogDestinationSystemOut()

        val record =
            makeLogRecord(
                severity = Severity.INFO,
                loggingCode = "some.event",
                message = "blah",
                keyValuePairs = mapOf(KEY_STACK to "at Something.blah")
            )
        dest.log(record)

        val output = newOut.toString()
        output shouldContain "$CURRENT_TIME_STRING   [some.event] blah"
        output shouldContain "at Something.blah"
    }
}
