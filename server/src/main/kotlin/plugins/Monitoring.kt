package plugins

import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.callloging.CallLogging
import java.util.UUID
import logging.KEY_REQUEST_ID
import logging.KEY_ROUTE
import org.slf4j.event.Level
import utils.CoreGlobals

fun Application.configureMonitoring() {
    install(CallLogging) {
        level = Level.INFO
        logger = CoreGlobals.slf4jLogger

        filter { false }
        callIdMdc(KEY_REQUEST_ID)
        mdc(KEY_ROUTE) { it.logStr() }
    }
    install(RequestTracing)
    install(CallId) {
        header(HttpHeaders.XRequestId)
        generate { UUID.randomUUID().toString() }
        verify { callId: String -> callId.isNotEmpty() }
    }
}
