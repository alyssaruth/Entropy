package utils

import ch.qos.logback.classic.Logger as LogbackLogger
import com.fasterxml.jackson.databind.ObjectMapper
import java.time.Clock
import logging.Logger
import org.slf4j.LoggerFactory

object CoreGlobals {
    val slf4jLogger: LogbackLogger = LoggerFactory.getLogger("entropy") as LogbackLogger
    @JvmField var logger: Logger = Logger(slf4jLogger)
    var clock: Clock = Clock.systemUTC()
    val objectMapper = ObjectMapper()
}
