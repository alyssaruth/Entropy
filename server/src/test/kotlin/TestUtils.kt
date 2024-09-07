import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.kotest.matchers.shouldBe

infix fun String.shouldMatchJson(expected: String) {
    val mapper = jacksonObjectMapper()

    val parsed = mapper.readValue<JsonNode>(this)
    val expectedParsed = mapper.readValue<JsonNode>(expected)

    parsed shouldBe expectedParsed
}
