package http

import http.dto.JoinRoomResponse
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kong.unirest.HttpMethod
import makeFailureResponse
import org.junit.jupiter.api.Test
import testCore.getDialogMessage
import testCore.getErrorDialog
import testCore.runAsync
import util.AbstractClientTest

class RoomApiTest : AbstractClientTest() {
    @Test
    fun `Should show an error if something goes wrong joining a room`() {
        val httpClient = mockHttpClient(makeFailureResponse())

        runAsync { RoomApi(httpClient).joinRoom(mockk(relaxed = true)) }

        getErrorDialog().getDialogMessage() shouldBe
            "An error occurred attempting to join this room"
    }

    private fun mockHttpClient(response: ApiResponse<JoinRoomResponse>): HttpClient {
        val httpClient = mockk<HttpClient>(relaxed = true)
        every {
            httpClient.doCall<JoinRoomResponse>(HttpMethod.POST, Routes.JOIN_ROOM, any())
        } returns response

        return httpClient
    }
}
