package logging

import ch.qos.logback.classic.Level
import io.kotest.matchers.maps.shouldNotContainKey
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import testCore.shouldContainKeyValues

class LoggerUncaughtExceptionHandlerTest : AbstractTest() {
    @Test
    fun `Should log a single WARN line for suppressed errors`() {
        val handler = LoggerUncaughtExceptionHandler()

        val message = "javax.swing.plaf.FontUIResource cannot be cast to class javax.swing.Painter"
        val ex = Exception(message)
        handler.uncaughtException(Thread.currentThread(), ex)

        val log = verifyLog("uncaughtException", Level.WARN)
        log.errorObject() shouldBe null
        log.message shouldBe "Suppressing uncaught exception: $ex"
        log.shouldContainKeyValues(
            KEY_THREAD to Thread.currentThread().toString(),
            KEY_EXCEPTION_MESSAGE to message,
        )
    }

    @Test
    fun `Should not suppress errors without a message`() {
        val handler = LoggerUncaughtExceptionHandler()

        val ex = Exception()
        handler.uncaughtException(Thread.currentThread(), ex)

        val log = verifyLog("uncaughtException", Level.ERROR)
        log.errorObject() shouldBe ex
        log.shouldContainKeyValues(KEY_THREAD to Thread.currentThread().toString())
        log.getLogFields().shouldNotContainKey(KEY_EXCEPTION_MESSAGE)
        log.message shouldContain "Uncaught exception: $ex"
    }

    @Test
    fun `Should not suppress errors with an unrecognised message`() {
        val t = Thread("Foo")
        val handler = LoggerUncaughtExceptionHandler()

        val ex = Exception("Argh")
        handler.uncaughtException(t, ex)

        val log = verifyLog("uncaughtException", Level.ERROR)
        log.errorObject() shouldBe ex
        log.shouldContainKeyValues(KEY_THREAD to t.toString(), KEY_EXCEPTION_MESSAGE to "Argh")
        log.message shouldContain "Uncaught exception: $ex"
    }
}
