package types

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import utils.InjectedThings.logger

@JsonSerialize(using = StringMicrotypeSerializer::class)
abstract class StringMicrotype(val value: String) {
    override fun equals(other: Any?) =
        other is StringMicrotype && this.javaClass == other.javaClass && this.value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value
}

class StringMicrotypeSerializer : JsonSerializer<StringMicrotype>() {
    override fun serialize(
        var1: StringMicrotype,
        gen: JsonGenerator,
        serializers: SerializerProvider,
    ) {
        gen.writeString(var1.value)
    }
}

abstract class StringMicrotypeDeserializer<E : StringMicrotype>(
    private val constructor: (String) -> E
) : JsonDeserializer<E>() {
    override fun deserialize(p0: JsonParser, p1: DeserializationContext) =
        try {
            constructor.invoke(p0.text)
        } catch (t: Throwable) {
            logger.error("reflect.error", "Failed to instantiate ${javaClass.simpleName}", t)
            null
        }
}
