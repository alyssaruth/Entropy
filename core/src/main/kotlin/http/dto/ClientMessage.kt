package http.dto

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "messageType",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = LobbyMessage::class, name = "LOBBY"),
    JsonSubTypes.Type(value = NewChatMessage::class, name = "CHAT")
)
abstract class ClientMessage
