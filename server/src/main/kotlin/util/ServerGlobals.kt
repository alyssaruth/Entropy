package util

import auth.Session
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.TimeUnit
import routes.lobby.LobbyService
import server.EntropyServer
import store.MemoryUserConnectionStore
import store.SessionStore
import store.Store
import store.UserConnectionStore

private const val CORE_POOL_SIZE = 50
private const val MAX_POOL_SIZE = 500
private const val MAX_QUEUE_SIZE = 100
private const val KEEP_ALIVE_TIME = 20

object ServerGlobals {
    val uscStore: UserConnectionStore = MemoryUserConnectionStore()
    val sessionStore: Store<UUID, Session> = SessionStore()

    @JvmField
    val workerPool =
        EntropyThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAX_POOL_SIZE,
            KEEP_ALIVE_TIME.toLong(),
            TimeUnit.SECONDS,
            ArrayBlockingQueue(MAX_QUEUE_SIZE),
        )

    val server: EntropyServer = EntropyServer()

    @JvmField val lobbyService: LobbyService = LobbyService(server, sessionStore, uscStore)
}
