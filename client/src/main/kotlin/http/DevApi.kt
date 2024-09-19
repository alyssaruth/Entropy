package http

import http.dto.DevCommandRequest
import kong.unirest.HttpMethod

class DevApi(private val httpClient: HttpClient) {
    fun doServerCommand(commandString: String) {
        httpClient.doCall<Unit>(
            HttpMethod.POST,
            Routes.DEV_COMMAND,
            DevCommandRequest(commandString)
        )
    }
}
