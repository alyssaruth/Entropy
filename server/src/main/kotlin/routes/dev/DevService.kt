package routes.dev

import dto.DevCommand
import formatAsFileSize
import util.Debug
import util.dumpServerThreads
import utils.InjectedThings.logger
import utils.dumpThreadStacks
import java.lang.management.ManagementFactory

class DevService {
    fun processCommand(command: DevCommand) = when (command) {
        DevCommand.DUMP_THREADS -> dumpServerThreads()
        DevCommand.DUMP_THREAD_STACKS -> dumpThreadStacks()
        DevCommand.DUMP_USERS -> TODO()
        DevCommand.DUMP_POOL_STATS -> TODO()
        DevCommand.DUMP_MEMORY -> dumpMemory(false)
        DevCommand.FORCE_GC -> dumpMemory(true)
    }

    private fun dumpMemory(forceGc: Boolean) {
        if (forceGc) {
            logger.info("gc", "Forcing a GC")
            System.gc()
        }

        val memoryList = ManagementFactory.getMemoryPoolMXBeans()
        memoryList.forEach {
            val used = it.usage.used.formatAsFileSize()
            val max = it.usage.max

            val usageStr = if (max == -1L) used else "$used / ${max.formatAsFileSize()}"
            logger.info("memory.usage", "[${it.name}] $usageStr")
        }
    }
}
