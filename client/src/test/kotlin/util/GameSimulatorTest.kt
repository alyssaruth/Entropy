package util

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import makeSimulationParams
import org.junit.jupiter.api.Test

class GameSimulatorTest : AbstractClientTest() {
    @Test
    fun `Should select the next player correctly based on play order`() {
        val simulator = GameSimulator(makeSimulationParams())
        simulator.playOrder = listOf(3, 0, 2, 1)
        simulator.opponentZero.isEnabled = true
        simulator.opponentOne.isEnabled = true
        simulator.opponentTwo.isEnabled = true
        simulator.opponentThree.isEnabled = true

        simulator.nextPlayer(simulator.opponentZero) shouldBe simulator.opponentTwo
        simulator.nextPlayer(simulator.opponentTwo) shouldBe simulator.opponentOne
        simulator.nextPlayer(simulator.opponentOne) shouldBe simulator.opponentThree
        simulator.nextPlayer(simulator.opponentThree) shouldBe simulator.opponentZero
    }

    @Test
    fun `Should skip over disabled players`() {
        val simulator = GameSimulator(makeSimulationParams())
        simulator.playOrder = listOf(1, 3, 0, 2)
        simulator.opponentZero.isEnabled = true
        simulator.opponentOne.isEnabled = false
        simulator.opponentTwo.isEnabled = true
        simulator.opponentThree.isEnabled = true

        simulator.nextPlayer(simulator.opponentZero) shouldBe simulator.opponentTwo
        simulator.nextPlayer(simulator.opponentTwo) shouldBe simulator.opponentThree
        simulator.nextPlayer(simulator.opponentThree) shouldBe simulator.opponentZero
    }

    @Test
    fun `Should throw an error if all other players are disabled`() {
        val simulator = GameSimulator(makeSimulationParams())
        simulator.playOrder = listOf(1, 3, 0, 2)
        simulator.opponentZero.isEnabled = true
        simulator.opponentOne.isEnabled = false
        simulator.opponentTwo.isEnabled = false
        simulator.opponentThree.isEnabled = false

        shouldThrow<Exception> { simulator.nextPlayer(simulator.opponentZero) }
    }
}
