package utils

import java.time.Clock
import logging.LogDestinationSystemOut
import logging.Logger
import logging.LoggingConsole
import org.slf4j.LoggerFactory

object InjectedThings {
    @JvmField val loggingConsole = LoggingConsole()

    val slf4jLogger: org.slf4j.Logger = LoggerFactory.getLogger("glean")
    @JvmField
    var logger: Logger = Logger(slf4jLogger, listOf(loggingConsole, LogDestinationSystemOut()))
    var clock: Clock = Clock.systemUTC()
}
