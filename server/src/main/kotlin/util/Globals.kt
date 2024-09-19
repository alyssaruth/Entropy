package util

import auth.UserConnection
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import store.MemoryUserConnectionStore
import store.Store

private const val CORE_POOL_SIZE = 50
private const val MAX_POOL_SIZE = 500
private const val MAX_QUEUE_SIZE = 100
private const val KEEP_ALIVE_TIME = 20

object Globals {
    val uscStore: Store<UserConnection> = MemoryUserConnectionStore()
    @JvmField
    val workerPool =
        EntropyThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME.toLong(),
            TimeUnit.SECONDS,
            ArrayBlockingQueue(MAX_QUEUE_SIZE)
        )
}
