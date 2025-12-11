package strategy

import com.fasterxml.jackson.core.type.TypeReference
import game.GameMode
import java.util.UUID
import utils.CoreGlobals

data class ApiStrategy(
    override val name: String,
    val supportedModes: List<GameMode>,
    val port: Int,
    val lastError: String? = null,
    val id: UUID = UUID.randomUUID(),
) : IStrategy

/**
 * Can't just write using the vanilla mapper because of type erasure problems :(
 * https://stackoverflow.com/questions/34193177/why-does-jackson-polymorphic-serialization-not-work-in-lists
 */
fun List<ApiStrategy>.toJsonString(): String {
    val writer = CoreGlobals.jsonMapper.writerFor(object : TypeReference<List<ApiStrategy>>() {})

    return writer.writeValueAsString(this)
}
