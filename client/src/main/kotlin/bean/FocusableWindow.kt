package bean

import java.awt.event.WindowEvent
import java.awt.event.WindowFocusListener
import javax.swing.JFrame
import logging.KEY_ACTIVE_WINDOW
import utils.CoreGlobals

abstract class FocusableWindow : JFrame(), WindowFocusListener {
    abstract val windowName: String

    init {
        addWindowFocusListener(this)
    }

    override fun windowGainedFocus(e: WindowEvent?) {
        CoreGlobals.logger.addToContext(KEY_ACTIVE_WINDOW, windowName)
    }

    override fun windowLostFocus(e: WindowEvent?) {}
}
