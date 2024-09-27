package screen.online

import com.github.alyssaburlton.swingtest.clickOk
import com.github.alyssaburlton.swingtest.getChild
import com.github.alyssaburlton.swingtest.shouldBeVisible
import com.github.alyssaburlton.swingtest.shouldNotBeVisible
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import javax.swing.JTextField
import main.kotlin.testCore.getDialogMessage
import main.kotlin.testCore.getErrorDialog
import org.junit.jupiter.api.Test
import screen.ScreenCache
import testCore.AbstractTest
import util.Globals

class PlayOnlineDialogTest : AbstractTest() {
    @Test
    fun `Should not allow an empty name`() {
        val dlg = PlayOnlineDialog()
        dlg.isVisible = true

        dlg.clickOk(async = true)

        getErrorDialog().getDialogMessage() shouldBe "You must enter a name."
        dlg.shouldBeVisible()
    }

    @Test
    fun `Should invoke the session API`() {
        Globals.sessionApi = mockk(relaxed = true)

        val dlg = PlayOnlineDialog()
        dlg.isVisible = true
        dlg.getChild<JTextField>().text = "Alyssa"
        dlg.clickOk()

        dlg.shouldNotBeVisible()
        verify { Globals.sessionApi.beginSession("Alyssa") }
        ScreenCache.getConnectingDialog().shouldNotBeVisible()
    }
}
