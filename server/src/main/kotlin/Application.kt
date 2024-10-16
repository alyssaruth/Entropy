import auth.Session
import auth.UserConnection
import io.ktor.server.application.*
import java.util.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import plugins.*
import routes.session.SessionService
import server.EntropyServer
import store.MemoryUserConnectionStore
import store.SessionStore
import store.Store
import utils.CoreGlobals

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun koinModule(
    sessionStore: Store<UUID, Session>,
    userConnectionStore: Store<String, UserConnection>,
) = module {
    single { sessionStore }
    single { userConnectionStore }
    single { SessionService(get(), get()) }
}

@Suppress("UNUSED")
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureRouting()
    install(Koin) {
        CoreGlobals.slf4jLogger
        modules(koinModule(SessionStore(), MemoryUserConnectionStore()))
    }

    // Boot old stuff
    EntropyServer.main()
}
