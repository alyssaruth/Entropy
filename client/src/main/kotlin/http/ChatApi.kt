package http

import http.dto.NewChatRequest
import kong.unirest.HttpMethod

class ChatApi(private val httpClient: HttpClient) {
    fun sendChat(message: String, roomName: String?) {
        httpClient.doCall<Unit>(HttpMethod.PUT, Routes.CHAT, NewChatRequest(message, roomName))
    }
}
