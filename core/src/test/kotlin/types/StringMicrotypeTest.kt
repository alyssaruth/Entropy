package types

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import testCore.AbstractTest

class StringMicrotypeTest : AbstractTest() {
    @Test
    fun `Should serialize and deserialize correctly`() {
        val microtype = TestMicrotype("testValue")

        val serialized = ObjectMapper().writeValueAsString(microtype)
        serialized shouldBe "\"testValue\""

        val deserialized = ObjectMapper().readValue(serialized, TestMicrotype::class.java)
        deserialized shouldBe microtype
    }

    @JsonDeserialize(using = TestMicrotypeDeserializer::class)
    private class TestMicrotype(value: String) : StringMicrotype(value)

    private class TestMicrotypeDeserializer :
        StringMicrotypeDeserializer<TestMicrotype>(::TestMicrotype)
}
