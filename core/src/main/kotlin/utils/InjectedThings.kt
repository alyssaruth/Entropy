package utils

import logging.LogDestinationSystemOut
import logging.Logger
import logging.LoggingConsole
import java.time.Clock

object InjectedThings {
    @JvmField
    val loggingConsole = LoggingConsole()

    @JvmField
    var logger: Logger = Logger(listOf(loggingConsole, LogDestinationSystemOut()))
    var clock: Clock = Clock.systemUTC()
}
