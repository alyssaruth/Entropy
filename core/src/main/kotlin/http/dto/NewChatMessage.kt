package http.dto

data class NewChatMessage(val roomName: String?, val message: OnlineMessage) : ClientMessage()
