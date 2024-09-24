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
import kong.unirest.HttpMethod
import main.kotlin.testCore.getDialogMessage
import main.kotlin.testCore.getQuestionDialog
import main.kotlin.testCore.verifyNotCalled
import org.junit.jupiter.api.Test
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

    private fun mockHttpClient(response: ApiResponse<BeginSessionResponse>): HttpClient {
        val httpClient = mockk<HttpClient>(relaxed = true)
        every {
            httpClient.doCall<BeginSessionResponse>(HttpMethod.POST, BEGIN_SESSION, any())
        } returns FailureResponse(422, UPDATE_REQUIRED, "oh no")

        return httpClient
    }
}
