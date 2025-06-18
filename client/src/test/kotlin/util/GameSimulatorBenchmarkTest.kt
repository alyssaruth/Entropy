package util

import game.GameMode
import org.junit.jupiter.api.Test
import screen.ScreenCache.get
import screen.SimulationDialog
import testCore.makeGameSettings

class GameSimulatorBenchmarkTest : AbstractClientTest() {
    @Test
    fun `Entropy - Hard vs Easy`() {
        val settings = makeGameSettings(mode = GameMode.Entropy)
        val params =
            SimulationParams(
                settings,
                opponentTwoEnabled = false,
                opponentThreeEnabled = false,
                CpuStrategies.STRATEGY_BASIC,
                CpuStrategies.STRATEGY_EV,
                "",
                "",
                enableLogging = false,
                randomiseOrder = true,
                forceStart = false,
            )

        val simulator = GameSimulator(params)
        repeat(11111) { simulator.startNewGame(it) }

        val results = get(SimulationDialog::class.java).getResults(0)
        println(results.getWinRate(11111))
    }
}
