package logging

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import main.kotlin.testCore.AbstractTest
import main.kotlin.testCore.CURRENT_TIME
import main.kotlin.testCore.FakeLogDestination
import main.kotlin.testCore.shouldContainKeyValues
import org.junit.jupiter.api.Test

class LoggerTest : AbstractTest() {
    @Test
    fun `Should log INFO`() {
        val destination = FakeLogDestination()
        val logger = Logger(listOf(destination))

        val loggingCode = "some.event"
        logger.info(loggingCode, "A thing happened")
        logger.waitUntilLoggingFinished()

        val record = destination.logRecords.first()
        record.severity shouldBe Severity.INFO
        record.loggingCode shouldBe loggingCode
        record.message shouldBe "A thing happened"
        record.errorObject shouldBe null
        record.timestamp shouldBe CURRENT_TIME
        record.keyValuePairs.size shouldBe 0
    }

    @Test
    fun `Should support extra key values when logging INFO`() {
        val destination = FakeLogDestination()
        val logger = Logger(listOf(destination))

        val loggingCode = "some.event"
        logger.info(loggingCode, "A thing happened", "Key" to "Value")
        logger.waitUntilLoggingFinished()

        val record = destination.logRecords.first()
        record.severity shouldBe Severity.INFO
        record.loggingCode shouldBe loggingCode
        record.message shouldBe "A thing happened"
        record.errorObject shouldBe null
        record.timestamp shouldBe CURRENT_TIME
        record.shouldContainKeyValues("Key" to "Value")
    }

    @Test
    fun `Should log WARN`() {
        val destination = FakeLogDestination()
        val logger = Logger(listOf(destination))

        val loggingCode = "some.event"
        logger.warn(loggingCode, "A slightly bad thing happened")
        logger.waitUntilLoggingFinished()

        val record = destination.logRecords.first()
        record.severity shouldBe Severity.WARN
        record.loggingCode shouldBe loggingCode
        record.message shouldBe "A slightly bad thing happened"
        record.errorObject shouldBe null
        record.timestamp shouldBe CURRENT_TIME
        record.keyValuePairs.size shouldBe 0
    }

    @Test
    fun `Should log ERROR`() {
        val destination = FakeLogDestination()
        val logger = Logger(listOf(destination))

        val loggingCode = "bad.thing"
        val throwable = Throwable("Boo")
        logger.error(loggingCode, "An exception happened!", throwable, "other.info" to 60)
        logger.waitUntilLoggingFinished()

        val record = destination.logRecords.first()
        record.severity shouldBe Severity.ERROR
        record.errorObject shouldBe throwable
        record.loggingCode shouldBe loggingCode
        record.timestamp shouldBe CURRENT_TIME
        record.shouldContainKeyValues("other.info" to 60, KEY_EXCEPTION_MESSAGE to "Boo")
    }

    @Test
    fun `Should log progress correctly`() {
        val destination = FakeLogDestination()
        val logger = Logger(listOf(destination))
        logger.logProgress("progress", 9, 100)
        logger.logProgress("progress", 11, 100)
        logger.waitUntilLoggingFinished()
        destination.logRecords.shouldBeEmpty()

        logger.logProgress("progress", 10, 100)
        logger.waitUntilLoggingFinished()
        val log = destination.logRecords.last()
        log.message shouldBe "Done 10/100 (10.0%)"
    }

    @Test
    fun `Should log to all destinations`() {
        val destinationOne = FakeLogDestination()
        val destinationTwo = FakeLogDestination()
        val logger = Logger(listOf(destinationOne, destinationTwo))
        logger.info("foo", "bar")
        logger.waitUntilLoggingFinished()

        destinationOne.logRecords.shouldHaveSize(1)
        destinationTwo.logRecords.shouldHaveSize(1)
    }

    @Test
    fun `Should not log on the current thread, but should be possible to await all logging having finished`() {
        val destination = SleepyLogDestination()
        val logger = Logger(listOf(destination))

        logger.info("foo", "bar")

        destination.logRecords.shouldBeEmpty()
        logger.waitUntilLoggingFinished()
        destination.logRecords.shouldHaveSize(1)
    }

    @Test
    fun `Should be possible to continue logging after awaiting logging to finish`() {
        val destination = SleepyLogDestination()
        val logger = Logger(listOf(destination))

        logger.info("foo", "bar")
        logger.waitUntilLoggingFinished()

        logger.info("foo", "baz")
        logger.waitUntilLoggingFinished()

        destination.logRecords.shouldHaveSize(2)
    }

    @Test
    fun `Should automatically include logging context fields`() {
        val destination = FakeLogDestination()
        val logger = Logger(listOf(destination))

        logger.addToContext("appVersion", "4.1.1")
        logger.info("foo", "a thing happened", "otherKey" to "otherValue")
        logger.waitUntilLoggingFinished()

        val record = destination.logRecords.last()
        record.shouldContainKeyValues("appVersion" to "4.1.1", "otherKey" to "otherValue")
    }

    @Test
    fun `Should notify destinations when context is updated`() {
        val destination = mockk<ILogDestination>(relaxed = true)
        val logger = Logger(listOf(destination))

        logger.addToContext("appVersion", "4.1.1")

        verify { destination.contextUpdated(mapOf("appVersion" to "4.1.1")) }
    }
}

class SleepyLogDestination : ILogDestination {
    val logRecords: MutableList<LogRecord> = mutableListOf()

    override fun log(record: LogRecord) {
        Thread.sleep(500)
        logRecords.add(record)
    }

    override fun contextUpdated(context: Map<String, Any?>) {}

    fun clear() {
        logRecords.clear()
    }
}
