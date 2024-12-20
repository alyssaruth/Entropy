package util

import http.DevApi
import http.HealthCheckApi
import http.HttpClient
import http.SessionApi
import http.WebSocketReceiver
import screen.LoggingConsole
import screen.LoggingConsoleAppender
import settings.AbstractSettingStore
import settings.DefaultSettingStore

object ClientGlobals {
    private val baseUrl = "http://localhost:8080"
    val httpClient = HttpClient(baseUrl)
    @JvmField val loggingConsole = LoggingConsole()
    val consoleAppender = LoggingConsoleAppender(loggingConsole)
    val healthCheckApi = HealthCheckApi(httpClient)
    val devApi = DevApi(httpClient)
    @JvmField var sessionApi = SessionApi(httpClient)
    var updateManager = UpdateManager()
    val webSocketReceiver = WebSocketReceiver()
    @JvmField var achievementStore: AbstractSettingStore = DefaultSettingStore("achievements")
}
