package routes.dev

import formatAsFileSize
import http.ClientErrorCode
import io.ktor.http.*
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit
import routes.ClientException
import util.ServerGlobals
import util.dumpServerThreads
import utils.CoreGlobals.logger
import utils.dumpThreadStacks

class DevService {
    fun processCommand(commandStr: String) {
        val command =
            DevCommand.entries.find { it.value == commandStr }
                ?: throw ClientException(
                    HttpStatusCode.BadRequest,
                    ClientErrorCode("invalidCommand"),
                    "Command must be one of [${DevCommand.entries.map { it.value }}]"
                )

        when (command) {
            DevCommand.DUMP_THREADS -> dumpServerThreads()
            DevCommand.DUMP_THREAD_STACKS -> dumpThreadStacks()
            DevCommand.DUMP_USERS -> dumpUsers()
            DevCommand.DUMP_POOL_STATS -> dumpPoolStats()
            DevCommand.DUMP_MEMORY -> dumpMemory(false)
            DevCommand.FORCE_GC -> dumpMemory(true)
        }
    }

    private fun dumpUsers() {
        val uscs = ServerGlobals.uscStore.getAll()
        logger.info("usc.count", "${uscs.size} user(s) online.")
        uscs.forEach { logger.info("usc", it.toString()) }
    }

    private fun dumpPoolStats() {
        val workerPool = ServerGlobals.workerPool
        val blockQueue = workerPool.queue

        logger.info(
            "pool.config",
            "Max [${workerPool.maximumPoolSize}], Core [${workerPool.corePoolSize}], KeepAlive [${workerPool.getKeepAliveTime(TimeUnit.SECONDS)}s]"
        )

        logger.info(
            "pool.state",
            "Size [${workerPool.poolSize}], Active [${workerPool.activeCount}, Largest [${workerPool.largestPoolSize}]"
        )

        logger.info(
            "pool.completionStatus",
            "Completed ${workerPool.completedTaskCount} / ${workerPool.taskCount}"
        )

        logger.info(
            "queue.state",
            "Size [${blockQueue.size}], remaining capacity [${blockQueue.remainingCapacity()}]"
        )
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
