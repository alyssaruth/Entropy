package screen

import com.github.alyssaburlton.swingtest.clickCancel
import com.github.alyssaburlton.swingtest.clickOk
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import testCore.logger

class SimpleDialogTest : AbstractTest() {
    @Test
    fun `Pressing cancel should dispose the dialog by default`() {
        val dlg = SimpleDialogTestExtension()
        dlg.isVisible = true

        dlg.clickCancel()

        dlg.isVisible shouldBe false
    }

    @Test
    fun `Pressing ok should do whatever has been implemented`() {
        val dlg = SimpleDialogTestExtension()

        dlg.clickOk()

        verifyLog("ok.pressed")
    }

    inner class SimpleDialogTestExtension : SimpleDialog() {
        override fun okPressed() {
            logger.info("ok.pressed", "pressed ok")
        }
    }
}
