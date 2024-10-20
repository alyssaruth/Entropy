package util

import http.DevApi
import http.HealthCheckApi
import http.HttpClient
import http.SessionApi
import http.WebSocketReceiver
import screen.LoggingConsole
import screen.LoggingConsoleAppender

object ClientGlobals {
    private val baseUrl = "http://localhost:8080"
    private val httpClient = HttpClient(baseUrl)
    @JvmField val loggingConsole = LoggingConsole()
    val consoleAppender = LoggingConsoleAppender(loggingConsole)
    val healthCheckApi = HealthCheckApi(httpClient)
    val devApi = DevApi(httpClient)
    var sessionApi = SessionApi(httpClient)
    var updateManager = UpdateManager()
    val webSocketReceiver = WebSocketReceiver()
}
