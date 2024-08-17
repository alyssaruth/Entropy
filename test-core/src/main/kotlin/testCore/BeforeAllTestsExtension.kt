package main.kotlin.testCore

import java.time.Clock
import java.time.ZoneId
import logging.LoggerUncaughtExceptionHandler
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.ExtensionContext
import utils.InjectedThings

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
    }
}
