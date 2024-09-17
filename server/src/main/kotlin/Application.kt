import io.ktor.server.application.*
import plugins.*
import server.EntropyServer
import util.AbstractClient.devMode

fun main(args: Array<String>) {
    processArgs(args)

    io.ktor.server.netty.EngineMain.main(args)
}

private fun processArgs(args: Array<String>) {
    if (args.contains("devMode")) {
        devMode = true
    }
}

@Suppress("UNUSED")
fun Application.module() {
    configureSerialization()
    configureMonitoring()
    configureRouting()

    // Boot old stuff
    EntropyServer.main()
}
