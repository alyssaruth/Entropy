package auth

import java.util.UUID

data class Session(val id: UUID, val name: String, val ip: String, val apiVersion: Int)
