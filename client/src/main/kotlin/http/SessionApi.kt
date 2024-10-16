package http

import http.dto.BeginSessionRequest
import http.dto.BeginSessionResponse
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import kong.unirest.HttpMethod
import screen.ScreenCache
import util.ClientGlobals
import util.DialogUtilNew
import util.OnlineConstants
import util.getAchievementCount

class SessionApi(private val httpClient: HttpClient) {
    fun beginSession(name: String) {
        val response =
            httpClient.doCall<BeginSessionResponse>(
                HttpMethod.POST,
                Routes.BEGIN_SESSION,
                BeginSessionRequest(name, getAchievementCount()),
            )

        when (response) {
            is FailureResponse -> handleBeginSessionFailure(response)
            is CommunicationError ->
                DialogUtilNew.showErrorLater(
                    "Error communicating with server.\n\n${response.unirestException.message}"
                )
            is SuccessResponse<BeginSessionResponse> -> handleConnectSuccess(response.body)
        }
    }

    private fun handleConnectSuccess(response: BeginSessionResponse) {
        val lobby = ScreenCache.getEntropyLobby()
        lobby.username = response.name
        lobby.setLocationRelativeTo(null)
        lobby.isVisible = true
        lobby.init(response.lobby)
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
                        ClientGlobals.updateManager.checkForUpdates(
                            OnlineConstants.ENTROPY_VERSION_NUMBER
                        )
                    }
                }
                else -> DialogUtilNew.showError("An error occurred.\n\n${response.errorMessage}")
            }
        }
}
