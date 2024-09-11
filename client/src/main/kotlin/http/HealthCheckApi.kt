package http

import dto.HealthCheckResponse
import kong.unirest.HttpMethod

class HealthCheckApi(private val httpClient: HttpClient) {
    fun doHealthCheck() {
        httpClient.doCall<HealthCheckResponse>(HttpMethod.GET, "/health-check")
    }
}
