package http

import dto.HealthCheckResponse

class HealthCheckApi(private val httpClient: HttpClient) {
    fun doHealthCheck() {
        httpClient.doCall<HealthCheckResponse>("GET", "/health-check")
    }
}
