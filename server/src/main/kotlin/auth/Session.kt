package auth

import java.util.UUID
import store.IHasId

data class Session(
    override val id: UUID,
    val name: String,
    val ip: String,
    val achievementCount: Int,
    val apiVersion: Int,
) : IHasId<UUID>
