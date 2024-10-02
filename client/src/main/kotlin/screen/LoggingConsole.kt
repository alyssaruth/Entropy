package screen

import bean.FocusableWindow
import bean.WrapLayout
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Component
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTextPane
import javax.swing.border.EmptyBorder
import javax.swing.border.LineBorder
import javax.swing.border.MatteBorder
import javax.swing.text.BadLocationException
import javax.swing.text.DefaultStyledDocument
import javax.swing.text.StyleConstants
import javax.swing.text.StyleContext
import logging.ILogContextListener
import logging.KEY_STACK
import logging.errorObject
import logging.extractStackTrace
import logging.findLogField
import logging.loggingCode
import utils.CoreGlobals
import utils.runOnEventThread

class LoggingConsoleAppender(private val console: LoggingConsole) : AppenderBase<ILoggingEvent>() {
    override fun append(p0: ILoggingEvent) {
        console.log(p0)
    }
}

class LoggingConsole : FocusableWindow(), ILogContextListener {
    override val windowName = "Console"

    val doc = DefaultStyledDocument()
    val scrollPane = JScrollPane()
    private val textArea = JTextPane(doc)
    private val contextPanel = JPanel()

    init {
        title = "Console"
        setSize(1000, 600)
        setLocationRelativeTo(null)
        contentPane.layout = BorderLayout(0, 0)
        contentPane.add(contextPanel, BorderLayout.NORTH)
        contentPane.add(scrollPane)
        textArea.foreground = Color.GREEN
        textArea.background = Color.BLACK
        textArea.isEditable = false
        scrollPane.setViewportView(textArea)

        contextPanel.background = Color.BLACK
        contextPanel.border = MatteBorder(0, 0, 2, 0, Color.GREEN)
        contextPanel.layout = WrapLayout()
    }

    fun log(record: ILoggingEvent) {
        val cx = StyleContext()
        val text = record.toConsoleString()
        val style = cx.addStyle(text, null)

        if (record.level == Level.ERROR) {
            StyleConstants.setForeground(style, Color.RED)
        }

        try {
            doc.insertString(doc.length, "\n$text", style)
            record.errorObject()?.let { doc.insertString(doc.length, "\n$it", style) }

            val threadStack = record.findLogField(KEY_STACK)
            threadStack?.let { doc.insertString(doc.length, "\n$it", style) }

            textArea.select(doc.length, doc.length)
        } catch (ble: BadLocationException) {
            System.err.println("BLE trying to append: $text")
            System.err.println(extractStackTrace(ble))
        }
    }

    override fun contextUpdated(context: Map<String, Any?>) {
        contextPanel.removeAll()
        val labels = context.map(::factoryLabelForContext)

        runOnEventThread {
            labels.forEach { contextPanel.add(it) }

            contextPanel.validate()
            contextPanel.repaint()
        }
    }

    private fun ILoggingEvent.toConsoleString() =
        "${currentTimeLogString()}   [$loggingCode] $message"

    private fun factoryLabelForContext(field: Map.Entry<String, Any?>): Component {
        val label = JLabel("${field.key}: ${field.value}")
        label.foreground = Color.GREEN
        label.setMargins(5)

        val panel = JPanel()
        panel.border = LineBorder(Color.GREEN)
        panel.add(label)
        panel.isOpaque = false
        panel.background = null
        return panel
    }

    private fun JComponent.setMargins(margin: Int) {
        border = EmptyBorder(margin, margin, margin, margin)
    }

    fun clear() {
        textArea.text = ""
    }
}

fun currentTimeLogString() =
    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withLocale(Locale.UK)
        .withZone(ZoneId.systemDefault())
        .format(CoreGlobals.clock.instant())
