package http.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import http.ClientMessageType

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "messageType",
)
@JsonSubTypes(JsonSubTypes.Type(value = LobbyResponse::class, name = "LOBBY"))
sealed class ClientMessage {
    abstract val messageType: ClientMessageType
}
