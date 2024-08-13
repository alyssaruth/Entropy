package utils

import javax.swing.SwingUtilities
import logging.KEY_STACK
import logging.extractThreadStack
import utils.InjectedThings.logger

fun dumpThreadStacks() {
    logger.info("threadStacks", "Dumping thread stacks")

    val threads = Thread.getAllStackTraces()
    val it = threads.keys.iterator()
    while (it.hasNext()) {
        val thread = it.next()
        val stack = thread.stackTrace
        val state = thread.state
        if (stack.isNotEmpty()) {
            logger.info(
                "threadStack",
                "${thread.name} ($state)",
                KEY_STACK to extractThreadStack(stack)
            )
        }
    }
}

fun runOnEventThread(r: (() -> Unit)) {
    if (SwingUtilities.isEventDispatchThread()) {
        r.invoke()
    } else {
        SwingUtilities.invokeLater(r)
    }
}

fun runOnEventThreadBlocking(r: (() -> Unit)) {
    if (SwingUtilities.isEventDispatchThread()) {
        r.invoke()
    } else {
        SwingUtilities.invokeAndWait(r)
    }
}

fun runInOtherThread(r: (() -> Unit)): Thread {
    val t = Thread(r)
    t.start()
    return t
}
