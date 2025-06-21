package screen

import ch.qos.logback.classic.Level
import com.github.alyssaburlton.swingtest.flushEdt
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import java.awt.Color
import javax.swing.JLabel
import javax.swing.text.StyleConstants
import logging.KEY_STACK
import org.junit.jupiter.api.Test
import testCore.AbstractTest
import testCore.makeLoggingEvent
import utils.getAllChildComponentsForType

class LoggingConsoleTest : AbstractTest() {
    @Test
    fun `Should separate log records with a new line`() {
        val recordOne = makeLoggingEvent(loggingCode = "foo", message = "log one")
        val recordTwo = makeLoggingEvent(loggingCode = "bar", message = "log two")

        val console = LoggingConsole()
        console.log(recordOne)
        console.log(recordTwo)

        val text = console.getText().split("\n")
        text[1] shouldBe "${currentTimeLogString()}   [foo] log one"
        text[2] shouldBe "${currentTimeLogString()}   [bar] log two"
    }

    @Test
    fun `Should log a regular INFO log in green`() {
        val console = LoggingConsole()
        val infoLog = makeLoggingEvent(severity = Level.INFO)

        console.log(infoLog)
        console.getTextColour() shouldBe Color.GREEN
    }

    @Test
    fun `Should log an ERROR log in red`() {
        val console = LoggingConsole()
        val errorLog = makeLoggingEvent(severity = Level.ERROR)

        console.log(errorLog)
        console.getTextColour() shouldBe Color.RED
    }

    @Test
    fun `Should log the error message and stack trace`() {
        val console = LoggingConsole()
        val t = Throwable("Boom")

        val errorLog =
            makeLoggingEvent(
                severity = Level.ERROR,
                message = "Failed to load screen",
                errorObject = t,
            )
        console.log(errorLog)

        console.getText() shouldContain "Failed to load screen"
        console.getText() shouldContain "java.lang.Throwable: Boom"

        val endColour = console.getTextColour(console.doc.length - 1)
        endColour shouldBe Color.RED
    }

    @Test
    fun `Should log thread stacks`() {
        val console = LoggingConsole()

        val threadStackLock =
            makeLoggingEvent(
                severity = Level.INFO,
                message = "AWT Thread",
                keyValuePairs = mapOf(KEY_STACK to "at Foo.bar(58)"),
            )
        console.log(threadStackLock)

        console.getText() shouldContain "AWT Thread"
        console.getText() shouldContain "at Foo.bar(58)"
    }

    @Test
    fun `Should scroll to the bottom when a new log is added`() {
        val console = LoggingConsole()
        console.pack()
        console.scrollPane.verticalScrollBar.value shouldBe 0

        repeat(50) { console.log(makeLoggingEvent()) }

        flushEdt()
        console.scrollPane.verticalScrollBar.value shouldBeGreaterThan 0
    }

    @Test
    fun `Should support clearing the logs`() {
        val console = LoggingConsole()
        console.log(makeLoggingEvent())

        console.clear()

        console.getText() shouldBe ""
    }

    @Test
    fun `Should update when logging context changes`() {
        val console = LoggingConsole()
        console.contextUpdated(mapOf())
        console.getAllChildComponentsForType<JLabel>().shouldBeEmpty()

        console.contextUpdated(mapOf("appVersion" to "4.1.1"))
        flushEdt()
        val labels = console.getAllChildComponentsForType<JLabel>()
        labels.size shouldBe 1
        labels.first().text shouldBe "appVersion: 4.1.1"

        console.contextUpdated(mapOf("appVersion" to "4.1.1", "devMode" to false))
        flushEdt()
        val newLabels = console.getAllChildComponentsForType<JLabel>()
        newLabels.map { it.text }.shouldContainExactly("appVersion: 4.1.1", "devMode: false")
    }

    private fun LoggingConsole.getText(): String =
        try {
            doc.getText(0, doc.length)
        } catch (t: Throwable) {
            ""
        }

    private fun LoggingConsole.getTextColour(position: Int = 0): Color {
        val style = doc.getCharacterElement(position)
        return StyleConstants.getForeground(style.attributes)
    }
}
