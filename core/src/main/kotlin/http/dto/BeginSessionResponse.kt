package http.dto

import java.util.*

data class BeginSessionResponse(val name: String, val sessionId: UUID, val lobby: LobbyResponse)
