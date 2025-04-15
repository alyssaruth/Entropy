package game

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "actionType",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = ChallengeAction::class, name = "CHALLENGE"),
    JsonSubTypes.Type(value = IllegalAction::class, name = "ILLEGAL"),
    JsonSubTypes.Type(value = LeaveAction::class, name = "LEAVE"),
)
abstract class PlayerAction {
    abstract val playerName: String
}
