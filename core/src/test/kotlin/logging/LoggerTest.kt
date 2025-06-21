package logging

import ch.qos.logback.classic.Level
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.slf4j.MDC
import testCore.AbstractTest
import testCore.shouldContainKeyValues
import utils.CoreGlobals.slf4jLogger

class LoggerTest : AbstractTest() {
    @Test
    fun `Should log INFO`() {
        val logger = Logger(slf4jLogger)

        val loggingCode = "some.event"
        logger.info(loggingCode, "A thing happened")

        val record = getLogRecords().first()
        record.level shouldBe Level.INFO
        record.loggingCode shouldBe loggingCode
        record.message shouldBe "A thing happened"
        record.errorObject() shouldBe null
        record.shouldContainKeyValues(KEY_LOGGING_CODE to "some.event")
    }

    @Test
    fun `Should support extra key values when logging INFO`() {
        val logger = Logger(slf4jLogger)

        val loggingCode = "some.event"
        logger.info(loggingCode, "A thing happened", "Key" to "Value")

        val record = getLogRecords().first()
        record.level shouldBe Level.INFO
        record.loggingCode shouldBe loggingCode
        record.message shouldBe "A thing happened"
        record.errorObject() shouldBe null
        record.shouldContainKeyValues("Key" to "Value")
    }

    @Test
    fun `Should log WARN`() {
        val logger = Logger(slf4jLogger)

        val loggingCode = "some.event"
        logger.warn(loggingCode, "A slightly bad thing happened")

        val record = getLastLog()
        record.level shouldBe Level.WARN
        record.loggingCode shouldBe loggingCode
        record.message shouldBe "A slightly bad thing happened"
        record.errorObject() shouldBe null
    }

    @Test
    fun `Should log ERROR`() {
        val logger = Logger(slf4jLogger)

        val loggingCode = "bad.thing"
        val throwable = Throwable("Boo")
        logger.error(loggingCode, "An exception happened!", throwable, "other.info" to 60)

        val record = getLastLog()
        record.level shouldBe Level.ERROR
        record.errorObject() shouldBe throwable
        record.loggingCode shouldBe loggingCode
        record.shouldContainKeyValues("other.info" to 60, KEY_EXCEPTION_MESSAGE to "Boo")

        errorLogged() shouldBe true
    }

    @Test
    fun `Should log progress correctly`() {
        val logger = Logger(slf4jLogger)
        logger.logProgress("progress", 9, 100)
        logger.logProgress("progress", 11, 100)
        getLogRecords().shouldBeEmpty()

        logger.logProgress("progress", 10, 100)
        val log = getLastLog()
        log.message shouldBe "Done 10/100 (10.0%)"
    }

    @Test
    fun `Should automatically include logging context fields`() {
        val logger = Logger(slf4jLogger)

        logger.addToContext("appVersion", "4.1.1")
        MDC.put("threadContext", "test thread")
        logger.info("foo", "a thing happened", "otherKey" to "otherValue")

        val record = getLastLog()
        record.shouldContainKeyValues(
            "appVersion" to "4.1.1",
            "threadContext" to "test thread",
            "otherKey" to "otherValue",
        )
    }

    @Test
    fun `Should update listeners when global log context changes`() {
        val logger = Logger(slf4jLogger)
        val mockListener = mockk<ILogContextListener>(relaxed = true)

        logger.addContextListener(mockListener)
        logger.addToContext("client.ip", "1.2.3.4")

        verify { mockListener.contextUpdated(mapOf("client.ip" to "1.2.3.4")) }
    }
}
