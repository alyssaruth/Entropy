package plugins

import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.createRouteScopedPlugin
import io.ktor.server.application.hooks.CallSetup
import io.ktor.server.application.hooks.ResponseSent
import io.ktor.server.plugins.origin
import io.ktor.server.request.httpMethod
import io.ktor.util.AttributeKey
import logging.KEY_DURATION
import utils.CoreGlobals.logger

val RequestTracing =
    createRouteScopedPlugin("RequestTracePlugin", {}) {
        val callStart = AttributeKey<Long>("callStart")
        on(CallSetup) { it.attributes.put(callStart, System.currentTimeMillis()) }

        onCall { logger.info("callReceived", "Received call ${it.logStr()}") }

        on(ResponseSent) { call ->
            val duration = System.currentTimeMillis() - call.attributes[callStart]
            logger.info(
                "callHandled",
                "Handled call ${call.logStr()}: ${call.response.status()} (${duration}ms)",
                KEY_DURATION to duration,
                "status" to call.response.status(),
            )
        }
    }

fun ApplicationCall.logStr() = "${request.httpMethod.value} ${request.origin.uri}"
