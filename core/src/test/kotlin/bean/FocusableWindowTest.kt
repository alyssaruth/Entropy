package bean

import io.kotest.matchers.shouldBe
import io.mockk.mockk
import logging.KEY_ACTIVE_WINDOW
import logging.findLogField
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import utils.CoreGlobals.logger

class FocusableWindowTest : AbstractTest() {
    @Test
    fun `Should update logging context when gains focus`() {
        val window = FakeFocusableWindow()
        window.windowGainedFocus(mockk())

        logger.info("some.code", "some message")

        getLastLog().findLogField(KEY_ACTIVE_WINDOW) shouldBe "Fake Window"
    }

    private inner class FakeFocusableWindow : FocusableWindow() {
        override val windowName = "Fake Window"
    }
}
