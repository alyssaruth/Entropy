package util

import ch.qos.logback.classic.Level
import com.github.alyssaburlton.swingtest.clickCancel
import com.github.alyssaburlton.swingtest.clickNo
import com.github.alyssaburlton.swingtest.clickOk
import com.github.alyssaburlton.swingtest.clickYes
import com.github.alyssaburlton.swingtest.findWindow
import com.github.alyssaburlton.swingtest.flushEdt
import com.github.alyssaburlton.swingtest.purgeWindows
import io.kotest.matchers.shouldBe
import javax.swing.JDialog
import javax.swing.SwingUtilities
import main.kotlin.testCore.getErrorDialog
import main.kotlin.testCore.getInfoDialog
import main.kotlin.testCore.getQuestionDialog
import main.kotlin.testCore.runAsync
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class DialogUtilNewTest : AbstractTest() {
    @Test
    fun `Should log for INFO dialogs`() {
        runAsync { DialogUtilNew.showInfo("Something useful") }

        verifyLog("dialogShown", Level.INFO).message shouldBe "Info dialog shown: Something useful"

        getInfoDialog().clickOk()
        flushEdt()
        verifyLog("dialogClosed", Level.INFO).message shouldBe "Info dialog closed"
    }

    @Test
    fun `Should log for ERROR dialogs`() {
        runAsync { DialogUtilNew.showError("Something bad") }

        verifyLog("dialogShown", Level.INFO).message shouldBe "Error dialog shown: Something bad"
        getErrorDialog().clickOk()
        flushEdt()
        verifyLog("dialogClosed", Level.INFO).message shouldBe "Error dialog closed"
    }

    @Test
    fun `Should show an ERROR dialog later`() {
        SwingUtilities.invokeLater { Thread.sleep(500) }
        DialogUtilNew.showErrorLater("Some error")

        findWindow<JDialog>() shouldBe null

        flushEdt()
        getErrorDialog().clickOk()
        flushEdt()
        verifyLog("dialogClosed", Level.INFO).message shouldBe "Error dialog closed"
    }

    @Test
    fun `Should log for QUESTION dialogs, with the correct selection`() {
        runAsync { DialogUtilNew.showQuestion("Do you like cheese?") }
        verifyLog("dialogShown", Level.INFO).message shouldBe
            "Question dialog shown: Do you like cheese?"
        getQuestionDialog().clickYes()
        flushEdt()
        verifyLog("dialogClosed", Level.INFO).message shouldBe
            "Question dialog closed - selected Yes"

        clearLogs()
        purgeWindows()

        runAsync { DialogUtilNew.showQuestion("Do you like mushrooms?") }
        verifyLog("dialogShown", Level.INFO).message shouldBe
            "Question dialog shown: Do you like mushrooms?"
        getQuestionDialog().clickNo()
        flushEdt()
        verifyLog("dialogClosed", Level.INFO).message shouldBe
            "Question dialog closed - selected No"

        clearLogs()
        purgeWindows()

        runAsync { DialogUtilNew.showQuestion("Do you want to delete all data?", true) }
        verifyLog("dialogShown", Level.INFO).message shouldBe
            "Question dialog shown: Do you want to delete all data?"
        getQuestionDialog().clickCancel()
        flushEdt()
        verifyLog("dialogClosed", Level.INFO).message shouldBe
            "Question dialog closed - selected Cancel"
    }

    @Test
    fun `Should log when showing and dismissing loading dialog`() {
        DialogUtilNew.showLoadingDialog("One moment...")
        flushEdt()
        verifyLog("dialogShown", Level.INFO).message shouldBe "Loading dialog shown: One moment..."

        DialogUtilNew.dismissLoadingDialog()
        verifyLog("dialogClosed", Level.INFO).message shouldBe "Loading dialog closed"
    }

    @Test
    fun `Should not log if loading dialog wasn't visible`() {
        DialogUtilNew.dismissLoadingDialog()
        verifyNoLogs("dialogClosed")
    }
}
