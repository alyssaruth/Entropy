package http

import com.github.alyssaburlton.swingtest.clickNo
import com.github.alyssaburlton.swingtest.clickYes
import com.github.alyssaburlton.swingtest.flushEdt
import http.Routes.BEGIN_SESSION
import http.dto.BeginSessionRequest
import http.dto.BeginSessionResponse
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import kong.unirest.HttpMethod
import kong.unirest.UnirestException
import main.kotlin.testCore.getDialogMessage
import main.kotlin.testCore.getErrorDialog
import main.kotlin.testCore.getQuestionDialog
import main.kotlin.testCore.verifyNotCalled
import online.screen.EntropyLobby
import org.junit.jupiter.api.Test
import screen.ScreenCache
import testCore.AbstractTest
import util.Globals
import util.OnlineConstants

class SessionApiTest : AbstractTest() {
    @Test
    fun `should POST to the correct endpoint`() {
        val httpClient = mockk<HttpClient>(relaxed = true)

        val api = SessionApi(httpClient)
        api.beginSession("alyssa")

        verify {
            httpClient.doCall<BeginSessionResponse>(
                HttpMethod.POST,
                BEGIN_SESSION,
                BeginSessionRequest("alyssa"),
            )
        }
    }

    @Test
    fun `should handle a response indicating an update is required and check for updates`() {
        Globals.updateManager = mockk(relaxed = true)
        val httpClient = mockHttpClient(FailureResponse(422, UPDATE_REQUIRED, "oh no"))

        SessionApi(httpClient).beginSession("alyssa")
        flushEdt()

        val questionDialog = getQuestionDialog()
        questionDialog.getDialogMessage() shouldBe
            "Your client must be updated to connect. Check for updates now?"

        questionDialog.clickYes()
        verify { Globals.updateManager.checkForUpdates(OnlineConstants.ENTROPY_VERSION_NUMBER) }
    }

    @Test
    fun `should not check for updates if 'No' is answered`() {
        Globals.updateManager = mockk(relaxed = true)
        val httpClient = mockHttpClient(FailureResponse(422, UPDATE_REQUIRED, "oh no"))

        SessionApi(httpClient).beginSession("alyssa")
        flushEdt()

        val questionDialog = getQuestionDialog()
        questionDialog.getDialogMessage() shouldBe
            "Your client must be updated to connect. Check for updates now?"

        questionDialog.clickNo()
        verifyNotCalled { Globals.updateManager.checkForUpdates(any()) }
    }

    @Test
    fun `should show an error for an unexpected error`() {
        val httpClient =
            mockHttpClient(FailureResponse(422, ClientErrorCode("bad"), "Internal Server Error"))

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
    fun `should boot the lobby on success`() {
        val mockLobby = mockk<EntropyLobby>(relaxed = true)
        ScreenCache.setEntropyLobby(mockLobby)
        val httpClient =
            mockHttpClient(SuccessResponse(200, BeginSessionResponse("alyssa", UUID.randomUUID())))

        SessionApi(httpClient).beginSession("alyssa")

        val lobby = ScreenCache.getEntropyLobby()
        verify {
            lobby.username = "alyssa"
            lobby.isVisible = true
            lobby.init()
        }
    }

    private fun mockHttpClient(response: ApiResponse<BeginSessionResponse>): HttpClient {
        val httpClient = mockk<HttpClient>(relaxed = true)
        every {
            httpClient.doCall<BeginSessionResponse>(HttpMethod.POST, BEGIN_SESSION, any())
        } returns response

        return httpClient
    }
}
