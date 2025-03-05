package routes.chat

import http.Routes
import http.dto.NewChatRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import routes.requiresSession
import util.ServerGlobals

class ChatController {
    fun installRoutes(application: Application) {
        application.routing { put(Routes.CHAT) { receiveChatMessage(call) } }
    }

    private suspend fun receiveChatMessage(call: ApplicationCall) =
        requiresSession(call) { session ->
            val request = call.receive<NewChatRequest>()
            ServerGlobals.chatService.receiveChat(request, session)
            call.respond(HttpStatusCode.Created)
        }
}
