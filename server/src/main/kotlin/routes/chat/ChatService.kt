package routes.chat

import auth.Session
import auth.UserConnection
import http.INVALID_ROOM_ID
import http.dto.NewChatMessage
import http.dto.NewChatRequest
import http.dto.OnlineMessage
import io.ktor.http.*
import routes.ClientException
import server.EntropyServer
import store.RoomStore
import store.UserConnectionStore
import util.XmlConstants
import utils.CoreGlobals.jsonMapper

class ChatService(
    private val server: EntropyServer,
    private val roomStore: RoomStore,
    private val uscStore: UserConnectionStore,
) {
    fun receiveChat(request: NewChatRequest, session: Session) {
        val roomName = request.roomName
        val colour = uscStore.findForName(session.name)?.colour ?: "black"

        if (roomName == null) {
            val message = OnlineMessage(colour, session.name, request.message)
            notifyUsers(uscStore.getAll(), roomName, message)
        } else {
            val room =
                roomStore.findForName(roomName)
                    ?: throw ClientException(
                        HttpStatusCode.BadRequest,
                        INVALID_ROOM_ID,
                        "No room exists for name $roomName",
                    )

            val actualColour = room.getColourForPlayer(session.name)
            val message = OnlineMessage(actualColour, session.name, request.message)
            room.addToChatHistory(message)

            val uscs = uscStore.getAllForNames(room.allUsersInRoom)
            notifyUsers(uscs, roomName, message)
        }
    }

    private fun notifyUsers(uscs: List<UserConnection>, roomName: String?, message: OnlineMessage) {
        val chatMessage = jsonMapper.writeValueAsString(NewChatMessage(roomName, message))

        server.sendViaNotificationSocket(uscs, chatMessage, XmlConstants.SOCKET_NAME_CHAT)
    }
}
