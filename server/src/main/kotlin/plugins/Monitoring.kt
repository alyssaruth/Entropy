package plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.callid.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.request.*
import org.slf4j.event.*
import utils.CoreGlobals

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        logger = CoreGlobals.slf4jLogger

        filter { false }
        callIdMdc("call-id")
        mdc("route") { "${it.request.httpMethod.value} ${it.request.origin.uri}" }
    }
    install(CallId) {
        header(HttpHeaders.XRequestId)
        verify { callId: String -> callId.isNotEmpty() }
    }
}
