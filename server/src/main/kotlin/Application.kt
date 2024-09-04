import plugins.*
import io.ktor.server.application.*
import server.EntropyServer

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureSecurity()
    configureRouting()

    // Boot old stuff
    EntropyServer.main(emptyArray())
}
