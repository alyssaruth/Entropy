package http.dto

import java.util.UUID

data class SitDownRequest(val roomId: UUID, val playerNumber: Int)
