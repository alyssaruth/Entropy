package util

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import logging.Severity
import main.kotlin.testCore.AbstractTest
import org.junit.jupiter.api.Test

class UrlUtilTest : AbstractTest() {
    @Test
    fun `Should execute the expected command`() {
        val runtime = mockk<Runtime>(relaxed = true)
        launchUrl("foo.bar", runtime)

        verify { runtime.exec("xdg-open foo.bar") }
    }

    @Test
    fun `Should log an appropriate error if launching the URL fails`() {
        val error = IOException("Oops")

        val runtime = mockk<Runtime>()
        every { runtime.exec(any<String>()) } throws error

        launchUrl("foo.bar", runtime)

        val log = verifyLog("urlError", Severity.ERROR)
        log.message shouldBe "Failed to launch foo.bar"
        log.errorObject shouldBe error
    }
}
