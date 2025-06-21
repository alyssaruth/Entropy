package testCore

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import io.kotest.matchers.maps.shouldContainAll
import io.kotest.matchers.shouldBe
import io.mockk.verify
import logging.KEY_LOGGING_CODE
import logging.getLogFields
import net.logstash.logback.marker.MapEntriesAppendingMarker
import utils.CoreGlobals

fun ILoggingEvent.shouldContainKeyValues(vararg values: Pair<String, Any?>) {
    this.getLogFields().shouldContainAll(mapOf(*values))
}

fun makeLoggingEvent(
    severity: Level = Level.INFO,
    loggingCode: String = "log",
    message: String = "A thing happened",
    errorObject: Throwable? = null,
    keyValuePairs: Map<String, Any?> = mapOf(),
): LoggingEvent {
    val event =
        LoggingEvent(
            "entropy",
            CoreGlobals.slf4jLogger,
            severity,
            message,
            errorObject,
            emptyArray(),
        )

    event.addMarker(MapEntriesAppendingMarker(keyValuePairs + (KEY_LOGGING_CODE to loggingCode)))
    return event
}

fun <T> List<T>.only(): T {
    size shouldBe 1
    return first()
}

fun verifyNotCalled(verifyBlock: io.mockk.MockKVerificationScope.() -> Unit) {
    verify(exactly = 0) { verifyBlock() }
}
