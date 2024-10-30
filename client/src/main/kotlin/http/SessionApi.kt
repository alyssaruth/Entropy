package http

import achievement.getAchievementsEarned
import http.dto.BeginSessionRequest
import http.dto.BeginSessionResponse
import http.dto.UpdateAchievementCountRequest
import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import kong.unirest.HttpMethod
import online.screen.EntropyLobby
import screen.ScreenCache
import util.ClientGlobals
import util.DialogUtilNew
import util.OnlineConstants

class SessionApi(private val httpClient: HttpClient) {
    fun beginSession(name: String) {
        val response =
            httpClient.doCall<BeginSessionResponse>(
                HttpMethod.POST,
                Routes.BEGIN_SESSION,
                BeginSessionRequest(name, getAchievementsEarned()),
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

    fun updateAchievementCount(achievementCount: Int) {
        httpClient.doCall<UpdateAchievementCountRequest>(
            HttpMethod.POST,
            Routes.ACHIEVEMENT_COUNT,
            UpdateAchievementCountRequest(achievementCount),
        )
    }

    private fun handleConnectSuccess(response: BeginSessionResponse) {
        ClientGlobals.httpClient.sessionId = response.sessionId

        val lobby = ScreenCache.get<EntropyLobby>()
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
