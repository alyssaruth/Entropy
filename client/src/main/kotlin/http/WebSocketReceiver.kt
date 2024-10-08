package http

import http.dto.ClientMessage
import http.dto.LobbyResponse
import kong.unirest.JsonObjectMapper
import utils.CoreGlobals.logger

class WebSocketReceiver {
    private val jsonObjectMapper = JsonObjectMapper()

    fun receiveMessage(rawMessage: String) {
        when (val clientMessage = deserializeClientMessage(rawMessage)) {
            is LobbyResponse -> handleLobbyResponse(clientMessage)
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
        jsonObjectMapper.readValue(rawMessage, ClientMessage::class.java)

    private fun handleLobbyResponse(clientMessage: LobbyResponse) {}
}
