package routes.lobby

import http.dto.LobbyResponse

class LobbyService {
    fun getLobby(): LobbyResponse {
        return LobbyResponse(emptyList(), emptyList())
    }
}
