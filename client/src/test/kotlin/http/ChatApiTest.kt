package http

import http.dto.NewChatRequest
import io.mockk.mockk
import io.mockk.verify
import kong.unirest.HttpMethod
import org.junit.jupiter.api.Test
import util.AbstractClientTest

class ChatApiTest : AbstractClientTest() {
    @Test
    fun `Should send chat message to correct endpoint`() {
        val httpClient = mockk<HttpClient>(relaxed = true)

        val api = ChatApi(httpClient)
        api.sendChat("hi everyone :)", "Barium I")

        verify {
            httpClient.doCall<Unit>(
                HttpMethod.PUT,
                Routes.CHAT,
                NewChatRequest("hi everyone :)", "Barium I"),
            )
        }
    }
}
