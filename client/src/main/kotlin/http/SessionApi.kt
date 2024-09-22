package http

import http.dto.BeginSessionResponse
import http.dto.DevCommandRequest
import javax.swing.JOptionPane
import kong.unirest.HttpMethod
import util.DialogUtilNew
import util.OnlineConstants
import util.UpdateManager.checkForUpdates

class SessionApi(private val httpClient: HttpClient) {
    fun beginSession(name: String) {
        val response =
            httpClient.doCall<BeginSessionResponse>(
                HttpMethod.POST,
                Routes.DEV_COMMAND,
                DevCommandRequest(name)
            )

        when (response) {
            is FailureResponse -> handleBeginSessionFailure(response)
            is CommunicationError ->
                DialogUtilNew.showError(
                    "Error communicating with server: ${response.unirestException.message}"
                )
            is SuccessResponse<BeginSessionResponse> -> {
                // Errr, store the sessionId someplace, then hook back into legacy code somehow to
                // launch the lobby etc
                val sessionResponse = response.body
            }
        }
    }

    private fun handleBeginSessionFailure(response: FailureResponse<*>) {
        when (response.errorCode) {
            UPDATE_REQUIRED -> {
                val response =
                    DialogUtilNew.showQuestion(
                        "Your client must be updated to connect. Check for updates now?"
                    )

                if (response == JOptionPane.YES_OPTION) {
                    checkForUpdates(OnlineConstants.ENTROPY_VERSION_NUMBER)
                }
            }
            else -> DialogUtilNew.showError("An error occurred: ${response.errorMessage}")
        }
    }
}
