package strategy

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "strategyType",
)
@JsonSubTypes(
    JsonSubTypes.Type(value = InBuiltStrategy::class, name = "IN_BUILT"),
    JsonSubTypes.Type(value = ApiStrategy::class, name = "API"),
)
interface IStrategy {
    val name: String
}
