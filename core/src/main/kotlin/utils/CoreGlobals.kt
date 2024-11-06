package utils

import ch.qos.logback.classic.Logger as LogbackLogger
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.time.Clock
import logging.Logger
import org.slf4j.LoggerFactory

object CoreGlobals {
    val slf4jLogger: LogbackLogger = LoggerFactory.getLogger("entropy") as LogbackLogger
    @JvmField var logger: Logger = Logger(slf4jLogger)
    var clock: Clock = Clock.systemUTC()
    val jsonMapper: ObjectMapper =
        JsonMapper()
            .registerKotlinModule()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}
