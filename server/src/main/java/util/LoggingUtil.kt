package util

import `object`.ServerRunnable
import `object`.ServerThread
import `object`.UserConnection
import utils.InjectedThings.logger

fun dumpServerThreads() {
    val sb = StringBuilder()
    val dumpTimeMillis = System.currentTimeMillis()

    val backgroundThreads = mutableListOf<Thread>()
    val nonUscThreads = mutableListOf<ServerThread>()
    val hmUscToThreads = mutableMapOf<UserConnection, MutableList<ServerThread>>()
    var loggerThread: Thread? = null
    var idleWorkers = 0

    val threads = Thread.getAllStackTraces()
    val it: Iterator<Thread> = threads.keys.iterator()
    while (it.hasNext()) {
        val t = it.next()
        if (t !is ServerThread) {
            if (t.name == "Logger") {
                loggerThread = t
            } else {
                backgroundThreads.add(t)
            }

            continue
        }

        val r: ServerRunnable = t.runnable
        val usc = r.userConnection
        if (usc != null) {
            val hashedThreads = hmUscToThreads.getOrPut(usc, ::mutableListOf)
            hashedThreads.add(t)
        } else if (r.getDetails() == "No current task") {
            idleWorkers++
        } else {
            nonUscThreads.add(t)
        }
    }

    // Now dump the threads, grouped together
    sb.append("Threads @ ")
    sb.append(Debug.getCurrentTimeForLogging())
    sb.append("\n\n")

    sb.append("Permanent threads\n")
    sb.append("-----------------")
    sb.append("\n\n")

    // Dump the non-usc threads first. These are "permanent" threads, so not interested in time
    // running
    dumpServerThreadDetails(sb, nonUscThreads, -1)
    dumpOrdinaryThreadDetails(sb, loggerThread)
    sb.append("\n\n")

    // Idle worker count, nothing interesting to log for these so don't log a line for each
    if (idleWorkers > 0) {
        sb.append("Idle workers: ")
        sb.append(idleWorkers)
        sb.append("\n\n")
    }

    // Dump the usc threads if we have some - these are the most interesting
    if (hmUscToThreads.isNotEmpty()) {
        sb.append("Active workers\n")
        sb.append("-----------------")
        sb.append("\n\n")

        val itUsc: Iterator<List<ServerThread>> = hmUscToThreads.values.iterator()
        while (itUsc.hasNext()) {
            val serverThreadsForUsc = itUsc.next()
            dumpServerThreadDetails(sb, serverThreadsForUsc, dumpTimeMillis)
        }

        sb.append("\n\n")
    }

    // Dump basic info about other JVM threads
    sb.append("JVM threads\n")
    sb.append("-----------------")
    sb.append("\n\n")

    for (i in backgroundThreads.indices) {
        val t = backgroundThreads[i]
        dumpOrdinaryThreadDetails(sb, t)
    }

    logger.info("threads", sb.toString())
}

private fun dumpServerThreadDetails(
    sb: StringBuilder,
    serverThreads: List<ServerThread>,
    dumpTimeMillis: Long
) {
    for (i in serverThreads.indices) {
        val t: ServerThread = serverThreads[i]
        t.dumpDetails(sb, dumpTimeMillis)
    }
}

private fun dumpOrdinaryThreadDetails(sb: StringBuilder, t: Thread?) {
    val debugStr = t!!.name + " (" + t.state + ")\n"
    sb.append(debugStr)
}
