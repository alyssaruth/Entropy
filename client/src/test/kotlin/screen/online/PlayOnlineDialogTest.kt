package screen.online

import com.github.alyssaburlton.swingtest.clickOk
import com.github.alyssaburlton.swingtest.flushEdt
import com.github.alyssaburlton.swingtest.getChild
import com.github.alyssaburlton.swingtest.shouldBeVisible
import com.github.alyssaburlton.swingtest.shouldNotBeVisible
import io.kotest.matchers.shouldBe
import io.mockk.mockk
import io.mockk.verify
import javax.swing.JTextField
import org.junit.jupiter.api.Test
import screen.ScreenCache
import testCore.AbstractTest
import testCore.getDialogMessage
import testCore.getErrorDialog
import util.ClientGlobals

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
        ClientGlobals.sessionApi = mockk(relaxed = true)

        val dlg = PlayOnlineDialog()
        dlg.isVisible = true
        dlg.getChild<JTextField>().text = "Alyssa"
        dlg.clickOk()
        flushEdt()

        dlg.shouldNotBeVisible()
        verify { ClientGlobals.sessionApi.beginSession("Alyssa") }
        ScreenCache.getConnectingDialog().shouldNotBeVisible()
    }
}
