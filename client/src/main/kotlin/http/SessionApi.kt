package http

import http.dto.BeginSessionRequest
import http.dto.BeginSessionResponse
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import kong.unirest.HttpMethod
import screen.ScreenCache
import util.DialogUtilNew
import util.OnlineConstants
import util.UpdateManager.checkForUpdates

class SessionApi(private val httpClient: HttpClient) {
    fun beginSession(name: String) {
        val response =
            httpClient.doCall<BeginSessionResponse>(
                HttpMethod.POST,
                Routes.BEGIN_SESSION,
                BeginSessionRequest(name)
            )

        when (response) {
            is FailureResponse -> handleBeginSessionFailure(response)
            is CommunicationError ->
                DialogUtilNew.showErrorLater(
                    "Error communicating with server.\n\n${response.unirestException.message}"
                )
            is SuccessResponse<BeginSessionResponse> -> {
                // Errr, store the sessionId someplace, then hook back into legacy code somehow to
                // launch the lobby etc
                val sessionResponse = response.body

                val lobby = ScreenCache.getEntropyLobby()
                lobby.username = sessionResponse.name
                lobby.setLocationRelativeTo(null)
                lobby.isVisible = true
                lobby.init()
            }
        }
    }

    private fun handleBeginSessionFailure(response: FailureResponse<*>) =
        SwingUtilities.invokeLater {
            when (response.errorCode) {
                UPDATE_REQUIRED -> {
                    val ans =
                        DialogUtilNew.showQuestion(
                            "Your client must be updated to connect. Check for updates now?"
                        )

                    if (ans == JOptionPane.YES_OPTION) {
                        checkForUpdates(OnlineConstants.ENTROPY_VERSION_NUMBER)
                    }
                }
                else -> DialogUtilNew.showError("An error occurred.\n\n${response.errorMessage}")
            }
        }
}
