import io.ktor.server.application.*
import plugins.*
import server.EntropyServer

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

@Suppress("UNUSED")
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureRouting()

    // Boot old stuff
    EntropyServer.main()
}
