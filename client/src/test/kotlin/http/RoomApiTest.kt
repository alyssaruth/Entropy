package http

import com.github.alyssaburlton.swingtest.flushEdt
import com.github.alyssaburlton.swingtest.getChild
import com.github.alyssaburlton.swingtest.shouldBeVisible
import com.github.alyssaburlton.swingtest.shouldNotBeVisible
import getMessages
import http.Routes.JOIN_ROOM
import http.Routes.SIT_DOWN
import http.Routes.STAND_UP
import http.dto.JoinRoomRequest
import http.dto.JoinRoomResponse
import http.dto.RoomStateResponse
import http.dto.SitDownRequest
import http.dto.StandUpRequest
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.mockk.verify
import kong.unirest.HttpMethod
import makeFailureResponse
import mockHttpClient
import `object`.PlayerLabel
import online.screen.EntropyLobby
import online.screen.GameRoom
import org.apache.http.HttpStatus
import org.junit.jupiter.api.Test
import screen.HandPanelMk2
import screen.ScreenCache
import testCore.AbstractTest
import testCore.getDialogMessage
import testCore.getErrorDialog
import testCore.makeJoinRoomResponse
import testCore.makeRoomStateResponse
import testCore.makeRoomSummary
import testCore.runAsync

class RoomApiTest : AbstractTest() {
    @Test
    fun `Should POST to join a room and launch if successful`() {
        ScreenCache.get<EntropyLobby>().username = "Leah"
        val room = GameRoom.factoryCreate(makeRoomSummary())

        val players = mapOf(0 to "Alyssa")
        val response = makeJoinRoomResponse(players = players)
        val httpClient = mockJoinRoom(SuccessResponse(HttpStatus.SC_OK, response))
        val api = RoomApi(httpClient)
        api.joinRoom(room)

        verify {
            httpClient.doCall<JoinRoomResponse>(
                HttpMethod.POST,
                JOIN_ROOM,
                JoinRoomRequest(room.id),
            )
        }

        flushEdt()
        room.shouldBeVisible()
        room.chatPanel.getMessages() shouldBe response.chatHistory
        room.observer shouldBe true
        room.username shouldBe "Leah"

        val playerLabel = room.getChild<HandPanelMk2>().getChild<PlayerLabel>("PlayerOneLabel")
        playerLabel.shouldBeVisible()
        playerLabel.text.shouldContain("Alyssa")
    }

    @Test
    fun `Should show an error and not launch room if API response is not success`() {
        val room = GameRoom.factoryCreate(makeRoomSummary())

        val httpClient = mockJoinRoom(makeFailureResponse())

        runAsync {
            val api = RoomApi(httpClient)
            api.joinRoom(room)
        }

        room.shouldNotBeVisible()

        val error = getErrorDialog()
        error.getDialogMessage() shouldBe "An error occurred attempting to join this room"
    }

    @Test
    fun `Should POST to take a seat and update the room if successful`() {
        val room = GameRoom.factoryCreate(makeRoomSummary())
        room.username = "Alyssa"

        val players = mapOf(2 to "Alyssa")
        val response = makeRoomStateResponse(players = players)
        val httpClient = mockSitDown(SuccessResponse(HttpStatus.SC_OK, response))
        val api = RoomApi(httpClient)
        api.sitDown(room, 2)

        verify {
            httpClient.doCall<RoomStateResponse>(
                HttpMethod.POST,
                SIT_DOWN,
                SitDownRequest(room.id, 2),
            )
        }

        room.observer shouldBe false
        room.playerNumber shouldBe 2

        val playerLabel = room.getChild<HandPanelMk2>().getChild<PlayerLabel>("PlayerOneLabel")
        playerLabel.shouldBeVisible()
        playerLabel.text.shouldContain("Alyssa")
    }

    @Test
    fun `Should show an error if a seat has been taken`() {
        val room = GameRoom.factoryCreate(makeRoomSummary())
        room.observer = true

        val httpClient = mockSitDown(makeFailureResponse(clientErrorCode = SEAT_TAKEN))

        runAsync {
            val api = RoomApi(httpClient)
            api.sitDown(room, 2)
        }

        room.observer shouldBe true

        val error = getErrorDialog()
        error.getDialogMessage() shouldBe "This seat has been taken."
    }

    @Test
    fun `Should show an error if something else goes wrong taking a seat`() {
        val room = GameRoom.factoryCreate(makeRoomSummary())
        room.observer = true

        val httpClient = mockSitDown(makeFailureResponse())

        runAsync {
            val api = RoomApi(httpClient)
            api.sitDown(room, 2)
        }

        room.observer shouldBe true

        val error = getErrorDialog()
        error.getDialogMessage() shouldBe "An unexpected error occurred trying to take a seat."
    }

    @Test
    fun `Should POST to stand up and update the room if successful`() {
        val room = GameRoom.factoryCreate(makeRoomSummary())
        room.username = "Alyssa"
        room.observer = false
        room.addOrUpdatePlayer(0, "Alyssa")

        val response = makeRoomStateResponse(players = emptyMap())
        val httpClient = mockStandUp(SuccessResponse(HttpStatus.SC_OK, response))
        val api = RoomApi(httpClient)
        api.standUp(room)

        verify {
            httpClient.doCall<RoomStateResponse>(HttpMethod.POST, STAND_UP, StandUpRequest(room.id))
        }

        room.observer shouldBe true
        room.playerNumber shouldBe -1

        val playerLabel = room.getChild<HandPanelMk2>().getChild<PlayerLabel>("PlayerOneLabel")
        playerLabel.shouldNotBeVisible()
    }

    @Test
    fun `Should show an error if something else goes wrong standing up`() {
        val room = GameRoom.factoryCreate(makeRoomSummary())
        room.username = "Alyssa"
        room.observer = false
        room.addOrUpdatePlayer(0, "Alyssa")

        val httpClient = mockStandUp(makeFailureResponse())

        runAsync {
            val api = RoomApi(httpClient)
            api.standUp(room)
        }

        room.observer shouldBe false

        val error = getErrorDialog()
        error.getDialogMessage() shouldBe "An error occurred attempting to stand up."
    }

    private fun mockJoinRoom(response: ApiResponse<JoinRoomResponse>): HttpClient =
        mockHttpClient(response, HttpMethod.POST, JOIN_ROOM)

    private fun mockSitDown(response: ApiResponse<RoomStateResponse>): HttpClient =
        mockHttpClient(response, HttpMethod.POST, SIT_DOWN)

    private fun mockStandUp(response: ApiResponse<RoomStateResponse>): HttpClient =
        mockHttpClient(response, HttpMethod.POST, STAND_UP)
}
