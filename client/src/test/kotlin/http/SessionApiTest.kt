package http

import com.github.alyssaburlton.swingtest.clickNo
import com.github.alyssaburlton.swingtest.clickYes
import com.github.alyssaburlton.swingtest.flushEdt
import http.Routes.BEGIN_SESSION
import http.dto.BeginSessionRequest
import http.dto.BeginSessionResponse
import http.dto.LobbyMessage
import http.dto.UpdateAchievementCountRequest
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import kong.unirest.HttpMethod
import kong.unirest.UnirestException
import makeFailureResponse
import online.screen.EntropyLobby
import org.junit.jupiter.api.Test
import screen.ScreenCache
import testCore.getDialogMessage
import testCore.getErrorDialog
import testCore.getQuestionDialog
import testCore.verifyNotCalled
import util.AbstractClientTest
import util.ClientGlobals
import util.OnlineConstants
import util.put

class SessionApiTest : AbstractClientTest() {
    @Test
    fun `should POST to the correct endpoint`() {
        val httpClient = mockk<HttpClient>(relaxed = true)

        val api = SessionApi(httpClient)
        api.beginSession("alyssa")

        verify {
            httpClient.doCall<BeginSessionResponse>(
                HttpMethod.POST,
                BEGIN_SESSION,
                BeginSessionRequest("alyssa", 0),
            )
        }
    }

    @Test
    fun `should handle a response indicating an update is required and check for updates`() {
        ClientGlobals.updateManager = mockk(relaxed = true)
        val httpClient = mockHttpClient(makeFailureResponse(UPDATE_REQUIRED))

        SessionApi(httpClient).beginSession("alyssa")
        flushEdt()

        val questionDialog = getQuestionDialog()
        questionDialog.getDialogMessage() shouldBe
            "Your client must be updated to connect. Check for updates now?"

        questionDialog.clickYes(async = true)
        verify {
            ClientGlobals.updateManager.checkForUpdates(OnlineConstants.ENTROPY_VERSION_NUMBER)
        }
    }

    @Test
    fun `should not check for updates if 'No' is answered`() {
        ClientGlobals.updateManager = mockk(relaxed = true)
        val httpClient = mockHttpClient(makeFailureResponse(UPDATE_REQUIRED))

        SessionApi(httpClient).beginSession("alyssa")
        flushEdt()

        val questionDialog = getQuestionDialog()
        questionDialog.getDialogMessage() shouldBe
            "Your client must be updated to connect. Check for updates now?"

        questionDialog.clickNo()
        verifyNotCalled { ClientGlobals.updateManager.checkForUpdates(any()) }
    }

    @Test
    fun `should show an error for an unexpected error`() {
        val httpClient =
            mockHttpClient(makeFailureResponse(ClientErrorCode("bad"), "Internal Server Error"))

        SessionApi(httpClient).beginSession("alyssa")
        flushEdt()

        getErrorDialog().getDialogMessage() shouldBe "An error occurred.\n\nInternal Server Error"
    }

    @Test
    fun `should show an error for a communication error`() {
        val httpClient =
            mockHttpClient(CommunicationError(UnirestException("Connection timed out.")))

        SessionApi(httpClient).beginSession("alyssa")
        flushEdt()

        getErrorDialog().getDialogMessage() shouldBe
            "Error communicating with server.\n\nConnection timed out."
    }

    @Test
    fun `should boot the lobby and set session id on success`() {
        val mockLobby = mockk<EntropyLobby>(relaxed = true)
        ScreenCache.put(mockLobby)

        val lobbyMessage = LobbyMessage(emptyList(), emptyList())
        val sessionId = UUID.randomUUID()
        val httpClient =
            mockHttpClient(
                SuccessResponse(200, BeginSessionResponse("alyssa", sessionId, lobbyMessage))
            )

        SessionApi(httpClient).beginSession("alyssa")

        ClientGlobals.httpClient.sessionId shouldBe sessionId

        val lobby = ScreenCache.get<EntropyLobby>()
        verify {
            lobby.username = "alyssa"
            lobby.isVisible = true
            lobby.init(lobbyMessage)
        }
    }

    @Test
    fun `should be able to update achievement count`() {
        val httpClient = mockk<HttpClient>(relaxed = true)

        SessionApi(httpClient).updateAchievementCount(8)

        verify {
            httpClient.doCall<Unit>(
                HttpMethod.POST,
                Routes.ACHIEVEMENT_COUNT,
                UpdateAchievementCountRequest(8),
            )
        }
    }

    @Test
    fun `should send a finish session request and clear the session id`() {
        ClientGlobals.httpClient.sessionId = UUID.randomUUID()

        val httpClient = mockk<HttpClient>(relaxed = true)
        SessionApi(httpClient).finishSession()

        verify { httpClient.doCall<Unit>(HttpMethod.POST, Routes.FINISH_SESSION) }

        ClientGlobals.httpClient.sessionId shouldBe null
    }

    private fun mockHttpClient(response: ApiResponse<BeginSessionResponse>): HttpClient {
        val httpClient = mockk<HttpClient>(relaxed = true)
        every {
            httpClient.doCall<BeginSessionResponse>(HttpMethod.POST, BEGIN_SESSION, any())
        } returns response

        return httpClient
    }
}
