package http

import http.dto.ClientMessage
import http.dto.LobbyMessage
import http.dto.NewChatMessage
import online.screen.EntropyLobby
import screen.ScreenCache
import utils.CoreGlobals
import utils.CoreGlobals.logger

class WebSocketReceiver {
    fun receiveMessage(rawMessage: String) {
        val clientMessage = deserializeClientMessage(rawMessage)
        logger.info(
            "serverMessage",
            "Received server message of type ${clientMessage::class.simpleName}",
        )

        when (clientMessage) {
            is LobbyMessage -> handleLobbyResponse(clientMessage)
            is NewChatMessage -> handleNewChatResponse(clientMessage)
        }
    }

    fun canHandleMessage(rawMessage: String) =
        try {
            deserializeClientMessage(rawMessage)
            true
        } catch (e: Exception) {
            logger.info(
                "legacyNotification",
                "Received old-style message via WebSocket: ${e.message}",
                "messageBody" to rawMessage,
            )
            false
        }

    private fun deserializeClientMessage(rawMessage: String): ClientMessage =
        CoreGlobals.jsonMapper.readValue(rawMessage, ClientMessage::class.java)

    private fun handleLobbyResponse(clientMessage: LobbyMessage) {
        ScreenCache.get<EntropyLobby>().syncLobby(clientMessage)
    }

    private fun handleNewChatResponse(clientMessage: NewChatMessage) {
        val chatPanel =
            ScreenCache.get<EntropyLobby>().getChatPanelForRoomName(clientMessage.roomName)
        chatPanel.updateChatBox(clientMessage.message)
    }
}
