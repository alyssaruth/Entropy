package types

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@JsonSerialize(using = StringMicrotypeSerializer::class)
@JsonDeserialize(using = StringMicrotypeDeserializer::class)
open class StringMicrotype(val value: String) {
    override fun equals(other: Any?) =
        other is StringMicrotype && this.javaClass == other.javaClass && this.value == other.value

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = value
}

class StringMicrotypeSerializer : JsonSerializer<StringMicrotype>() {
    override fun serialize(
        var1: StringMicrotype,
        gen: JsonGenerator,
        serializers: SerializerProvider
    ) {
        gen.writeString(var1.value)
    }
}

class StringMicrotypeDeserializer : JsonDeserializer<StringMicrotype>() {
    override fun deserialize(p0: JsonParser, p1: DeserializationContext) = StringMicrotype(p0.text)
}
