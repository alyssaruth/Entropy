package util

import http.DevApi
import http.HealthCheckApi
import http.HttpClient

object Globals {
    private val baseUrl = "http://localhost:8080"
    private val httpClient = HttpClient(baseUrl)
    val healthCheckApi = HealthCheckApi(httpClient)
    val devApi = DevApi(httpClient)
}
