package http

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import types.StringMicrotype
import types.StringMicrotypeDeserializer

@JsonDeserialize(using = ClientErrorCodeDeserializer::class)
class ClientErrorCode(value: String) : StringMicrotype(value)

private class ClientErrorCodeDeserializer :
    StringMicrotypeDeserializer<ClientErrorCode>(::ClientErrorCode)
