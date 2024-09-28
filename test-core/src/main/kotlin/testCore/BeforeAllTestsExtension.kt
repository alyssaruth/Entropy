package testCore

import java.time.Clock
import java.time.ZoneId
import logging.LoggerUncaughtExceptionHandler
import main.kotlin.testCore.CURRENT_TIME
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import utils.InjectedThings
import utils.InjectedThings.slf4jLogger

var doneOneTimeSetup = false

class BeforeAllTestsExtension : BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext?) {
        if (!doneOneTimeSetup) {
            doOneTimeSetup()
            doneOneTimeSetup = true
        }
    }

    private fun doOneTimeSetup() {
        Thread.setDefaultUncaughtExceptionHandler(LoggerUncaughtExceptionHandler())

        InjectedThings.logger = logger
        InjectedThings.clock = Clock.fixed(CURRENT_TIME, ZoneId.of("UTC"))

        listAppender.start()
        slf4jLogger.addAppender(listAppender)
    }
}
