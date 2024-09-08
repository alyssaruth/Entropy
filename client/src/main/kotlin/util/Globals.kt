package util

import http.HealthCheckApi
import http.HttpClient

object Globals {
    val httpClient = HttpClient()
    val baseUrl = "http://localhost:8080"
    var healthCheckApi = HealthCheckApi(httpClient)
}
