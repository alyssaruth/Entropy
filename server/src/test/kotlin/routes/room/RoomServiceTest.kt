package routes.room

import http.INVALID_ROOM_ID
import http.SEAT_TAKEN
import http.dto.JoinRoomRequest
import http.dto.JoinRoomResponse
import http.dto.RoomStateResponse
import http.dto.SitDownRequest
import http.dto.StandUpRequest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import java.util.UUID
import org.junit.jupiter.api.Test
import routes.ClientException
import store.RoomStore
import testCore.AbstractTest
import testCore.makeOnlineMessage
import util.makeRoom
import util.makeSession

private val VALID_ROOM_ID = UUID.randomUUID()
private val NON_EXISTENT_ROOM_ID = UUID.randomUUID()

class RoomServiceTest : AbstractTest() {

    @Test
    fun `joinRoom should throw a client error if room ID is not valid`() {
        val (service) = makeService()
        val ex =
            shouldThrow<ClientException> {
                service.joinRoom(makeSession(), JoinRoomRequest(NON_EXISTENT_ROOM_ID))
            }

        ex.errorCode shouldBe INVALID_ROOM_ID
    }

    @Test
    fun `Should successfully join a room and return its details`() {
        val (service, roomStore) = makeService()
        val chatMessage = makeOnlineMessage()
        val room = roomStore.get(VALID_ROOM_ID)
        room.addToChatHistory(chatMessage)
        room.attemptToSitDown("Rafi", 1)

        val session = makeSession(name = "Kevin")
        val response = service.joinRoom(session, JoinRoomRequest(VALID_ROOM_ID))

        response shouldBe JoinRoomResponse(listOf(chatMessage), mapOf(1 to "Rafi"), emptyMap())
    }

    @Test
    fun `sitDown should throw a client error if room ID is not valid`() {
        val (service) = makeService()
        val ex =
            shouldThrow<ClientException> {
                service.sitDown(makeSession(), SitDownRequest(NON_EXISTENT_ROOM_ID, 3))
            }

        ex.errorCode shouldBe INVALID_ROOM_ID
    }

    @Test
    fun `sitDown should throw a client error if the seat is already occupied`() {
        val (service, roomStore) = makeService()
        val room = roomStore.get(VALID_ROOM_ID)
        room.attemptToSitDown("Kevin", 1)
        val ex =
            shouldThrow<ClientException> {
                service.sitDown(makeSession(), SitDownRequest(VALID_ROOM_ID, 1))
            }

        ex.errorCode shouldBe SEAT_TAKEN
    }

    @Test
    fun `sitDown should allow an empty seat to be taken, and respond with updated state`() {
        val (service, roomStore) = makeService()
        val room = roomStore.get(VALID_ROOM_ID)
        room.attemptToSitDown("Kevin", 1)
        val response = service.sitDown(makeSession(name = "Bob"), SitDownRequest(VALID_ROOM_ID, 2))
        response shouldBe RoomStateResponse(mapOf(1 to "Kevin", 2 to "Bob"), emptyMap())
    }

    @Test
    fun `standUp should throw a client error if room ID is not valid`() {
        val (service) = makeService()
        val ex =
            shouldThrow<ClientException> {
                service.standUp(makeSession(), StandUpRequest(NON_EXISTENT_ROOM_ID))
            }

        ex.errorCode shouldBe INVALID_ROOM_ID
    }

    @Test
    fun `standUp should return a player to observer status`() {
        val (service, roomStore) = makeService()
        val room = roomStore.get(VALID_ROOM_ID)
        room.attemptToSitDown("Kevin", 1)
        room.attemptToSitDown("Bob", 2)
        val response = service.standUp(makeSession(name = "Kevin"), StandUpRequest(VALID_ROOM_ID))
        response shouldBe RoomStateResponse(mapOf(2 to "Bob"), emptyMap())
    }

    private fun makeService(): Pair<RoomService, RoomStore> {
        val roomStore = RoomStore()
        val room = makeRoom(id = VALID_ROOM_ID)
        roomStore.put(room)
        return RoomService(roomStore) to roomStore
    }
}
