package utils

import java.time.Clock
import logging.LogDestinationSystemOut
import logging.Logger
import logging.LoggingConsole

object InjectedThings {
    @JvmField val loggingConsole = LoggingConsole()

    @JvmField var logger: Logger = Logger(listOf(loggingConsole, LogDestinationSystemOut()))
    var clock: Clock = Clock.systemUTC()
}
