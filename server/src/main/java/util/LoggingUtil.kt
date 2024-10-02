package util

import `object`.ServerThread
import utils.CoreGlobals.logger

fun dumpServerThreads() {
    val sb = StringBuilder()
    val dumpTimeMillis = System.currentTimeMillis()

    val threads = Thread.getAllStackTraces().keys
    val serverThreads = threads.filterIsInstance<ServerThread>()
    val backgroundThreads = threads - serverThreads

    val nonUscThreads = serverThreads.filter { it.runnable.userConnection == null }
    val uscThreads = serverThreads.filter { it.runnable.userConnection != null }
    val idleCount = nonUscThreads.count { it.runnable.details == "No current task" }

    // Now dump the threads, grouped together
    sb.append("Threads @ ")
    sb.append(Debug.getCurrentTimeForLogging())
    sb.append("\n\n")

    sb.append("Permanent threads\n")
    sb.append("-----------------")
    sb.append("\n\n")

    // Dump the non-usc threads first.
    dumpServerThreadDetails(sb, nonUscThreads, -1)
    sb.append("\n\n")
    sb.append("Idle workers: $idleCount\n\n")

    // Dump the usc threads if we have some - these are the most interesting
    sb.append("Active workers\n")
    sb.append("-----------------")
    sb.append("\n\n")
    dumpServerThreadDetails(sb, uscThreads, dumpTimeMillis)
    sb.append("\n\n")

    // Dump basic info about other JVM threads
    sb.append("JVM threads\n")
    sb.append("-----------------")
    sb.append("\n\n")
    backgroundThreads.forEach { dumpOrdinaryThreadDetails(sb, it) }

    logger.info("threads", sb.toString())
}

private fun dumpServerThreadDetails(
    sb: StringBuilder,
    serverThreads: List<ServerThread>,
    dumpTimeMillis: Long
) {
    serverThreads.forEach { it.dumpDetails(sb, dumpTimeMillis) }
}

private fun dumpOrdinaryThreadDetails(sb: StringBuilder, t: Thread) {
    sb.append("${t.name} (${t.state})\n")
}
