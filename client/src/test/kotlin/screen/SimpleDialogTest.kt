package screen

import com.github.alyssaburlton.swingtest.clickCancel
import com.github.alyssaburlton.swingtest.clickOk
import com.github.alyssaburlton.swingtest.findChild
import io.kotest.matchers.shouldBe
import javax.swing.AbstractButton
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

    @Test
    fun `Can exclude cancel button`() {
        val dlg = SimpleDialogTestExtension(false)
        dlg.findChild<AbstractButton>(text = "Cancel") shouldBe null
    }

    inner class SimpleDialogTestExtension(includeCancel: Boolean = true) :
        SimpleDialog(includeCancel) {
        init {
            isModal = false
        }

        override fun okPressed() {
            logger.info("ok.pressed", "pressed ok")
        }
    }
}
