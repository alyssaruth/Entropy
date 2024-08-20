package help

import io.kotest.matchers.shouldBe
import javax.swing.JTextPane
import main.kotlin.testCore.AbstractTest
import org.junit.jupiter.api.Test
import utils.getAllChildComponentsForType

abstract class AbstractHelpPanelTest<T : HelpPanel> : AbstractTest() {
    abstract fun factory(): T

    @Test
    fun `All text panes should be read-only`() {
        val pane = factory()
        val textPanes = pane.getAllChildComponentsForType<JTextPane>()
        textPanes.forEach { it.isEditable shouldBe false }
    }
}
