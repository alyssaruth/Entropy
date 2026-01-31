package strategy

import com.fasterxml.jackson.module.kotlin.readValue
import game.GameMode
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import util.AbstractClientTest
import utils.CoreGlobals

class ApiStrategyTest : AbstractClientTest() {
    @Test
    fun `Should serialise and deserialise correctly`() {
        val action: IStrategy = ApiStrategy("Alyssa", listOf(GameMode.Entropy), 400)
        val json = CoreGlobals.jsonMapper.writeValueAsString(action)
        val deserialized = CoreGlobals.jsonMapper.readValue(json, IStrategy::class.java)
        deserialized shouldBe action
    }

    @Test
    fun `Should serialise and deserialise a list of ApiStrategies`() {
        val strats =
            listOf(
                ApiStrategy("Strategy One", listOf(GameMode.Entropy), 8080),
                ApiStrategy("Strategy Two", listOf(GameMode.Entropy, GameMode.Vectropy), 9090),
            )

        val json = strats.toJsonString()

        val deserialized = CoreGlobals.jsonMapper.readValue<List<IStrategy>>(json)
        deserialized shouldBe strats
    }
}
